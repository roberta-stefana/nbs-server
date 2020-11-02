package main.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class QuaterPoints implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idQuaterPoints;

    @Column
    private int team1Quater1;
    @Column
    private int team2Quater1;
    @Column
    private int team1Quater2;
    @Column
    private int team2Quater2;
    @Column
    private int team1Quater3;
    @Column
    private int team2Quater3;
    @Column
    private int team1Quater4;
    @Column
    private int team2Quater4;

    public QuaterPoints() {
        this.team1Quater1=0;
        this.team1Quater2=0;
        this.team1Quater3=0;
        this.team1Quater4=0;
        this.team2Quater1=0;
        this.team2Quater2=0;
        this.team2Quater3=0;
        this.team2Quater4=0;
    }

    public int getIdQuaterPoints() {
        return idQuaterPoints;
    }

    public void setIdQuaterPoints(int idQuaterPoints) {
        this.idQuaterPoints = idQuaterPoints;
    }

    public int getTeam1Quater1() {
        return team1Quater1;
    }

    public void setTeam1Quater1(int team1Quater1) {
        this.team1Quater1 = team1Quater1;
    }

    public int getTeam2Quater1() {
        return team2Quater1;
    }

    public void setTeam2Quater1(int team2Quater1) {
        this.team2Quater1 = team2Quater1;
    }

    public int getTeam1Quater2() {
        return team1Quater2;
    }

    public void setTeam1Quater2(int team1Quater2) {
        this.team1Quater2 = team1Quater2;
    }

    public int getTeam2Quater2() {
        return team2Quater2;
    }

    public void setTeam2Quater2(int team2Quater2) {
        this.team2Quater2 = team2Quater2;
    }

    public int getTeam1Quater3() {
        return team1Quater3;
    }

    public void setTeam1Quater3(int team1Quater3) {
        this.team1Quater3 = team1Quater3;
    }

    public int getTeam2Quater3() {
        return team2Quater3;
    }

    public void setTeam2Quater3(int team2Quater3) {
        this.team2Quater3 = team2Quater3;
    }

    public int getTeam1Quater4() {
        return team1Quater4;
    }

    public void setTeam1Quater4(int team1Quater4) {
        this.team1Quater4 = team1Quater4;
    }

    public int getTeam2Quater4() {
        return team2Quater4;
    }

    public void setTeam2Quater4(int team2Quater4) {
        this.team2Quater4 = team2Quater4;
    }

    @Override
    public String toString() {
        return "QuaterPoints{" +
                "idQuaterPoints=" + idQuaterPoints +
                ", team1Quater1=" + team1Quater1 +
                ", team2Quater1=" + team2Quater1 +
                ", team1Quater2=" + team1Quater2 +
                ", team2Quater2=" + team2Quater2 +
                ", team1Quater3=" + team1Quater3 +
                ", team2Quater3=" + team2Quater3 +
                ", team1Quater4=" + team1Quater4 +
                ", team2Quater4=" + team2Quater4 +
                '}';
    }
}
