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
package org.wso2.siddhi.query.api.definition;

import org.wso2.siddhi.query.api.ExecutionPlan;
import org.wso2.siddhi.query.api.exception.AttributeAlreadyExistException;
import org.wso2.siddhi.query.api.exception.AttributeNotExistException;

import java.util.ArrayList;
import java.util.List;

public class StreamDefinition implements ExecutionPlan {

    String streamId;
    String ip ;
    List<Attribute> attributeList = new ArrayList<Attribute>();

    public StreamDefinition name(String streamId) {
        this.streamId = streamId;
        return this;
    }

    public StreamDefinition ip(String ip) {
        this.ip = ip;
        return this;
    }

    public StreamDefinition attribute(String attributeName, Attribute.Type type) {
        checkAttribute(attributeName);
        this.attributeList.add(new Attribute(attributeName, type));
        return this;
    }

    private void checkAttribute(String attributeName) {
        for (Attribute attribute : attributeList) {
            if (attribute.getName().equals(attributeName)) {
                throw new AttributeAlreadyExistException(attributeName + " is already defined for with type " + attribute.getType() + " for " + streamId);
            }
        }
    }

    public String getStreamId() {
        return streamId;
    }
    public String getIp(){
        return ip;
    }

    public List<Attribute> getAttributeList() {
        return attributeList;
    }

    public Attribute.Type getAttributeType(String attributeName) {
        for (Attribute attribute : attributeList) {
            if (attribute.getName().equals(attributeName)) {
                return attribute.getType();
            }
        }
        throw new AttributeNotExistException("Cannot find attribute type as "+attributeName+" dose not exist");
    }

    public int getAttributePosition(String attributeName) {
        for (int i = 0, attributeListSize = attributeList.size(); i < attributeListSize; i++) {
            Attribute attribute = attributeList.get(i);
            if (attribute.getName().equals(attributeName)) {
                return i;
            }
        }
        throw new AttributeNotExistException("Cannot get attribute position as "+attributeName+" dose not exist");
    }

    @Override
    public String toString() {
        return "StreamDefinition{" +
               "streamId='" + streamId + '\'' +
               ",ip='"+ip+'\''+
               ", attributeList=" + attributeList +
               '}';
    }

    public String[] getAttributeNameArray(){
        int attributeListSize = attributeList.size();
       String[] attributeNameArray= new String[attributeListSize];
        for (int i = 0; i < attributeListSize; i++) {
            attributeNameArray[i]= attributeList.get(i).getName();
        }
        return attributeNameArray;
    }
}
