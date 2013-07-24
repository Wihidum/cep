package org.wso2.carbon.cep.wihidum.loadbalancer.nodemanager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NodeProvider {
    private static List<Node> inputNodeList = new ArrayList<Node>();
    private static List<Node> outputNodeList = new ArrayList<Node>();
    private static File filePath = new File(".");


//    public static List<Node> getInputNodeListFromQuery(Query query) {
//
//        Stream stream = query.getInputStream();
//        QueryEventStream queryEventStream = ((BasicStream) stream).getQueryEventStream();
//        StreamDefinition streamDefinition = queryEventStream.getInStreamDefinition();
//        String streamId = streamDefinition.getStreamId();
//        String ip = streamDefinition.getIp();
//        String port = "9673";
//        inputNodeList.add(new Node(ip, port, streamId));
//        return inputNodeList;
//    }
//
//    public static List<Node> getOutputNodeListFromQuery(Query query) {
//
//        OutStream stream = query.getOutputStream();
//        String streamId = stream.getStreamId();
//        List<String> ipList = stream.getIp();
//        String port = "9673";
//        for (String ip : ipList) {
//            outputNodeList.add(new Node(ip, port, streamId));
//        }
//        return outputNodeList;
//    }

}
