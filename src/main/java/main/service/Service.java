package main.service;

import main.model.*;
import main.model.dto.StatsGameDTO;
import main.repository.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Service implements IService{
    @Autowired
    private ApplicationUserRepoJPA repo_user;
    @Autowired
    private TeamRepoJPA repo_team;
    @Autowired
    private PlayerRepoJPA repo_player;
    @Autowired
    private StatsRepoJPA repo_stats;
    @Autowired
    private GameRepoJPA repo_game;
    @Autowired
    private QuaterPointsRepoJPA repo_quater_points;
    @Autowired
    private LiveGameRepoJPA repo_live_game;
    @Autowired
    private CommentsRepoJPA repo_comments;

    public Service(){ }

    //ApplicationUser
    public ApplicationUser saveApplicationUser(ApplicationUser user){
        return repo_user.save(user);
    }
    public ApplicationUser findByUsername(String username){
        return repo_user.findByUsername(username);
    }

    //Team
    public Iterable<Team> findAllTeam(){
        return repo_team.findAll();
    }

    //Player
    public Player savePlayer(Player player){
        return repo_player.save(player);
    }
    public Iterable<Player> findPlayersByTeam(int teamId) {
        Team team = repo_team.getOne(teamId);
        return repo_player.findByIdTeam(team.getIdTeam());
    }

    //Stats
    public Stats saveStats(Stats stats){
        stats.setIdPlayer(stats.getPlayer().getIdPlayer());
        return repo_stats.save(stats);
    }

    public List<Stats> saveTeamStats(StatsGameDTO statsGameDTO){
        List<Player> playerList = statsGameDTO.getPlayerList();
        Game game = statsGameDTO.getGame();
        List<Stats> savedStatsList = new ArrayList<>();
        playerList.forEach(player -> {
            Stats stats = new Stats(player, game.getIdGame());
            savedStatsList.add(repo_stats.save(stats));
        });
        return savedStatsList;
    }

    public List<Stats> findAllGameStats(int idGame){
        return repo_stats.findAllByIdGame(idGame);
    }

    //LiveGame
    public LiveGame saveLiveGame(LiveGame liveGame){
        return repo_live_game.save(liveGame);
    }

    //Game
    public Game saveGame(Game game){
        LiveGame savedLiveGame= repo_live_game.save(new LiveGame());
        QuaterPoints savedQuaterPoints = repo_quater_points.save(new QuaterPoints());
        game.setQuaterPoints(savedQuaterPoints);
        game.setLiveGame(savedLiveGame);
        game.setIdLiveGame(savedLiveGame.getIdLiveGame());
        game.setDate(new Date());
        game.setIdTeam1(game.getTeam1().getIdTeam());
        game.setIdTeam2(game.getTeam2().getIdTeam());
        game.setLive(true);

        return repo_game.save(game);
    }

    public Game updateGame(Game game){
        return repo_game.save(game);
    }

    public Game findGameByIdGame(int idGame){
        return repo_game.findByIdGame(idGame);
    }

    public List<Game> findAllGameByLive(Boolean live){
        return repo_game.findAllByLive(live);
    }

    public List<Game> findAllGame(){
        return repo_game.findAll();
    }


    //QuaterPoints
    public QuaterPoints saveQuaterPoints(QuaterPoints quaterPoints){
        return repo_quater_points.save(quaterPoints);
    }


    //Comments
    public Comments saveComments(Comments comments){
        return repo_comments.save(comments);
    }

    public List<Comments> findAllCommentsByIdGameAndQuater(int idGame, int quater){
        return repo_comments.findAllByIdGameAndQuater(idGame, quater);
    }

    public List<Comments> findAllCommentsByIdGameOrderByDate(int idGame){
        return repo_comments.findAllByIdGameOrderByDateDesc(idGame);
    }

    public void saveFirstPlayersComments(int idGame){
        Game game = repo_game.findByIdGame(idGame);
        List<Player> playersTeam1 = repo_player.findByIdTeam(game.getTeam1().getIdTeam());
        List<Player> playersTeam2 = repo_player.findByIdTeam(game.getTeam2().getIdTeam());

        playersTeam1.forEach(x ->{
            if(x.isOnCourt()){
                saveComments(new Comments("#"+x.getNumber()+ " "+ x.getName() + " IN", idGame, 1,"10:00","0-0", x.getIdTeam()));
            }
        });

        playersTeam2.forEach(x ->{
            if(x.isOnCourt()){
                saveComments(new Comments("#"+x.getNumber()+ " "+ x.getName() + " IN", idGame, 1,"10:00","0-0", x.getIdTeam()));
            }
        });
    }

}
