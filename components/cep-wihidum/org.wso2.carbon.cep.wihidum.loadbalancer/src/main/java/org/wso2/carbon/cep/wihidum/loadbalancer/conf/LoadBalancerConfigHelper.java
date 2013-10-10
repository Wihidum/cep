package org.wso2.carbon.cep.wihidum.loadbalancer.conf;


import org.apache.axiom.om.OMElement;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Iterator;

public class LoadBalancerConfigHelper {
    public static LoadBalancerConfiguration fromOM(OMElement omElement) {
        LoadBalancerConfiguration loadBalancerConfiguration = new LoadBalancerConfiguration();
        Iterator iterator = omElement.getChildElements();
        for (; iterator.hasNext(); ) {
            OMElement omElementChild = (OMElement) iterator.next();
            if (omElementChild.getLocalName().equals("enabled")) {//TODO change this to "ENABLED"
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
                        String nodeId = ((OMElement) obj).getAttribute(new QName("id")).getAttributeValue();
                        /*TODO need a way to store this node id
                          this was designed to calculate node id using host and port strings
                          may have to change it later*/
                        String ip = null;
                        String port = null;

                        for (; iteratorTwo.hasNext(); ) {
                            Object obj1 = iteratorTwo.next();
                            if (obj1 instanceof OMElement) {
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
                        String RRDId = ((OMElement) obj).getAttribute(new QName("id")).getAttributeValue();
                        ArrayList<String> nodeIdList = new ArrayList<String>();
                        for (; iteratorTwo.hasNext(); ) {
                            Object obj1 = iteratorTwo.next();
                            if (obj1 instanceof OMElement) {
                                if (((OMElement) obj1).getLocalName().equals("outputnode")) {
                                    nodeIdList.add(((OMElement) obj1).getAttribute(new QName("id")).getAttributeValue());
                                }
                            }
                        }
                        loadBalancerConfiguration.addRRDconfig(RRDId, nodeIdList);//TODO add error handling to storing mech
                    }
                }
            } else if (omElementChild.getLocalName().equals("joins")) {
                Iterator iteratorOne = omElementChild.getChildren();
                for (; iteratorOne.hasNext(); ) {
                    Object obj = iteratorOne.next();

                    if (obj instanceof OMElement && ((OMElement) obj).getLocalName().equals("join")) {
                        Iterator iteratorTwo = ((OMElement) obj).getChildren();
                        String JoinId = ((OMElement) obj).getAttribute(new QName("id")).getAttributeValue();
                        ArrayList<String> nodeIdList = new ArrayList<String>();
                        for (; iteratorTwo.hasNext(); ) {
                            Object obj1 = iteratorTwo.next();
                            if (obj1 instanceof OMElement) {
                                if (((OMElement) obj1).getLocalName().equals("outputnode")) {
                                    nodeIdList.add(((OMElement) obj1).getAttribute(new QName("id")).getAttributeValue());
                                }
                            }
                        }
                        loadBalancerConfiguration.addJoinconfig(JoinId, nodeIdList);//TODO add error handling to storing mech
                    }
                }
            } else if (omElementChild.getLocalName().equals("ESD")) {
                Iterator iteratorOne = omElementChild.getChildren();
                for (; iteratorOne.hasNext(); ) {
                    Object obj = iteratorOne.next();

                    if (obj instanceof OMElement && ((OMElement) obj).getLocalName().equals("stream")) {
                        Iterator iteratorTwo = ((OMElement) obj).getChildren();
                        String streamId = ((OMElement) obj).getAttribute(new QName("id")).getAttributeValue();
                        ArrayList<String> senderIdList = new ArrayList<String>();
                        for (; iteratorTwo.hasNext(); ) {
                            Object obj1 = iteratorTwo.next();
                            if (obj1 instanceof OMElement) {
                                if (((OMElement) obj1).getLocalName().equals("outputnode") ||
                                        ((OMElement) obj1).getLocalName().equals("RRD")) {
                                    senderIdList.add(((OMElement) obj1).getAttribute(new QName("id")).getAttributeValue());
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
}
