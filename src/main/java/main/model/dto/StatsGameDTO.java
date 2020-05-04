package main.model.dto;

import main.model.Game;
import main.model.Player;

import java.io.Serializable;
import java.util.List;

public class StatsGameDTO implements Serializable {
    private List<Player> playerList;
    private Game game;

    public StatsGameDTO(){}

    public List<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }


    @Override
    public String toString() {
        return "StatsGameDTO{" +
                "playerList=" + playerList +
                ", game=" + game +
                '}';
    }
}
