package main.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class LiveGame implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idLiveGame;

    @Column
    private int quater;

    @Column
    private String time;

    @Column
    private int points1;

    @Column
    private int points2;

    public LiveGame(){
    }

    public int getIdLiveGame() {
        return idLiveGame;
    }

    public void setIdLiveGame(int idLiveGame) {
        this.idLiveGame = idLiveGame;
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

    public int getPoints1() {
        return points1;
    }

    public void setPoints1(int points1) {
        this.points1 = points1;
    }

    public int getPoints2() {
        return points2;
    }

    public void setPoints2(int points2) {
        this.points2 = points2;
    }

    @Override
    public String toString() {
        return "LiveGame{" +
                "idLiveGame=" + idLiveGame +
                ", quater=" + quater +
                ", time='" + time + '\'' +
                ", points1=" + points1 +
                ", points2=" + points2 +
                '}';
    }
}
