package main.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Player implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPlayer;
    @Column
    private String name;
    @Column
    private int number;
    @Column
    private boolean onCourt;
    @Column
    private int idTeam;

    public Player(){
    }

    public int getIdPlayer() {
        return idPlayer;
    }

    public void setIdPlayer(int idPlayer) {
        this.idPlayer = idPlayer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isOnCourt() {
        return onCourt;
    }

    public void setOnCourt(boolean onCourt) {
        this.onCourt = onCourt;
    }

    public int getIdTeam() {
        return idTeam;
    }

    public void setIdTeam(int idTeam) {
        this.idTeam = idTeam;
    }

    @Override
    public String toString() {
        return "Player{" +
                "idPlayer=" + idPlayer +
                ", name='" + name + '\'' +
                ", number=" + number +
                ", onCourt=" + onCourt +
                ", idTeam=" + idTeam +
                '}';
    }
}
