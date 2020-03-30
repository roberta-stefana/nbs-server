package main.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
public class Game implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idGame;
    @Column
    private Date date;
    @Column
    private Boolean live;
    @Column
    private int idTeam1;
    @Column
    private int idTeam2;
    @Column
    private int idLiveGame;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idTeam1", insertable=false, updatable = false)
    private Team team1;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idTeam2", insertable=false, updatable = false)
    private Team team2;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="idQuaterPoints")
    private QuaterPoints quaterPoints;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="idLiveGame", insertable = false, updatable = false)
    private LiveGame liveGame;


    public Game(){}

    public int getIdGame() {
        return idGame;
    }

    public void setIdGame(int idGame) {
        this.idGame = idGame;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getIdTeam1() {
        return idTeam1;
    }

    public void setIdTeam1(int idTeam1) {
        this.idTeam1 = idTeam1;
    }

    public int getIdTeam2() {
        return idTeam2;
    }

    public void setIdTeam2(int idTeam2) {
        this.idTeam2 = idTeam2;
    }

    public int getIdLiveGame() {
        return idLiveGame;
    }

    public void setIdLiveGame(int idLiveGame) {
        this.idLiveGame = idLiveGame;
    }

    public Team getTeam1() {
        return team1;
    }

    public void setTeam1(Team team1) {
        this.team1 = team1;
    }

    public Team getTeam2() {
        return team2;
    }

    public void setTeam2(Team team2) {
        this.team2 = team2;
    }

    public QuaterPoints getQuaterPoints() {
        return quaterPoints;
    }

    public void setQuaterPoints(QuaterPoints quaterPoints) {
        this.quaterPoints = quaterPoints;
    }

    public LiveGame getLiveGame() {
        return liveGame;
    }

    public void setLiveGame(LiveGame liveGame) {
        this.liveGame = liveGame;
    }

    public Boolean getLive() {
        return live;
    }

    public void setLive(Boolean live) {
        this.live = live;
    }

    @Override
    public String toString() {
        return "Game{" +
                "idGame=" + idGame +
                ", date=" + date +
                ", live=" + live +
                ", idTeam1=" + idTeam1 +
                ", idTeam2=" + idTeam2 +
                ", idLiveGame=" + idLiveGame +
                ", team1=" + team1 +
                ", team2=" + team2 +
                ", quaterPoints=" + quaterPoints +
                ", liveGame=" + liveGame +
                '}';
    }
}
