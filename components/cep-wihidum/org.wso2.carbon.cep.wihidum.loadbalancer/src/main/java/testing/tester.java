package testing;

import org.apache.log4j.Logger;
import org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider.Divider;
import org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider.DividerFactory;
import org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider.impl.EventStreamDivider;
import org.wso2.carbon.cep.wihidum.loadbalancer.eventreceiver.ExternalEventReceiver;
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

import java.net.SocketException;
import java.util.List;
import testing.utils.KeyStoreUtil;

public class tester {
    private static Logger log = Logger.getLogger(ExternalEventReceiver.class);
    private static ThriftDataReceiver thriftDataReceiver;
    private static final tester testServer = new tester();
    private static String reciverHost = "localhost";

//    private static Integer total_events = 0;


    private void start(int receiverPort) throws DataBridgeException {
        KeyStoreUtil.setKeyStoreParams();
        DividerFactory dividerFactory = DividerFactory.getInstances();
        final Divider divider = dividerFactory.getDivider();
//        Thread t = new Thread((EventStreamDivider)divider);
//        t.start();
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

//                for (Event event : eventList) {
//                    divider.divide(event);
//                }
//                total_events = total_events + eventList.size();
//                System.out.println("+++++++++++++++++++++++++total_events = " + total_events);
                //code for testing incoming events
                /*System.out.println("#############################new local list");
                for (Event evt : eventList){
                    System.out.println("stream ID = " + evt.getStreamId());
                }
                System.out.println("##############################END new local list");*/
                //end of code for testing incoming events

                divider.divide(eventList);
                log.info("events send to divider");

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

    public static void main(String[] args) {
        tester.startReciver("localhost", 7611);
    }

}
