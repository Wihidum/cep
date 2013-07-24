package org.wso2.carbon.cep.wihidum.loadbalancer.exception;


public class EventPublishException extends Exception {
    public EventPublishException() {

    }

    public EventPublishException(String message) {
        super(message);
    }

    public EventPublishException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventPublishException(Throwable cause) {
        super(cause);
    }


}
