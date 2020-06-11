package main.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;

@Entity
public class Stats implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int idStats;

    @ManyToOne
    @JoinColumn(name = "idPlayer", insertable = false, updatable = false)
    private Player player;

    @Column
    private String time;
    @Column
    private int madeFt;
    @Column
    private int missFt;
    @Column
    private int made2p;
    @Column
    private int miss2p;
    @Column
    private int made3p;
    @Column
    private int miss3p;
    @Column
    private int defRebounds;
    @Column
    private int offRebounds;
    @Column
    private int assists;
    @Column
    private int steals;
    @Column
    private int turnovers;
    @Column
    private int fouls;
    @Column
    private int foulsDrawn;
    @Column
    private int blockedShots;
    @Column
    private int efficiency;
    @Column
    private int idPlayer;
    @Column
    private int idGame;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="idLiveGame", insertable = false, updatable = false)
    private Game game;

    public Stats(){}

    public Stats(Player player, int idGame){
        this.player = player;
        this.idPlayer = player.getIdPlayer();
        this.idGame = idGame;
        this.time = "0:0";
    }

    public int getBlockedShots() {
        return blockedShots;
    }
    public int getFouls() {
        return fouls;
    }
    public void setBlockedShots(int blockedShots) {
        this.blockedShots = blockedShots;
    }

    public void setFouls(int fauls) {
        this.fouls = fauls;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(int efficiency) {
        this.efficiency = efficiency;
    }

    public int getIdStats() {
        return idStats;
    }

    public void setIdStats(int idStats) {
        this.idStats = idStats;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public int getMadeFt() {
        return madeFt;
    }

    public void setMadeFt(int madeFt) {
        this.madeFt = madeFt;
    }

    public int getMissFt() {
        return missFt;
    }

    public void setMissFt(int missFt) {
        this.missFt = missFt;
    }

    public int getMade2p() {
        return made2p;
    }

    public void setMade2p(int made2p) {
        this.made2p = made2p;
    }

    public int getMiss2p() {
        return miss2p;
    }

    public void setMiss2p(int miss2p) {
        this.miss2p = miss2p;
    }

    public int getMade3p() {
        return made3p;
    }

    public void setMade3p(int made3p) {
        this.made3p = made3p;
    }

    public int getMiss3p() {
        return miss3p;
    }

    public void setMiss3p(int miss3p) {
        this.miss3p = miss3p;
    }

    public int getSteals() {
        return steals;
    }

    public void setSteals(int steals) {
        this.steals = steals;
    }

    public int getTurnovers() {
        return turnovers;
    }

    public void setTurnovers(int turnovers) {
        this.turnovers = turnovers;
    }

    public int getDefRebounds() {
        return defRebounds;
    }

    public void setDefRebounds(int defRebounds) {
        this.defRebounds = defRebounds;
    }

    public int getOffRebounds() {
        return offRebounds;
    }

    public void setOffRebounds(int offRebounds) {
        this.offRebounds = offRebounds;
    }

    public int getIdPlayer() {
        return idPlayer;
    }

    public int getIdGame() {
        return idGame;
    }

    public void setIdPlayer(int idPlayer) {
        this.idPlayer = idPlayer;
    }

    public void setIdGame(int idGame) {
        this.idGame = idGame;
    }

    public int getFoulsDrawn() {
        return foulsDrawn;
    }

    public void setFoulsDrawn(int foulsDrawn) {
        this.foulsDrawn = foulsDrawn;
    }

    public void computeEfficiency(){
        this.efficiency = madeFt + made2p*2 + made3p*3 + offRebounds + defRebounds + assists + steals + blockedShots
                - turnovers - miss3p - miss2p - missFt;
    }


    @Override
    public String toString() {
        return "Stats{" +
                "idStats=" + idStats +
                ", player=" + player +
                ", time='" + time + '\'' +
                ", madeFt=" + madeFt +
                ", missFt=" + missFt +
                ", made2p=" + made2p +
                ", miss2p=" + miss2p +
                ", made3p=" + made3p +
                ", miss3p=" + miss3p +
                ", defRebounds=" + defRebounds +
                ", offRebounds=" + offRebounds +
                ", assists=" + assists +
                ", steals=" + steals +
                ", turnovers=" + turnovers +
                ", fouls=" + fouls +
                ", foulsDrawn=" + foulsDrawn +
                ", blockedShots=" + blockedShots +
                ", efficiency=" + efficiency +
                ", idPlayer=" + idPlayer +
                ", idGame=" + idGame +
                '}';
    }
}
