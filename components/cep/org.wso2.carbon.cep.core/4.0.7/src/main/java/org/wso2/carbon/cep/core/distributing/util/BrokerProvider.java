package org.wso2.carbon.cep.core.distributing.util;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class BrokerProvider {

    private final static String fileLocation = "/home/isuru/Documents/FYP/workingrepone/siddhi-lb/cep/org.wso2.carbon.cep.core/4.0.7/src/main/resources";


    public static Map<String, BrokerConfig> getBrokers() {
        Map<String, BrokerConfig> map = new HashMap<String, BrokerConfig>();

        try {
            FileInputStream fstream = new FileInputStream(fileLocation + "nodedetails.txt");
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                String[] array = strLine.split("\\|");
                BrokerConfig broker = new BrokerConfig(array[0], array[1], array[2], array[3], array[4]);
                map.put(broker.getIp(), broker);
            }

            in.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }


        return map;
    }


}






















