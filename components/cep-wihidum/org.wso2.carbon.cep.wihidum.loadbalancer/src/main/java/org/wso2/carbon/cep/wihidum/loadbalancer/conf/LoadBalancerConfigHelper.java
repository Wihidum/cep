package org.wso2.carbon.cep.wihidum.loadbalancer.conf;


import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;

import org.apache.axiom.om.OMFactory;
import org.wso2.carbon.cep.wihidum.loadbalancer.nodemanager.Node;

import javax.xml.namespace.QName;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LoadBalancerConfigHelper {
    public static LoadBalancerConfiguration fromOM(OMElement omElement) {
        LoadBalancerConfiguration loadBalancerConfiguration = new LoadBalancerConfiguration();
        Iterator iterator = omElement.getChildElements();
        for (; iterator.hasNext(); ) {
            OMElement omElementChild = (OMElement) iterator.next();
            if (omElementChild.getLocalName().equals("enabled")) {
                if (omElementChild.getText().equals("true")) {
                    loadBalancerConfiguration.setLoadbalanceron(true);
                } else {
                    loadBalancerConfiguration.setLoadbalanceron(false);
                }
            } else if (omElementChild.getLocalName().equals("port")) {
                loadBalancerConfiguration.setPort(Integer.parseInt(omElementChild.getText().trim()));
            } else if (omElementChild.getLocalName().equals("reciverbundlesize")) {
                loadBalancerConfiguration.setReciverbundlesize(Integer.parseInt(omElementChild.getText().trim()));
            } else if (omElementChild.getLocalName().equals("eventdividecount")) {
                loadBalancerConfiguration.setEventDivideCount(Integer.parseInt(omElementChild.getText().trim()));
            } else if (omElementChild.getLocalName().equals("method")) {
                Iterator iteratorTwo = omElementChild.getChildElements();
                for (; iteratorTwo.hasNext(); ) {
                    Object obj = iteratorTwo.next();
                    if (obj instanceof OMElement) {
                        OMElement omElementOne = (OMElement) obj;
                        if (omElementOne.getLocalName().equals("roundrobin")) {
                            loadBalancerConfiguration.setRoundRobin(Boolean.parseBoolean(omElementOne.getText().trim()));
                        }
                        if (omElementOne.getLocalName().equals("eventstream")) {
                            loadBalancerConfiguration.setEventStream(Boolean.parseBoolean(omElementOne.getText().trim()));
                        }
                    }
                }
            } else if (omElementChild.getLocalName().equals("outputnodes")) {
                Iterator iteratorOne = omElementChild.getChildren();
                for (; iteratorOne.hasNext(); ) {
                    Object obj = iteratorOne.next();

                    if (obj instanceof OMElement && ((OMElement) obj).getLocalName().equals("outputnode")) {
                        Iterator iteratorTwo = ((OMElement) obj).getChildren();
                        String nodeId = ((OMElement) obj).getAttribute(new QName("id")).toString();
                        /*TODO need a way to store this node id
                          this was designed to calculate node id using host and port strings
                          may have to change it later*/
                        String ip = null;
                        String port = null;

                        for (; iteratorTwo.hasNext(); ) {
                            Object obj1 = iteratorTwo.next();
                            if (obj instanceof OMElement) {
                                if (((OMElement) obj1).getLocalName().equals("ip")) {
                                    ip = ((OMElement) obj1).getText().trim();
                                } else if (((OMElement) obj1).getLocalName().equals("port")) {
                                    port = ((OMElement) obj1).getText().trim();
                                }
                            }
                        }
                        if (!ip.equals(null) && !port.equals(null)) {
                            loadBalancerConfiguration.addOutputNode(ip, port);
                        }
                    }
                }
            } else if (omElementChild.getLocalName().equals("RRDs")) {
                Iterator iteratorOne = omElementChild.getChildren();
                for (; iteratorOne.hasNext(); ) {
                    Object obj = iteratorOne.next();

                    if (obj instanceof OMElement && ((OMElement) obj).getLocalName().equals("RRD")) {
                        Iterator iteratorTwo = ((OMElement) obj).getChildren();
                        String RRDId = ((OMElement) obj).getAttribute(new QName("id")).toString();
                        ArrayList<String> nodeIdList = new ArrayList<String>();
                        for (; iteratorTwo.hasNext(); ) {
                            Object obj1 = iteratorTwo.next();
                            if (obj instanceof OMElement) {
                                if (((OMElement) obj1).getLocalName().equals("outputnode")) {
                                    nodeIdList.add(((OMElement) obj1).getAttribute(new QName("id")).toString());
                                }
                            }
                        }
                        loadBalancerConfiguration.addRRDconfig(RRDId, nodeIdList);//TODO add error handling to storing mech
                    }
                }
            } else if (omElementChild.getLocalName().equals("ESD")) {
                Iterator iteratorOne = omElementChild.getChildren();
                for (; iteratorOne.hasNext(); ) {
                    Object obj = iteratorOne.next();

                    if (obj instanceof OMElement && ((OMElement) obj).getLocalName().equals("stream")) {
                        Iterator iteratorTwo = ((OMElement) obj).getChildren();
                        String streamId = ((OMElement) obj).getAttribute(new QName("id")).toString();
                        ArrayList<String> senderIdList = new ArrayList<String>();
                        for (; iteratorTwo.hasNext(); ) {
                            Object obj1 = iteratorTwo.next();
                            if (obj instanceof OMElement) {
                                if (((OMElement) obj1).getLocalName().equals("outputnode") ||
                                        ((OMElement) obj1).getLocalName().equals("RRD")) {
                                    senderIdList.add(((OMElement) obj1).getAttribute(new QName("id")).toString());
                                }
                            }
                        }
                        loadBalancerConfiguration.addESDConfig(streamId, senderIdList);//TODO add error handling to storing mech
                    }
                }
            } else if (omElementChild.getLocalName().equals("blockingqueuecapacity")) {
                loadBalancerConfiguration.setBlockingQueueCapacity(Integer.parseInt(omElementChild.getText().trim()));
            } else if (omElementChild.getLocalName().equals("workerthreads")) {
                loadBalancerConfiguration.setQueueWorkerThreads(Integer.parseInt(omElementChild.getText().trim()));
            }

        }
        return loadBalancerConfiguration;
    }

    public static OMElement toOM(LoadBalancerConfiguration loadBalancerConfiguration){
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement loadbalncer = factory.createOMElement(new QName("","loadbalancer"));
        OMElement enable = factory.createOMElement(new QName("","enabled"));
        if(loadBalancerConfiguration.isLoadbalanceron()){
            enable.setText("true");
        }else{
            enable.setText("false");
        }
        OMElement port = factory.createOMElement(new QName("","port"));
        port.setText(String.valueOf(loadBalancerConfiguration.getPort()));
        OMElement reciverbundlesize = factory.createOMElement(new QName("","reciverbundlesize"));
        reciverbundlesize.setText(String.valueOf(loadBalancerConfiguration.getReciverbundlesize()));
        OMElement eventdividecount = factory.createOMElement(new QName("","eventdividecount"));
        eventdividecount.setText(String.valueOf(loadBalancerConfiguration.getEventDivideCount()));
        OMElement blockingqueuecapacity = factory.createOMElement(new QName("","blockingqueuecapacity"));
        blockingqueuecapacity.setText(String.valueOf(loadBalancerConfiguration.getBlockingQueueCapacity()));
        OMElement workerthreads = factory.createOMElement(new QName("","workerthreads"));
        workerthreads.setText(String.valueOf(loadBalancerConfiguration.getQueueWorkerThreads()));
        OMElement method = factory.createOMElement(new QName("","method"));
        OMElement roundrobin = factory.createOMElement(new QName("","roundrobin"));
        OMElement eventstream = factory.createOMElement(new QName("","eventstream"));

        if(loadBalancerConfiguration.isRoundRobin()){
            roundrobin.setText("true");
            eventstream.setText("false");
        }else if(loadBalancerConfiguration.isStreamDivide()){
            roundrobin.setText("true");
            eventstream.setText("false");

        }
          method.addChild(roundrobin);
          method.addChild(eventstream);
        loadbalncer.addChild(enable);
        loadbalncer.addChild(port);
        loadbalncer.addChild(reciverbundlesize);
        loadbalncer.addChild(eventdividecount);
        loadbalncer.addChild(blockingqueuecapacity);
        loadbalncer.addChild(workerthreads);
        loadbalncer.addChild(method);
        List<Node> nodeList =loadBalancerConfiguration.getNodeList();
        for(Node node : nodeList){
            OMElement outputNode = factory.createOMElement(new QName("","output"));
            OMElement ip = factory.createOMElement(new QName("","ip"));
            ip.setText(node.getHostname());
            OMElement portout = factory.createOMElement(new QName("","port"));
            portout.setText(node.getPort());
            outputNode.addChild(ip);
            outputNode.addChild(portout);
            loadbalncer.addChild(outputNode);

        }

        return  loadbalncer;
    }




}
