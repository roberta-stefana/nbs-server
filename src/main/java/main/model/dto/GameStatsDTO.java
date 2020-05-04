package main.model.dto;

import main.model.Game;
import main.model.Stats;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameStatsDTO implements Serializable {
    private Game game;
    private List<Stats> stats;

    public  GameStatsDTO(){ }

    public GameStatsDTO(Game game, List<Stats> stats){
        this.game = game;
        this.stats = stats;
    }

    public GameStatsDTO(Game game, Stats stats){
        this.game = game;
        this.stats = new ArrayList<>();
        this.stats.add(stats);
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public List<Stats> getStats() {
        return stats;
    }

    public void setStats(List<Stats> stats) {
        this.stats = stats;
    }

    @Override
    public String toString() {
        return "GameStatsDTO{" +
                "game=" + game +
                ", stats=" + stats +
                '}';
    }
}
