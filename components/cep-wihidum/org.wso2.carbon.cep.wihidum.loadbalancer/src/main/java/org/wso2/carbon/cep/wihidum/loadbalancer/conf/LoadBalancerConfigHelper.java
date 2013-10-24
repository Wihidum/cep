package org.wso2.carbon.cep.wihidum.loadbalancer.conf;


import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.wso2.carbon.cep.wihidum.loadbalancer.nodemanager.Node;

import javax.xml.namespace.QName;
import java.util.Iterator;
import java.util.List;

public class LoadBalancerConfigHelper {
    public static LoadBalancerConfiguration fromOM(OMElement omElement) {
        LoadBalancerConfiguration loadBalancerConfiguration = new LoadBalancerConfiguration();
        Iterator iterator = omElement.getChildElements();
        for (; iterator.hasNext(); ) {
            OMElement omElementChild = (OMElement) iterator.next();
            if (omElementChild.getLocalName().equals("enable")) {
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
            } else if (omElementChild.getLocalName().equals("outputnode")) {
                Iterator iteratorOne = omElementChild.getChildren();
                String ip = null;
                String port = null;
                for (; iteratorOne.hasNext(); ) {
                    Object obj = iteratorOne.next();
                    if (obj instanceof OMElement) {

                        if (((OMElement) obj).getLocalName().equals("ip")) {
                            ip = ((OMElement) obj).getText().trim();
                        } else if (((OMElement) obj).getLocalName().equals("port")) {
                            port = ((OMElement) obj).getText().trim();
                        }


                    }
                }
                loadBalancerConfiguration.addOutputNode(ip, port);
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
        OMElement enable = factory.createOMElement(new QName("","enable"));
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
