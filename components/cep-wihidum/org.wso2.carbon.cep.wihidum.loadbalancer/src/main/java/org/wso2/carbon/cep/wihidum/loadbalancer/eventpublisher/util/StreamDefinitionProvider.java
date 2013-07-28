package org.wso2.carbon.cep.wihidum.loadbalancer.eventpublisher.util;


import org.wso2.carbon.databridge.commons.StreamDefinition;

import java.util.ArrayList;
import java.util.List;

public class StreamDefinitionProvider {

    private static List<StreamDefinition> streamDefinitions = new ArrayList<StreamDefinition>();


    public static void addStreamDefinition(StreamDefinition streamDefinition) {
        boolean add = true;
        for (StreamDefinition streamDefinitionOne : streamDefinitions) {
            if (streamDefinition.toString().equals(streamDefinitionOne.toString())) {
                add = false;
            }
        }
        if (add) {
            streamDefinitions.add(streamDefinition);
        }


    }

    public static List<StreamDefinition> getStreamDefinitionList() {
        return streamDefinitions;
    }


}
