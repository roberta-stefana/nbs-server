package main.service;

import main.model.*;
import main.model.dto.GameStatsDTO;
import main.model.dto.StatsGameDTO;
import main.repository.*;
import main.websocket.Message;
import main.websocket.MessageType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

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
    public Player findPlayerById(int idPlayer){
        return repo_player.findByIdPlayer(idPlayer);
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

    public List<Stats> findAllStatsByIdPlayer(int idPlayer){
        return repo_stats.findAllByIdPlayer(idPlayer);
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

    private void saveFirstPlayersComments(Game game){
        List<Player> playersTeam1 = repo_player.findByIdTeam(game.getTeam1().getIdTeam());
        List<Player> playersTeam2 = repo_player.findByIdTeam(game.getTeam2().getIdTeam());

        playersTeam1.forEach(x ->{
            if(x.isOnCourt()){
                saveComments(new Comments("#"+x.getNumber()+ " "+ x.getName() + " IN", game.getIdGame(), 1,"10:00","0-0", x.getIdTeam()));
            }
        });

        playersTeam2.forEach(x ->{
            if(x.isOnCourt()){
                saveComments(new Comments("#"+x.getNumber()+ " "+ x.getName() + " IN", game.getIdGame(), 1,"10:00","0-0", x.getIdTeam()));
            }
        });
    }

    //WebSocket
    public Game adminJoined(int idGame){
        Game game = repo_game.findByIdGame(idGame);
        saveFirstPlayersComments(game);
        return game;
    }

    public GameStatsDTO addUser(Game game, int activeUsers){
        Game newGame = setActiveUsersGame(game, activeUsers);
        List<Stats> stats = findAllGameStats(game.getIdGame());
        GameStatsDTO gameStatsDTO = new GameStatsDTO(newGame, stats);
        return gameStatsDTO;
    }

    public Game setActiveUsersGame(Game game, int activeUsers){
        LiveGame liveGame = game.getLiveGame();
        liveGame.setActiveUsers(activeUsers);
        LiveGame savedLiveGame = repo_live_game.save(liveGame);
        game.setLiveGame(savedLiveGame);
        return game;
    }

    public void endGame(Game game){
        game.setLive(false);
        repo_game.save(game);
    }

    public List<Object> changeQuater(Game game, String time){
        LiveGame liveGame = game.getLiveGame();
        String textComment = "";
        if(liveGame.getQuater()==4){
            textComment =" END GAME ";
        }else{
            int lastQuater = liveGame.getQuater();
            liveGame.setQuater(lastQuater+1);
            liveGame.setTime("10:00");
            LiveGame savedLiveGame = saveLiveGame(liveGame);
            game.setLiveGame(savedLiveGame);
            textComment ="End of quater "+lastQuater+". Quater "+savedLiveGame.getQuater()+" is about to start.";
        }
        Comments comments = new Comments(
                textComment,
                game.getIdGame(),liveGame.getQuater(),time,
                liveGame.getPoints1()+"-"+ liveGame.getPoints2(), game.getIdTeam1());
        Comments savedComment = saveComments(comments);
        return Arrays.asList(game, savedComment);
    }

    public Stats updateStats(Stats stats, String type){
        switch (type){
            case "OFF REB":
                stats.setOffRebounds(stats.getOffRebounds()+1);
            case "DEF REB":
                stats.setDefRebounds(stats.getDefRebounds()+1);
            case "BS":
                stats.setBlockedShots(stats.getBlockedShots()+1);
            case "AS":
                stats.setAssists(stats.getAssists()+1);
            case "ST":
                stats.setSteals(stats.getSteals()+1);
            case "TO":
                stats.setTurnovers(stats.getTurnovers()+1);
            case "PF":
                stats.setFouls(stats.getFouls()+1);
            case "FD":
                stats.setFoulsDrawn(stats.getFoulsDrawn()+1);
        }

        stats.computeEfficiency();
        Stats newStats = saveStats(stats);
        return newStats;
    }

    public List<Object> updatePlayersTime(Game game, List<Stats> statsList, String time){
        List<Stats> savedStats = new ArrayList<>();
        String[] timeComponents= time.split("/");
        String intervalTime = timeComponents[0];
        String gameTime = timeComponents[1];

        LiveGame liveGame= game.getLiveGame();
        liveGame.setTime(gameTime);
        LiveGame newLiveGame = saveLiveGame(liveGame);
        game.setLiveGame(newLiveGame);
        statsList.forEach(x->{
            String playerTime = x.getTime();
            String[] arr = playerTime.split(":");
            int existingMin = Integer.parseInt(arr[0]);
            int existingSec = Integer.parseInt(arr[1]);
            String[] arr2 = intervalTime.split((":"));
            int plusMin = Integer.parseInt(arr2[0]);
            int plusSec = Integer.parseInt(arr2[1]);
            int finalSec = (existingSec+plusSec) % 60;
            int finalMin = existingMin+ plusMin+ (existingSec+ plusSec)/60;

            x.setTime(finalMin+":"+finalSec);
            Stats newStats = saveStats(x);
            savedStats.add(newStats);
        });
        return Arrays.asList(game, savedStats);
    }

    public List<Object> updateMissShot(int points, Stats stats){
        String commentText ="";
        Message message;
        if(points == 1){
            stats.setMissFt(stats.getMissFt()+1);
            commentText =" 1 free throw missed";
        }else if(points==2){
            stats.setMiss2p(stats.getMiss2p()+1);
            commentText = " 2 points missed";
        }else{
            stats.setMiss3p(stats.getMiss3p()+1);
            commentText = " 3 points missed";
        }
        stats.computeEfficiency();
        Stats newStats = saveStats(stats);
        return Arrays.asList(commentText, newStats);
    }

    public List<Object> updateSubstitution(String playersId, Game game, String time){
        String[] playersIdComponents= playersId.split(",");
        Player playerOut = findPlayerById(Integer.parseInt(playersIdComponents[0]));
        playerOut.setOnCourt(false);
        Player savedPlayerOut = savePlayer(playerOut);
        Player playerIn = findPlayerById(Integer.parseInt(playersIdComponents[1]));
        playerIn.setOnCourt(true);
        Player savedPlayerIn = savePlayer(playerIn);
        LiveGame liveGame = game.getLiveGame();
        String playersIdAndTeam = playersId + ","+playerIn.getIdTeam();
        Comments comments = new Comments(
                "#"+savedPlayerOut.getNumber()+ " "+savedPlayerOut.getName()+
                        " OUT, #" + savedPlayerIn.getNumber()+ " " + savedPlayerIn.getName() + " IN",
                game.getIdGame(),liveGame.getQuater(),time,
                liveGame.getPoints1()+"-"+ liveGame.getPoints2(), playerIn.getIdTeam());
        Comments savedComment = saveComments(comments);
        return Arrays.asList(playersIdAndTeam, savedComment);
    }

    public List<Object> updateScoreShot(Game game,Stats stats, int points, String time){
        LiveGame liveGame = game.getLiveGame();
        if(stats.getPlayer().getIdTeam() == game.getIdTeam1()){
            liveGame.setPoints1(liveGame.getPoints1()+points);
        }else{
            liveGame.setPoints2(liveGame.getPoints2()+points);
        }
        String commentText="";
        Message message;
        if(points == 1){
            stats.setMadeFt(stats.getMadeFt()+1);
            commentText =" 1 free throw made";
        }else if(points==2){
            stats.setMade2p(stats.getMade2p()+1);
            commentText = " 2 points made";
        }else{
            stats.setMade3p(stats.getMade3p()+1);
            commentText = " 3 points made";
        }
        stats.computeEfficiency();
        Stats newStats = saveStats(stats);
        liveGame.setTime(time);
        LiveGame newLiveGame = saveLiveGame(liveGame);
        game.setLiveGame(newLiveGame);
        return Arrays.asList(game, newStats, commentText);
    }

}
