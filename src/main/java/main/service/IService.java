package main.service;

import main.model.*;
import main.model.dto.GameStatsDTO;
import main.model.dto.StatsGameDTO;

import java.util.List;

public interface IService {
    //ApplicationUser
    ApplicationUser saveApplicationUser(ApplicationUser user);
    ApplicationUser findByUsername(String username);

    //Team
    Iterable<Team> findAllTeam();

    //Players
    Player savePlayer(Player player);
    Player findPlayerById(int idPlayer);
    Iterable<Player> findPlayersByTeam(int teamId);

    //Stats
    Stats saveStats(Stats stats);
    List<Stats> saveTeamStats(StatsGameDTO statsGameDTO);
    List<Stats> findAllGameStats(int idGame);
    List<Stats> findAllStatsByIdPlayer(int idPlayer);

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
    List<Comments> findAllCommentsByIdGameOrderByDate(int idGame);

    //WebSocket
    Game adminJoined(int idGame);
    GameStatsDTO addUser(Game game, int activeUsers);
    Game setActiveUsersGame(Game game, int activeUsers);
    void endGame(Game game);
    List<Object> changeQuater(Game game, String time);
    Stats updateStats(Stats stats, String type);
    List<Object> updatePlayersTime(Game game, List<Stats> statsList, String time);
    List<Object> updateMissShot(int points, Stats stats);
    List<Object> updateSubstitution(String playersId, Game game, String time);
    List<Object> updateScoreShot(Game game,Stats stats, int points, String time);
}
