package main.service;

import main.model.*;
import main.model.dto.GameStatsDTO;
import main.model.dto.StatsGameDTO;
import main.websocket.Message;

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
    List<Comments> findAllCommentsByIdGame(int idGame);

    //WebSocket
    GameStatsDTO hostGame(int idGame);

    GameStatsDTO addUser(int idGame);
    Game setActiveUsersGame(Game game, int activeUsers);
    void endGame(int idGame);
    List<Object> changeQuater(int idGame, String time);
    Stats updateStats(Stats stats, String type);
    List<Object> updatePlayersTime(int idGame, List<Stats> statsList, String time);
    List<Object> updateMissShot(int points, Stats stats);
    List<Object> updateSubstitution(String playersId, int idGame, String time);
    List<Object> updateScoreShot(int idGame,Stats stats, int points, String time);

    Message composeComment(int idGame, String time, String commentText, Message message, Stats newStats);
}
