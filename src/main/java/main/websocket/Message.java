package main.websocket;

import main.model.ApplicationUser;

import java.io.Serializable;

public class Message implements Serializable {
    private String data;
    private MessageType type;
    private Object object;

    public Message(){}

    public Message(MessageType messageType, Object object){
        this.type=messageType;
        this.object = object;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }


    @Override
    public String toString() {
        return "Message{" +
                "data='" + data + '\'' +
                ", type=" + type +
                ", object=" + object +
                '}';
    }
}
