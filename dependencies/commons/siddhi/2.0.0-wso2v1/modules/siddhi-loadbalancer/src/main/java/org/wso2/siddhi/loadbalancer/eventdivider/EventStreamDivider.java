package org.wso2.siddhi.loadbalancer.eventdivider;

import org.wso2.carbon.databridge.commons.Event;
import org.wso2.siddhi.loadbalancer.nodemanager.Node;
import org.wso2.siddhi.loadbalancer.nodemanager.NodeProvider;
import org.wso2.siddhi.query.api.QueryFactory;
import org.wso2.siddhi.query.api.query.Query;

import java.util.List;


public class EventStreamDivider implements Divider {
    final static Query query = QueryFactory.createQuery();
    private static final List<Node> nodelist  = NodeProvider.getOutputNodeListFromQuery(query);




    @Override
    public synchronized void divide(Event event) {

            for(Node node:nodelist){
                if(node.getStreamID().equals(event.getStreamId())){
                    node.addEvent(event);
                    break;
                }

            }

    }
}
