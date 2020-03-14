package main.service;

import main.model.*;
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
        return repo_player.findByTeam(team);
    }

    //Stats
    public Stats saveStats(Stats stats){
        stats.setIdGame(stats.getGame().getIdGame());
        stats.setIdPlayer(stats.getPlayer().getIdPlayer());
        return repo_stats.save(stats);
    }

    public List<Stats> saveTeamStats(StatsGameDTO statsGameDTO){
        List<Player> playerList = statsGameDTO.getPlayerList();
        Game game = statsGameDTO.getGame();
        List<Stats> savedStatsList = new ArrayList<>();
        playerList.forEach(player -> {
            Stats stats = new Stats(player, game);
            savedStatsList.add(repo_stats.save(stats));
        });
        return savedStatsList;
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
        return repo_game.save(game);
    }

    //QuaterPoints
    public QuaterPoints saveQuaterPoints(QuaterPoints quaterPoints){
        return repo_quater_points.save(quaterPoints);
    }
}
