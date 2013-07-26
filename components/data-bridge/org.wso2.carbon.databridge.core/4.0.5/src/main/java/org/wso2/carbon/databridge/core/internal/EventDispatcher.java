/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.databridge.core.internal;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.databridge.commons.Credentials;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.commons.exception.DifferentStreamDefinitionAlreadyDefinedException;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;
import org.wso2.carbon.databridge.commons.exception.UndefinedEventTypeException;
import org.wso2.carbon.databridge.commons.utils.EventDefinitionConverterUtils;
import org.wso2.carbon.databridge.core.*;
import org.wso2.carbon.databridge.core.Utils.AgentSession;
import org.wso2.carbon.databridge.core.Utils.EventComposite;
import org.wso2.carbon.databridge.core.conf.DataBridgeConfiguration;
import org.wso2.carbon.databridge.core.definitionstore.AbstractStreamDefinitionStore;
import org.wso2.carbon.databridge.core.exception.StreamDefinitionNotFoundException;
import org.wso2.carbon.databridge.core.exception.StreamDefinitionStoreException;
import org.wso2.carbon.databridge.core.internal.queue.EventQueue;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dispactches events  and their definitions subscribers
 */
public class EventDispatcher {

    public static final String HACK_DOMAIN_CONSTANT = "-1234";
    private List<AgentCallback> subscribers = new ArrayList<AgentCallback>();
    private List<RawDataAgentCallback> rawDataSubscribers = new ArrayList<RawDataAgentCallback>();
    private AbstractStreamDefinitionStore streamDefinitionStore;
    private Map<String, StreamTypeHolder> streamTypeCache = new ConcurrentHashMap<String, StreamTypeHolder>();
    private EventQueue eventQueue;

    private static final Log log = LogFactory.getLog(EventDispatcher.class);


    public EventDispatcher(AbstractStreamDefinitionStore streamDefinitionStore,
                           DataBridgeConfiguration dataBridgeConfiguration) {
        this.eventQueue = new EventQueue(subscribers, rawDataSubscribers, dataBridgeConfiguration);
        this.streamDefinitionStore = streamDefinitionStore;
    }

    public void addCallback(AgentCallback agentCallback) {
        subscribers.add(agentCallback);
    }

    /**
     * Add thrift subscribers
     * @param agentCallback
     */
    public void addCallback(RawDataAgentCallback agentCallback) {
        rawDataSubscribers.add(agentCallback);
    }

    public String defineStream(String streamDefinition, AgentSession agentSession)
            throws
            MalformedStreamDefinitionException,
            DifferentStreamDefinitionAlreadyDefinedException, StreamDefinitionStoreException {
        synchronized (EventDispatcher.class) {
            StreamDefinition newStreamDefinition = EventDefinitionConverterUtils.convertFromJson(streamDefinition);

            StreamDefinition existingStreamDefinition;
            try {
                existingStreamDefinition = streamDefinitionStore.getStreamDefinition(agentSession.getCredentials(), newStreamDefinition.getName(), newStreamDefinition.getVersion());
                if (!existingStreamDefinition.equals(newStreamDefinition)) {
                    throw new DifferentStreamDefinitionAlreadyDefinedException("Similar event stream for " + newStreamDefinition + " with the same name and version already exist: " + streamDefinitionStore.getStreamDefinition(agentSession.getCredentials(), newStreamDefinition.getName(), newStreamDefinition.getVersion()));
                }
                newStreamDefinition = existingStreamDefinition;
                updateStreamCacheIfNotStreamDefnExisting(agentSession.getDomainName(), newStreamDefinition,
                         agentSession.getCredentials());
            } catch (StreamDefinitionNotFoundException e) {
                streamDefinitionStore.saveStreamDefinition(agentSession.getCredentials(), newStreamDefinition);
                updateStreamTypeCache(agentSession.getDomainName(), newStreamDefinition);
            }

            for (AgentCallback agentCallback : subscribers) {
                agentCallback.definedStream(newStreamDefinition, agentSession.getCredentials());
            }
            return newStreamDefinition.getStreamId();
        }
    }

    private void updateStreamTypeCache(String domainName,
                                       StreamDefinition streamDefinition) {
        StreamTypeHolder streamTypeHolder;
        // this will occur only outside of carbon (ex: Siddhi)
//        if (domainName == null) {
//            domainName = HACK_DOMAIN_CONSTANT;
//        }

        synchronized (EventDispatcher.class) {
            if (streamTypeCache.containsKey(domainName)) {
                streamTypeHolder = streamTypeCache.get(domainName);
            } else {
                streamTypeHolder = new StreamTypeHolder(domainName);
            }

            updateStreamTypeHolder(streamTypeHolder, streamDefinition);
            if (log.isTraceEnabled()) {
                String logMsg = "Event Stream Type getting updated : ";
                logMsg += "Event stream holder for domain name : " + domainName + " : \n ";
                logMsg += "Meta, Correlation & Payload Data Type Map : ";
                for (Map.Entry entry : streamTypeHolder.getAttributeCompositeMap().entrySet()) {
                    logMsg += "StreamID=" + entry.getKey() + " :  ";
                    logMsg += "Meta= " + Arrays.deepToString(((AttributeComposite) entry.getValue()).getAttributeTypes()[0]) + " :  ";
                    logMsg += "Correlation= " + Arrays.deepToString(((AttributeComposite) entry.getValue()).getAttributeTypes()[1]) + " :  ";
                    logMsg += "Payload= " + Arrays.deepToString(((AttributeComposite) entry.getValue()).getAttributeTypes()[2]) + "\n";
                }
                log.trace(logMsg);
            }


            streamTypeCache.put(domainName, streamTypeHolder);
        }
    }

    private void updateStreamCacheIfNotStreamDefnExisting(String domainName,
                                                          StreamDefinition streamDefinition, Credentials credentials) {
        synchronized (EventDispatcher.class) {
            if (streamTypeCache.containsKey(domainName)) {
                StreamTypeHolder streamTypeHolder = streamTypeCache.get(domainName);
                if (null == streamTypeHolder.getDataType(streamDefinition.getStreamId())) {
                    updateStreamTypeCache(domainName, streamDefinition);
                }
                //else the stream is already in the cache.
            } else {
                if (streamTypeCache.size() == 0) {
                    StreamTypeHolder streamTypeHolder = new StreamTypeHolder(domainName);
                    Collection<StreamDefinition> allStreamDefinitions =
                            streamDefinitionStore.getAllStreamDefinitions(credentials);
                    for (StreamDefinition aStreamDefinition : allStreamDefinitions) {
                        updateStreamTypeHolder(streamTypeHolder, aStreamDefinition);
                        updateStreamTypeCache(credentials.getDomainName(), aStreamDefinition);
                    }
                } else {
                    updateStreamTypeCache(domainName, streamDefinition);
                }
            }
        }
    }

    public void publish(Object eventBundle, AgentSession agentSession,
                        EventConverter eventConverter)
            throws UndefinedEventTypeException {
        try {
            eventQueue.publish(new EventComposite(eventBundle, getStreamDefinitionHolder(agentSession.getCredentials()), agentSession, eventConverter));
        } catch (StreamDefinitionNotFoundException e) {
            throw new UndefinedEventTypeException("No event stream definition exist " + e.getErrorMessage());
        }
    }

    private StreamTypeHolder getStreamDefinitionHolder(Credentials credentials)
            throws StreamDefinitionNotFoundException {
        // this will occur only outside of carbon (ex: Siddhi)

//        String domainName = (credentials.getDomainName() == null) ? HACK_DOMAIN_CONSTANT : credentials.getDomainName();
        StreamTypeHolder streamTypeHolder = streamTypeCache.get(credentials.getDomainName());

        if (log.isTraceEnabled()) {
            log.trace("Retrieving Event Stream Type Cache : " + streamTypeCache);
        }

        if (streamTypeHolder != null) {
            if (log.isDebugEnabled()) {
                String logMsg = "Event stream holder for domain name : " + credentials.getDomainName() + " : \n ";
                logMsg += "Meta, Correlation & Payload Data Type Map : ";
                for (Map.Entry entry : streamTypeHolder.getAttributeCompositeMap().entrySet()) {
                    logMsg += "StreamID=" + entry.getKey() + " :  ";
                    logMsg += "Meta= " + Arrays.deepToString(((AttributeComposite) entry.getValue()).getAttributeTypes()[0]) + " :  ";
                    logMsg += "Correlation= " + Arrays.deepToString(((AttributeComposite) entry.getValue()).getAttributeTypes()[1]) + " :  ";
                    logMsg += "Payload= " + Arrays.deepToString(((AttributeComposite) entry.getValue()).getAttributeTypes()[2]) + "\n";
                }
                log.debug(logMsg);
            }
            return streamTypeHolder;
        } else {
            synchronized (EventDispatcher.class) {
                streamTypeHolder = streamTypeCache.get(credentials.getDomainName());
                if(null == streamTypeHolder){
                streamTypeHolder = new StreamTypeHolder(credentials.getDomainName());
                Collection<StreamDefinition> allStreamDefinitions =
                        streamDefinitionStore.getAllStreamDefinitions(credentials);
                for (StreamDefinition streamDefinition : allStreamDefinitions) {
                    updateStreamTypeHolder(streamTypeHolder, streamDefinition);
                    updateStreamTypeCache(credentials.getDomainName(), streamDefinition);
                }
                }
            }
        }

        if (log.isDebugEnabled()) {
            String logMsg = "Event stream holder for domain name : " + credentials.getDomainName() + " : \n ";
            logMsg += "Meta, Correlation & Payload Data Type Map : ";
            for (Map.Entry entry : streamTypeHolder.getAttributeCompositeMap().entrySet()) {
                logMsg += "StreamID=" + entry.getKey() + " :  ";
                logMsg += "Meta= " + Arrays.deepToString(((AttributeComposite) entry.getValue()).getAttributeTypes()[0]) + " :  ";
                logMsg += "Correlation= " + Arrays.deepToString(((AttributeComposite) entry.getValue()).getAttributeTypes()[1]) + " :  ";
                logMsg += "Payload= " + Arrays.deepToString(((AttributeComposite) entry.getValue()).getAttributeTypes()[2]) + "\n";
            }
            log.debug(logMsg);
        }
        return streamTypeHolder;
    }

    private void updateStreamTypeHolder(StreamTypeHolder streamTypeHolder,
                                        StreamDefinition streamDefinition) {
        streamTypeHolder.putDataType(streamDefinition.getStreamId(),
                EventDefinitionConverterUtils.generateAttributeTypeArray(streamDefinition.getMetaData()),
                EventDefinitionConverterUtils.generateAttributeTypeArray(streamDefinition.getCorrelationData()),
                EventDefinitionConverterUtils.generateAttributeTypeArray(streamDefinition.getPayloadData()));
    }

    public List<AgentCallback> getSubscribers() {
        return subscribers;
    }

    public List<RawDataAgentCallback> getRawDataSubscribers() {
        return rawDataSubscribers;
    }

    public String findStreamId(Credentials credentials, String streamName,
                               String streamVersion)
            throws StreamDefinitionNotFoundException, StreamDefinitionStoreException {
        try {
            return streamDefinitionStore.getStreamId(credentials, streamName, streamVersion);
        } catch (StreamDefinitionNotFoundException e) {
            throw new StreamDefinitionNotFoundException("No event stream definition exist " + e.getErrorMessage());
        }

    }

    public boolean deleteStream(Credentials credentials, String streamId) {
        return streamDefinitionStore.deleteStreamDefinition(credentials, streamId);
    }

    public boolean deleteStream(Credentials credentials, String streamName, String streamVersion) {
        return streamDefinitionStore.deleteStreamDefinition(credentials, streamName, streamVersion);
    }

    private static class StreamTypeCache {


    }
}
