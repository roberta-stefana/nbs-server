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
    List<Stats> findAllGameStats(int idGame);

    //LiveGame
    LiveGame saveLiveGame(LiveGame liveGame);


    //Game
    Game saveGame(Game game);
    Game findGameByIdGame(int idGame);
    List<Game> findAllGameByLive(Boolean live);
    List<Game> findAllGame();

    //QuaterPoints
    QuaterPoints saveQuaterPoints(QuaterPoints quaterPoints);

    //Comments
    Comments saveComments(Comments comments);
    List<Comments> findAllCommentsByIdGameAndQuater(int idGame, int quater);
    List<Comments> findAllCommentsByIdGame(int idGame);
    void saveFirstPlayersComments(int idGame);
}
