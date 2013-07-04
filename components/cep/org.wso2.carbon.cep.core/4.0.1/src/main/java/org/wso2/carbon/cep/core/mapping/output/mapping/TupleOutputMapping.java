/*
 * Copyright 2004,2005 The Apache Software Foundation.
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

package org.wso2.carbon.cep.core.mapping.output.mapping;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.cep.core.exception.CEPEventProcessingException;
import org.wso2.carbon.cep.core.mapping.output.property.TupleOutputProperty;
import org.wso2.carbon.databridge.commons.Attribute;
import org.wso2.carbon.databridge.commons.AttributeType;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.StreamDefinition;

import java.util.ArrayList;
import java.util.List;

public class TupleOutputMapping extends OutputMapping {

    private static final Log log = LogFactory.getLog(TupleOutputMapping.class);

    private StreamDefinition streamDefinition = new StreamDefinition("temp");
    private List<TupleOutputProperty> payloadDataProperties;
    private List<TupleOutputProperty> correlationDataProperties;
    private List<TupleOutputProperty> metaDataProperties;

    public Object convert(Object event) {
        try {
            return buildTupleEvent(event);
        } catch (CEPEventProcessingException e) {
            log.error("Error in accessing information from the output event to build the OM Element " + e);
        }
        return null;
    }

    private Object buildTupleEvent(Object event) throws CEPEventProcessingException {
        Event newEvent = new Event();
//        if (streamId == null) {
//            log.error("No streamId found for " + streamName + ":" + streamVersion);
//            return null;
//        } else {
//            newEvent.setStreamId(streamId);
//        }
        if (metaDataProperties != null && metaDataProperties.size() > 0) {
            Object[] data = new Object[metaDataProperties.size()];
            for (int i = 0, metaDataPropertiesSize = metaDataProperties.size(); i < metaDataPropertiesSize; i++) {
                data[i] = getPropertyValue(event, metaDataProperties.get(i).getValueOf());
            }
            (newEvent).setMetaData(data);
        }
        if (correlationDataProperties != null && correlationDataProperties.size() > 0) {
            Object[] data = new Object[correlationDataProperties.size()];
            for (int i = 0, metaDataPropertiesSize = correlationDataProperties.size(); i < metaDataPropertiesSize; i++) {
                data[i] = getPropertyValue(event, correlationDataProperties.get(i).getValueOf());
            }
            (newEvent).setCorrelationData(data);
        }
        //payload data has to processed last as all the current data is in the payload
        if (payloadDataProperties != null && payloadDataProperties.size() > 0) {
            Object[] data = new Object[payloadDataProperties.size()];
            for (int i = 0, metaDataPropertiesSize = payloadDataProperties.size(); i < metaDataPropertiesSize; i++) {
                data[i] = getPropertyValue(event, payloadDataProperties.get(i).getValueOf());
            }
            (newEvent).setPayloadData(data);
        }
        return new Object[]{newEvent,streamDefinition};
    }

    public List<TupleOutputProperty> getPayloadDataProperties() {
        return payloadDataProperties;
    }

    public void setPayloadDataProperties(List<TupleOutputProperty> payloadDataProperties) {
        this.payloadDataProperties = payloadDataProperties;
        streamDefinition.setPayloadData(constructAttributeList(payloadDataProperties));
    }

    public List<TupleOutputProperty> getCorrelationDataProperties() {
        return correlationDataProperties;
    }

    public void setCorrelationDataProperties(List<TupleOutputProperty> correlationDataProperties) {
        this.correlationDataProperties = correlationDataProperties;
        streamDefinition.setCorrelationData(constructAttributeList(correlationDataProperties));

    }

    public List<TupleOutputProperty> getMetaDataProperties() {
        return metaDataProperties;
    }

    public void setMetaDataProperties(List<TupleOutputProperty> metaDataProperties) {
        this.metaDataProperties = metaDataProperties;
        streamDefinition.setMetaData(constructAttributeList(metaDataProperties));

    }

    private List<Attribute> constructAttributeList(List<TupleOutputProperty> dataProperties) {
        List<Attribute> attributeList = new ArrayList<Attribute>();
        for (TupleOutputProperty property : dataProperties) {
            attributeList.add(new Attribute(property.getName(), convertAttributeType(property.getType())));
        }
        return attributeList;
    }

    private AttributeType convertAttributeType(String type) {
        if (String.class.getName().equals(type)) {
            return AttributeType.STRING;
        } else if (Integer.class.getName().equals(type)) {
            return AttributeType.INT;
        } else if (Float.class.getName().equals(type)) {
            return AttributeType.FLOAT;
        } else if (Long.class.getName().equals(type)) {
            return AttributeType.LONG;
        } else if (Double.class.getName().equals(type)) {
            return AttributeType.DOUBLE;
        } else if (Boolean.class.getName().equals(type)) {
            return AttributeType.BOOL;
        } else {
            return AttributeType.STRING;
        }
    }

}
