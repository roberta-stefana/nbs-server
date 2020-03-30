package main.websocket;

import main.model.ApplicationUser;

import java.io.Serializable;

public class Message implements Serializable {
    private ApplicationUser user;
    private String data;
    private MessageType type;

    public Message(){}

    public Message(ApplicationUser user, String data, MessageType type) {
        this.user = user;
        this.data = data;
        this.type=type;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public void setUser(ApplicationUser user) {
        this.user = user;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Message{" +
                "user='" + user + '\'' +
                ", message='" + data + '\'' +
                '}';
    }
}
