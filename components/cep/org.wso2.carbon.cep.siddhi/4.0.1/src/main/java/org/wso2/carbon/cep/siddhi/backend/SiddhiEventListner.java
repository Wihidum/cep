/*
 * Copyright 2004,2012 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.cep.siddhi.backend;

import org.wso2.carbon.cep.core.listener.CEPEventListener;
import org.wso2.carbon.databridge.commons.AttributeType;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.QueryCallback;
import org.wso2.siddhi.query.api.definition.Attribute;
import org.wso2.siddhi.query.api.definition.StreamDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class receives events from the siddhi cep framework and
 * send the messages through QueryResultListner
 */
public class SiddhiEventListner extends QueryCallback {

    private CEPEventListener cepEventListener;
    private StreamDefinition siddhiStreamDefinition;
    private org.wso2.carbon.databridge.commons.StreamDefinition tupleStreamDefinition;
    private String[] names;

    public SiddhiEventListner(StreamDefinition streamDefinition,
                              CEPEventListener cepEventListener) {
        this.cepEventListener = cepEventListener;
        this.siddhiStreamDefinition = streamDefinition;
        this.names = streamDefinition.getAttributeNameArray();
        org.wso2.carbon.databridge.commons.StreamDefinition tupleStreamDefinition = new org.wso2.carbon.databridge.commons.StreamDefinition(streamDefinition.getStreamId());
        List<org.wso2.carbon.databridge.commons.Attribute> attributeList = new ArrayList<org.wso2.carbon.databridge.commons.Attribute>();
        for (Attribute siddhiAttribute : streamDefinition.getAttributeList()) {
            attributeList.add(new org.wso2.carbon.databridge.commons.Attribute(siddhiAttribute.getName(), attributeTypeConverter(siddhiAttribute.getType())));
        }
        tupleStreamDefinition.setPayloadData(attributeList);
        this.tupleStreamDefinition = tupleStreamDefinition;
        cepEventListener.defineStream(tupleStreamDefinition);
    }

    private AttributeType attributeTypeConverter(Attribute.Type type) {
        switch (type) {

            case STRING:
                return AttributeType.STRING;
            case INT:
                return AttributeType.INT;
            case LONG:
                return AttributeType.LONG;
            case FLOAT:
                return AttributeType.FLOAT;
            case DOUBLE:
                return AttributeType.DOUBLE;
            case BOOL:
                return AttributeType.BOOL;
        }
        return null;
    }


    private List<HashMap<String, Object>> toMap(Event[] events) {
        List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>(events.length);
        for (Event event : events) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            Object[] values = event.getData();
            for (int i = 0; i < names.length; i++) {
                map.put(names[i], values[i]);
            }
            list.add(map);
        }
        return list;
    }

    @Override
    public void receive(long timestamp, Event[] inEvents, Event[] removeEvents) {
        if (inEvents != null) {
            cepEventListener.onComplexEvent(toMap(inEvents));
        }
        if (removeEvents != null) {
            cepEventListener.onComplexEvent(toMap(removeEvents));
        }
//        if (inEvents != null) {
//            cepEventListener.onComplexEvent(toTuple(inEvents));
//        }
//        if (removeEvents != null) {
//            cepEventListener.onComplexEvent(toTuple(removeEvents));
//        }
    }

    private List toTuple(Event[] events) {
        List<org.wso2.carbon.databridge.commons.Event> outputEvents = new ArrayList<org.wso2.carbon.databridge.commons.Event>();
        for (Event event : events) {
            outputEvents.add(new org.wso2.carbon.databridge.commons.Event(siddhiStreamDefinition.getStreamId(), event.getTimeStamp(), null, null, event.getData()));

        }
        return outputEvents;  //To change body of created methods use File | Settings | File Templates.
    }
}
