package org.wso2.siddhi.loadbalancer.eventreceiver;

import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.commons.Credentials;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.commons.thrift.utils.HostAddressFinder;
import org.wso2.carbon.databridge.core.AgentCallback;
import org.wso2.carbon.databridge.core.DataBridge;
import org.wso2.carbon.databridge.core.definitionstore.InMemoryStreamDefinitionStore;
import org.wso2.carbon.databridge.core.exception.DataBridgeException;
import org.wso2.carbon.databridge.core.internal.authentication.AuthenticationHandler;
import org.wso2.carbon.databridge.receiver.thrift.internal.ThriftDataReceiver;
import org.wso2.siddhi.loadbalancer.eventdivider.Divider;
import org.wso2.siddhi.loadbalancer.eventdivider.EventRRDivider;
import org.wso2.siddhi.loadbalancer.utils.KeyStoreUtil;

import java.net.SocketException;
import java.util.List;


public class ExternalEventReceiver {
    private static Logger log = Logger.getLogger(ExternalEventReceiver.class);
    private static ThriftDataReceiver thriftDataReceiver;
    private static final ExternalEventReceiver testServer = new ExternalEventReceiver();
    private static String reciverHost = "localhost";


    private void start(int receiverPort) throws DataBridgeException {
        KeyStoreUtil.setKeyStoreParams();
        final Divider divider = new EventRRDivider();
        //  Thread t = new Thread((EventRRDivider)divider);
        //   t.start();
        DataBridge databridge = new DataBridge(new AuthenticationHandler() {
            @Override
            public boolean authenticate(String userName,
                                        String password) {
                return true;// allays authenticate to true

            }
        }, new InMemoryStreamDefinitionStore());

        thriftDataReceiver = new ThriftDataReceiver(receiverPort, databridge);

        databridge.subscribe(new AgentCallback() {


            public void definedStream(StreamDefinition streamDefinition,
                                      Credentials credentials) {
                // log.info("Added StreamDefinition " + streamDefinition);
                // log.info("Again StreamDefinition " + streamDefinition);
            }

            public void removeStream(StreamDefinition streamDefinition,
                                     Credentials credentials) {
                // log.info("Removed StreamDefinition " + streamDefinition);
            }

            @Override
            public void receive(List<Event> eventList, Credentials credentials) {
                //  log.info("eventListSize=" + eventList.size() + " eventList " + eventList + " for username " + credentials.getUsername());

                for (Event event : eventList) {
                    divider.divide(event);
                }
                // log.info("events send to divider");

            }

        });

        try {
            String address = HostAddressFinder.findAddress(reciverHost);
            log.info("Thrift Server starting on " + address);
            thriftDataReceiver.start(address);
            log.info("Thrift Server Started");
        } catch (SocketException e) {
            log.error("Thrift Server not started !", e);

        }
    }


    public static boolean startReciver(String host, int recivingPort) {
        reciverHost = host;
        boolean start = false;
        try {
            testServer.start(recivingPort);

            start = true;
        } catch (DataBridgeException e) {
            start = false;
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return start;
    }


    public static void stopReciver() {
        thriftDataReceiver.stop();
        log.info("DataRecciver Stopped");
    }


}
