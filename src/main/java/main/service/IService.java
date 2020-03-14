package main.service;

import main.model.*;

import java.util.List;

public interface IService {
    //ApplicationUser
    ApplicationUser saveApplicationUser(ApplicationUser user);
    ApplicationUser findByUsername(String username);

    //Team
    Iterable<Team> findAllTeam();

    //Players
    Player savePlayer(Player player);
    Iterable<Player> findPlayersByTeam(int teamId);

    //Stats
    Stats saveStats(Stats stats);
    List<Stats> saveTeamStats(StatsGameDTO statsGameDTO);

    //Game
    Game saveGame(Game game);

    //QuaterPoints
    QuaterPoints saveQuaterPoints(QuaterPoints quaterPoints);
}
