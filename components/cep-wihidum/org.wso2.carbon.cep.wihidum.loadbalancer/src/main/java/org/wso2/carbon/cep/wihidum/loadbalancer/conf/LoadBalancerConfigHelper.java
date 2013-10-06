package org.wso2.carbon.cep.wihidum.loadbalancer.conf;


import org.apache.axiom.om.OMElement;
import javax.xml.namespace.QName;
import java.util.Iterator;

public class LoadBalancerConfigHelper {
    public static LoadBalancerConfiguration fromOM(OMElement omElement) {
        LoadBalancerConfiguration loadBalancerConfiguration = new LoadBalancerConfiguration();
        Iterator iterator = omElement.getChildElements();
        for (; iterator.hasNext(); ) {
            OMElement omElementChild = (OMElement) iterator.next();
            if (omElementChild.getLocalName().equals("start")) {//TODO change this to "ENABLED"
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
                    }
                }
            } else if (omElementChild.getLocalName().equals("outputnodes")) {
                Iterator iteratorOne = omElementChild.getChildren();
                for (; iteratorOne.hasNext(); ) {
                    Object obj = iteratorOne.next();

                    if (obj instanceof OMElement && ((OMElement) obj).getLocalName().equals("outputnode")) {
                        Iterator iteratorTwo = ((OMElement) obj).getChildren();
                        String nodeId = ((OMElement) obj).getAttribute(new QName("id")).toString(); //TODO need a way to store this
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
            } else if (omElementChild.getLocalName().equals("RRDs")) {//TODO design a way to store RRD configs
                Iterator iteratorOne = omElementChild.getChildren();
                for (; iteratorOne.hasNext(); ) {
                    Object obj = iteratorOne.next();

                    if (obj instanceof OMElement && ((OMElement) obj).getLocalName().equals("RRD")) {
                        Iterator iteratorTwo = ((OMElement) obj).getChildren();
                        String RRDId = ((OMElement) obj).getAttribute(new QName("id")).toString();

                        for (; iteratorTwo.hasNext(); ) {
                            Object obj1 = iteratorTwo.next();
                            if (obj instanceof OMElement) {
                                if (((OMElement) obj1).getLocalName().equals("outputnode")) {
                                    String nodeId = ((OMElement) obj1).getAttribute(new QName("id")).toString();
                                }
                            }
                        }
                    }
                }
            } else if (omElementChild.getLocalName().equals("ESD")) {//TODO design a way to store ESD config
                Iterator iteratorOne = omElementChild.getChildren();
                for (; iteratorOne.hasNext(); ) {
                    Object obj = iteratorOne.next();

                    if (obj instanceof OMElement && ((OMElement) obj).getLocalName().equals("stream")) {
                        Iterator iteratorTwo = ((OMElement) obj).getChildren();
                        String streamId = ((OMElement) obj).getAttribute(new QName("id")).toString();

                        for (; iteratorTwo.hasNext(); ) {
                            Object obj1 = iteratorTwo.next();
                            if (obj instanceof OMElement) {
                                if (((OMElement) obj1).getLocalName().equals("outputnode")) {
                                    String nodeId = ((OMElement) obj1).getAttribute(new QName("id")).toString();
                                } else if (((OMElement) obj1).getLocalName().equals("RRD")) {
                                    String RRDId = ((OMElement) obj1).getAttribute(new QName("id")).toString();
                                }
                            }
                        }
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
