/*
*  Copyright (c) 2005-2012, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.databridge.core;

import org.wso2.carbon.databridge.commons.AttributeType;

/**
 * Class to hold attribute type order array and size of attributes.
 */

public class AttributeComposite {
    private AttributeType[][] attributeTypes;
    private int attributeSize;

    public AttributeComposite(AttributeType[][] attributeTypes) {
        this.attributeTypes = attributeTypes;
        this.attributeSize = getSize(attributeTypes);
    }

    private int getSize(AttributeType[][] attributeTypes) {
        int size = 0;
        if (attributeTypes[0] != null) {
            size += attributeTypes[0].length;
        }
        if (attributeTypes[1] != null) {
            size += attributeTypes[1].length;
        }
        if (attributeTypes[2] != null) {
            size += attributeTypes[2].length;
        }
        return size;
    }

    public int getAttributeSize() {
        return attributeSize;
    }

    public void setAttributeSize(int attributeSize) {
        this.attributeSize = attributeSize;
    }

    public AttributeType[][] getAttributeTypes() {
        return attributeTypes;
    }

    public void setAttributeTypes(AttributeType[][] attributeTypes) {
        this.attributeTypes = attributeTypes;
    }
}
