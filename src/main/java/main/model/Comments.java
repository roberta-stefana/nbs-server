package main.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

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
    @Column
    private String time;
    @Column
    private String score;
    @Column
    private int idTeam;
    @Column
    private Date date;

    public Comments(){}

    public Comments(String comment, int idGame, int quater, String time, String score, int idTeam){
        this.comment = comment;
        this.idGame=idGame;
        this.quater=quater;
        this.time=time;
        this.score=score;
        this.idTeam=idTeam;
        this.date = new Date();
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getScore() {
        return score;
    }

    public int getIdTeam() {
        return idTeam;
    }

    public void setIdTeam(int idTeam) {
        this.idTeam = idTeam;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Comments{" +
                "idComment=" + idComment +
                ", comment='" + comment + '\'' +
                ", idGame=" + idGame +
                ", quater=" + quater +
                ", time='" + time + '\'' +
                ", score='" + score + '\'' +
                ", idTeam=" + idTeam +
                ", date=" + date +
                '}';
    }
}
