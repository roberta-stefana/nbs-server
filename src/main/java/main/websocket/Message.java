package main.websocket;

import main.model.ApplicationUser;
import main.model.Comments;

import java.io.Serializable;

public class Message implements Serializable {
    private String idGame;
    private MessageType type;
    private Object object;
    private String time;
    private Comments comment;

    public Message(){}

    public Message(MessageType messageType, Object object){
        this.type=messageType;
        this.object = object;
    }

    public Message(MessageType messageType, Object object, String time){
        this.type=messageType;
        this.object = object;
        this.time = time;
    }

    public Message(MessageType messageType, Comments comments){
        this.type=messageType;
        this.comment = comments;
    }

    public Message(MessageType messageType, Comments comments, Object object){
        this.type=messageType;
        this.comment = comments;
        this.object = object;
    }

    public Message(MessageType messageType){
        this.type = messageType;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getIdGame() {
        return idGame;
    }

    public void setIdGame(String idGame) {
        this.idGame = idGame;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Comments getComment() {
        return comment;
    }

    public void setComment(Comments comment) {
        this.comment = comment;
    }


    @Override
    public String toString() {
        return "Message{" +
                "idGame='" + idGame + '\'' +
                ", type=" + type +
                ", object=" + object +
                ", time='" + time + '\'' +
                ", comment=" + comment +
                '}';
    }
}
