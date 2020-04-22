package main.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Comments implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idComment;

    @Column
    private String comment;

    @Column
    private int idGame;

    @Column
    private int quater;

    public Comments(){}

    public Comments(String comment, int idGame, int quater){
        this.comment = comment;
        this.idGame=idGame;
        this.quater=quater;
    }

    public int getIdComment() {
        return idComment;
    }

    public void setIdComment(int idComment) {
        this.idComment = idComment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getIdGame() {
        return idGame;
    }

    public void setIdGame(int idGame) {
        this.idGame = idGame;
    }

    public int getQuater() {
        return quater;
    }

    public void setQuater(int quater) {
        this.quater = quater;
    }

    @Override
    public String toString() {
        return "Comments{" +
                "idComment=" + idComment +
                ", comment='" + comment + '\'' +
                ", idGame=" + idGame +
                ", quater=" + quater +
                '}';
    }
}
