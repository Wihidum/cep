package org.wso2.carbon.cep.core.distributing.util;


public class BrokerConfig {
    private String ip;
    private String inputBroker;
    private String outputBroker;
    private String inputTopic;
    private String outputTopic;

    public BrokerConfig(String ip, String inputBroker, String inputTopic, String outputBroker, String outputTopic) {
        this.ip = ip;
        this.inputBroker = inputBroker;
        this.outputBroker = outputBroker;
        this.inputTopic = inputTopic;
        this.outputTopic = outputTopic;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getInputBroker() {
        return inputBroker;
    }

    public void setInputBroker(String inputBroker) {
        this.inputBroker = inputBroker;
    }

    public String getOutputBroker() {
        return outputBroker;
    }

    public void setOutputBroker(String outputBroker) {
        this.outputBroker = outputBroker;
    }

    public String getInputTopic() {
        return inputTopic;
    }

    public void setInputTopic(String inputTopic) {
        this.inputTopic = inputTopic;
    }

    public String getOutputTopic() {
        return outputTopic;
    }

    public void setOutputTopic(String outputTopic) {
        this.outputTopic = outputTopic;
    }
}
