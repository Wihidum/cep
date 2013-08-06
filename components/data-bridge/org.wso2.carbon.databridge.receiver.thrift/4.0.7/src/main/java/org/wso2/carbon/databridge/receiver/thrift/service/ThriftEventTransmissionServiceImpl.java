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

package org.wso2.carbon.databridge.receiver.thrift.service;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.wso2.carbon.cep.wihidum.loadbalancer.SiddhiLBManager;
import org.wso2.carbon.cep.wihidum.loadbalancer.conf.LoadBalancerConfiguration;
import org.wso2.carbon.databridge.commons.Credentials;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.commons.exception.*;
import org.wso2.carbon.databridge.commons.thrift.data.ThriftEventBundle;
import org.wso2.carbon.databridge.commons.thrift.exception.*;
import org.wso2.carbon.databridge.commons.thrift.service.general.ThriftEventTransmissionService;
import org.wso2.carbon.databridge.core.*;
import org.wso2.carbon.databridge.core.Utils.AgentSession;
import org.wso2.carbon.databridge.core.internal.authentication.Authenticator;
import org.wso2.carbon.databridge.core.internal.utils.DataBridgeConstants;
import org.wso2.carbon.databridge.receiver.thrift.converter.ThriftEventConverter;

import javax.xml.namespace.QName;
import java.util.List;


/**
 * The client implementation for ThriftDataReceiverService
 */
public class ThriftEventTransmissionServiceImpl implements
        ThriftEventTransmissionService.Iface {
    private static Logger logger = Logger.getLogger(ThriftEventTransmissionServiceImpl.class);
    private DataBridgeReceiverService dataBridgeReceiverService;
    private EventConverter eventConverter = new ThriftEventConverter();
    private SiddhiLBManager siddhiLBManager = SiddhiLBManager.getSiddhiLBManager();





    public ThriftEventTransmissionServiceImpl(DataBridgeReceiverService dataBridgeReceiverService) {
        this.dataBridgeReceiverService = dataBridgeReceiverService;
    }

    @Override
    public String defineStream(String sessionId, String streamDefinition)
            throws TException, ThriftSessionExpiredException,
            ThriftDifferentStreamDefinitionAlreadyDefinedException,
            ThriftMalformedStreamDefinitionException {
        try {
            return dataBridgeReceiverService.defineStream(sessionId, streamDefinition);
        } catch (DifferentStreamDefinitionAlreadyDefinedException e) {
            throw new ThriftDifferentStreamDefinitionAlreadyDefinedException(e.getErrorMessage());
        } catch (MalformedStreamDefinitionException e) {
            throw new ThriftMalformedStreamDefinitionException(e.getErrorMessage());
        } catch (SessionTimeoutException e) {
            throw new ThriftSessionExpiredException(e.getErrorMessage());
        }
    }

    @Override
    public String findStreamId(String sessionId, String streamName, String streamVersion)
            throws ThriftNoStreamDefinitionExistException, ThriftSessionExpiredException,
            TException {
        try {
            String streamDefinition = dataBridgeReceiverService.findStreamId(sessionId, streamName, streamVersion);
            if (streamDefinition == null) {
                //this is used as Thrift cannot send null values
                throw new ThriftNoStreamDefinitionExistException("Stream definition not exist for " + streamName + " " + streamVersion);
            }
            return streamDefinition;
        } catch (SessionTimeoutException e) {
            throw new ThriftSessionExpiredException(e.getErrorMessage());
        }
    }

    @Override
    public boolean deleteStreamById(String sessionId, String streamId)
            throws ThriftSessionExpiredException,
            TException {
        try {
            return dataBridgeReceiverService.deleteStream(sessionId, streamId);
        } catch (SessionTimeoutException e) {
            throw new ThriftSessionExpiredException(e.getErrorMessage());
        }
    }

    @Override
    public boolean deleteStreamByNameVersion(String sessionId, String streamName, String streamVersion)
            throws ThriftSessionExpiredException,
            TException {
        try {
            return dataBridgeReceiverService.deleteStream(sessionId, streamName, streamVersion);
        } catch (SessionTimeoutException e) {
            throw new ThriftSessionExpiredException(e.getErrorMessage());
        }
    }

    public void publish(ThriftEventBundle eventBundle)
            throws ThriftUndefinedEventTypeException, ThriftSessionExpiredException, TException {

        if(siddhiLBManager.loadBalancerConfiguration().isLoadbalanceron()){
           AgentSession agentSession =    ((DataBridge)dataBridgeReceiverService).getAgentSession(eventBundle.getSessionId());
           StreamTypeHolder streamTypeHolder = new StreamTypeHolder(agentSession.getDomainName());
           List<StreamDefinition> streamDefinitionList = ((DataBridge) dataBridgeReceiverService).getAllStreamDefinitions(agentSession.getCredentials());
            for(StreamDefinition streamDefinition:streamDefinitionList){
                streamTypeHolder.putStreamDefinition(streamDefinition);
                siddhiLBManager.addStreamDefinition(streamDefinition);
            }
            List<Event> eventList =eventConverter.toEventList(eventBundle,streamTypeHolder);
             siddhiLBManager.receiveEventBundle(eventList);
        }else{
        try {
            dataBridgeReceiverService.publish(eventBundle, eventBundle.getSessionId(), eventConverter);

        } catch (UndefinedEventTypeException e) {
            throw new ThriftUndefinedEventTypeException(e.getErrorMessage());
        } catch (SessionTimeoutException e) {
            throw new ThriftSessionExpiredException(e.getErrorMessage());
      }

    }
    }

}
