package main.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.model.*;
import main.model.dto.GameStatsDTO;
import main.service.IService;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.*;

@Component
public class WebsocketServer extends WebSocketServer {
    @Autowired
    private IService service;
    private Map<Integer, List<WebSocket>> users; //idGame si lista de guests
    private List<Game> games;

    public WebsocketServer() {
        super(new InetSocketAddress(8081));
        users = new HashMap<>();
        games = new ArrayList<>();
        this.start();
    }

    @Override
    public void onStart() {
        System.out.println("Websocket running on port 8081 ...");
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println("New connection from " + webSocket.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("ON CLOSE !!");
        removeUser(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            Message msg = mapper.readValue(message, Message.class);

            switch (msg.getType()) {
                case ADMIN_JOINED:
                    adminJoined(Integer.parseInt(msg.getIdGame()), conn);
                    break;
                case USER_JOINED:
                    addUser(Integer.parseInt(msg.getIdGame()), conn);
                    break;
                case USER_LEFT:
                    removeUser(conn);
                    break;
                case SEND_START_GAME:
                    sendStartGame(Integer.parseInt(msg.getIdGame()));
                    break;
                case SEND_END_GAME:
                    sendEndGame(Integer.parseInt(msg.getIdGame()));
                    break;
                case SEND_SCORE_1:
                    Stats stats = mapper.convertValue(msg.getObject(), Stats.class);
                    sendScore(stats,Integer.parseInt(msg.getIdGame()), msg.getTime(), 1);
                    break;
                case SEND_SCORE_2:
                    Stats stats2 = mapper.convertValue(msg.getObject(), Stats.class);
                    sendScore(stats2,Integer.parseInt(msg.getIdGame()), msg.getTime(), 2);
                    break;
                case SEND_SCORE_3:
                    Stats stats3 = mapper.convertValue(msg.getObject(), Stats.class);
                    sendScore(stats3,Integer.parseInt(msg.getIdGame()), msg.getTime(), 3);
                    break;
                case SEND_MISS_1:
                    Stats stats4 = mapper.convertValue(msg.getObject(), Stats.class);
                    sendMiss(stats4,Integer.parseInt(msg.getIdGame()), msg.getTime(), 1);
                    break;
                case SEND_MISS_2:
                    Stats stats5 = mapper.convertValue(msg.getObject(), Stats.class);
                    sendMiss(stats5,Integer.parseInt(msg.getIdGame()), msg.getTime(), 2);
                    break;
                case SEND_MISS_3:
                    Stats stats6 = mapper.convertValue(msg.getObject(), Stats.class);
                    sendMiss(stats6,Integer.parseInt(msg.getIdGame()), msg.getTime(), 3);
                    break;
                case SEND_OFF_REBOUND:
                    Stats stats7 = mapper.convertValue(msg.getObject(), Stats.class);
                    sendOffRebound(stats7,Integer.parseInt(msg.getIdGame()), msg.getTime());
                    break;
                case SEND_DEF_REBOUND:
                    Stats stats8 = mapper.convertValue(msg.getObject(), Stats.class);
                    sendDefRebound(stats8,Integer.parseInt(msg.getIdGame()), msg.getTime());
                    break;
                case SEND_BLOCKED_SHOT:
                    Stats stats9 = mapper.convertValue(msg.getObject(), Stats.class);
                    sendBlockedShot(stats9,Integer.parseInt(msg.getIdGame()), msg.getTime());
                    break;
                case SEND_ASSIST:
                    Stats stats10 = mapper.convertValue(msg.getObject(), Stats.class);
                    sendAssist(stats10,Integer.parseInt(msg.getIdGame()), msg.getTime());
                    break;
                case SEND_STEAL:
                    Stats stats14 = mapper.convertValue(msg.getObject(), Stats.class);
                    sendSteal(stats14,Integer.parseInt(msg.getIdGame()), msg.getTime());
                    break;
                case SEND_TURNOVER:
                    Stats stats11 = mapper.convertValue(msg.getObject(), Stats.class);
                    sendTurnover(stats11,Integer.parseInt(msg.getIdGame()), msg.getTime());
                    break;
                case SEND_FOUL:
                    Stats stats12 = mapper.convertValue(msg.getObject(), Stats.class);
                    sendFoul(stats12,Integer.parseInt(msg.getIdGame()), msg.getTime());
                    break;
                case SEND_FOUL_DRAWN:
                    Stats stats13 = mapper.convertValue(msg.getObject(), Stats.class);
                    sendFoulDrawn(stats13,Integer.parseInt(msg.getIdGame()), msg.getTime());
                    break;
                case SEND_PLAYERS_TIME:
                    List<Stats> statsList = new ArrayList<>();
                    List list = mapper.convertValue(msg.getObject(), List.class);
                    list.forEach(x->statsList.add(mapper.convertValue(x, Stats.class)));
                    sendPlayersTime(statsList,Integer.parseInt(msg.getIdGame()), msg.getTime());
                    break;
                case SEND_CHANGE_QUATER:

                    sendChangeQuater(Integer.parseInt(msg.getIdGame()), msg.getTime());
                    break;
                case SEND_SUBSTITUTION:
                    sendSubstitution((String)msg.getObject(),Integer.parseInt(msg.getIdGame()), msg.getTime());
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }


    private void adminJoined(int idGame, WebSocket conn){
        System.out.println("NEW GAME --> ADMIN JOINED");
        Game game = getGameById(idGame);
        if(game == null) {
            List<WebSocket> guests = new ArrayList<>();
            guests.add(conn);
            users.put(idGame, guests);
            Game newGame = service.findGameByIdGame(idGame);
            games.add(newGame);
            service.saveFirstPlayersComments(idGame);
            Message message = new Message(MessageType.ADMIN_SUCCESSFULLY_JOINED, newGame);
            sendMessageToOneUser(message, conn);
        }else{ // FOR REFRESH
            setReconnectAdmin(idGame, conn);
            List<Stats> stats = service.findAllGameStats(idGame);
            GameStatsDTO gameStatsDTO = new GameStatsDTO(game, stats);
            Message message = new Message(MessageType.ADMIN_SUCCESSFULL_REFRESH, gameStatsDTO);
            sendMessageToOneUser(message, conn);
        }
    }

    private void addUser(int idGame, WebSocket conn){
        System.out.println("USER JOINED --> ADD USER");
        List<WebSocket> guests = users.get(idGame);
        guests.add(conn);
        users.put(idGame, guests);
        int activeUsers = guests.size()-1;
        Message message = new Message(MessageType.ACTIVE_USERS, String.valueOf(activeUsers));

        Game game = getGameById(idGame);
        LiveGame liveGame =game.getLiveGame();
        liveGame.setActiveUsers(activeUsers);
        LiveGame newLiveGame = service.saveLiveGame(liveGame);
        List<Stats> stats = service.findAllGameStats(idGame);
        GameStatsDTO gameStatsDTO = new GameStatsDTO(game, stats);


        Message messageUser = new Message(MessageType.GUEST_SUCCESSFULLY_JOINED, gameStatsDTO);
        sendMessageToOneUser(messageUser, conn);
        broadcastMessage(message, guests);
    }

    private void removeUser(WebSocket conn)  {
        System.out.println("REMOVE USER");
        users.forEach((idGame,list)->{
            if(list.get(0) == conn){
                System.out.println("ADMIN LEFT");
                list.set(0, null);
                // trimitem mesaj celorlalti sa astepte pana vine adminul???
            }else{
                boolean isRemoved = list.remove(conn);
                if(isRemoved) {
                    System.out.println("Meci " + idGame + ": " + list.size());
                    Game game = getGameById(idGame);
                    LiveGame liveGame = game.getLiveGame();
                    liveGame.setActiveUsers(list.size()-1);
                    service.saveLiveGame(liveGame);
                    Message message = new Message(MessageType.ACTIVE_USERS, liveGame.getActiveUsers());
                    broadcastMessage(message, list);
                }
            }
        });
    }

    private void sendStartGame(int idGame){
        List<WebSocket> list = users.get(idGame);
        Comments comments = new Comments("Start game",idGame,1,"10:00","0-0", 1);
        service.saveComments(comments);
        Message message = new Message(MessageType.RECEIVE_START_GAME,comments);
        broadcastMessage(message, list);
    }

    private void sendEndGame(int idGame){
        Game game = getGameById(idGame);
        game.setLive(false);
        service.updateGame(game);

        List<WebSocket> list = users.get(idGame);
        Message message = new Message(MessageType.RECEIVE_END_GAME);
        broadcastMessage(message, list);

        users.remove(idGame);
        games.remove(game);
    }

    private void sendChangeQuater(int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_CHANGE_QUATER);
        Game game = getGameById(idGame);
        LiveGame liveGame = game.getLiveGame();
        String textComment = "";
        if(liveGame.getQuater()==4){
            textComment =" END GAME ";
        }else{
            int lastQuater = liveGame.getQuater();
            liveGame.setQuater(lastQuater+1);
            liveGame.setTime("10:00");
            service.saveLiveGame(liveGame);
            textComment ="End of quater "+lastQuater+". Quater "+liveGame.getQuater()+" is about to start.";
        }
        Comments comments = new Comments(
                textComment,
                idGame,liveGame.getQuater(),time,
                liveGame.getPoints1()+"-"+ liveGame.getPoints2(), game.getIdTeam1());
        Comments savedComment = service.saveComments(comments);
        message.setComment(savedComment);
        broadcastMessage(message, users.get(idGame));
    }

    private void sendOffRebound(Stats stats, int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_STATS_UPDATE);
        String textComment =" OFFENSIVE REBOUND";
        stats.setOffRebounds(stats.getOffRebounds()+1);
        stats.computeEfficiency();
        Stats newStats = service.saveStats(stats);
        LiveGame liveGame = getGameById(idGame).getLiveGame();
        createAndBroadcastComment(idGame,time, textComment, message, newStats, liveGame);
    }

    private void sendDefRebound(Stats stats, int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_STATS_UPDATE);
        String textComment =" DEFENSIVE REBOUND";
        stats.setDefRebounds(stats.getDefRebounds()+1);
        stats.computeEfficiency();
        Stats newStats = service.saveStats(stats);
        LiveGame liveGame = getGameById(idGame).getLiveGame();
        createAndBroadcastComment(idGame,time, textComment, message, newStats, liveGame);
    }

    private void sendBlockedShot(Stats stats, int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_STATS_UPDATE);
        String textComment =" BLOCKED SHOT";
        stats.setBlockedShots(stats.getBlockedShots()+1);
        stats.computeEfficiency();
        Stats newStats = service.saveStats(stats);
        LiveGame liveGame = getGameById(idGame).getLiveGame();
        createAndBroadcastComment(idGame,time, textComment, message, newStats, liveGame);
    }

    private void sendAssist(Stats stats, int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_STATS_UPDATE);
        String textComment =" ASSIST";
        stats.setAssists(stats.getAssists()+1);
        stats.computeEfficiency();
        Stats newStats = service.saveStats(stats);
        LiveGame liveGame = getGameById(idGame).getLiveGame();
        createAndBroadcastComment(idGame,time, textComment, message, newStats, liveGame);
    }

    private void sendSteal(Stats stats, int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_STATS_UPDATE);
        String textComment =" STEAL";
        stats.setSteals(stats.getSteals()+1);
        stats.computeEfficiency();
        Stats newStats = service.saveStats(stats);
        LiveGame liveGame = getGameById(idGame).getLiveGame();
        createAndBroadcastComment(idGame,time, textComment, message, newStats, liveGame);
    }

    private void sendTurnover(Stats stats, int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_STATS_UPDATE);
        String textComment =" TURNOVER";
        stats.setTurnovers(stats.getTurnovers()+1);
        stats.computeEfficiency();
        Stats newStats = service.saveStats(stats);
        LiveGame liveGame = getGameById(idGame).getLiveGame();
        createAndBroadcastComment(idGame,time, textComment, message, newStats, liveGame);
    }

    private void sendFoul(Stats stats, int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_STATS_UPDATE);
        String textComment =" PERSONAL FOUL";
        stats.setFouls(stats.getFouls()+1);
        Stats newStats = service.saveStats(stats);
        LiveGame liveGame = getGameById(idGame).getLiveGame();
        createAndBroadcastComment(idGame,time, textComment, message, newStats, liveGame);
    }

    private void sendFoulDrawn(Stats stats, int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_STATS_UPDATE);
        String textComment =" FOUL DRAWN";
        stats.setFoulsDrawn(stats.getFoulsDrawn()+1);
        Stats newStats = service.saveStats(stats);
        LiveGame liveGame = getGameById(idGame).getLiveGame();
        createAndBroadcastComment(idGame,time, textComment, message, newStats, liveGame);
    }

    private void sendPlayersTime(List<Stats> statsList, int idGame, String time){
        List<Stats> savedStats = new ArrayList<>();
        String[] timeComponents= time.split("/");
        String intervalTime = timeComponents[0];
        String gameTime = timeComponents[1];
        LiveGame liveGame= getGameById(idGame).getLiveGame();
        liveGame.setTime(gameTime);
        service.saveLiveGame(liveGame);
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
            Stats newStats = service.saveStats(x);
            savedStats.add(newStats);
        });
        Message message = new Message(MessageType.RECEIVE_PLAYERS_TIME, savedStats, gameTime);
        broadcastMessage(message,users.get(idGame));
    }

    private void sendMiss(Stats stats, int idGame, String time, int points){
        String commentText ="";
        Message message;
        if(points == 1){
            stats.setMissFt(stats.getMissFt()+1);
            commentText =" 1 free throw missed";
            message = new Message((MessageType.RECEIVE_MISS_1));
        }else if(points==2){
            stats.setMiss2p(stats.getMiss2p()+1);
            commentText = " 2 points missed";
            message = new Message((MessageType.RECEIVE_MISS_2));
        }else{
            stats.setMiss3p(stats.getMiss3p()+1);
            commentText = " 3 points missed";
            message = new Message((MessageType.RECEIVE_MISS_3));
        }
        stats.computeEfficiency();
        Stats newStats = service.saveStats(stats);
        LiveGame liveGame= getGameById(idGame).getLiveGame();

        createAndBroadcastComment(idGame, time, commentText, message, newStats, liveGame);
    }

    private void sendScore(Stats stats, int idGame, String time, int points){
        Game game = getGameById(idGame);
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
            message = new Message((MessageType.RECEIVE_SCORE_1));
        }else if(points==2){
            stats.setMade2p(stats.getMade2p()+1);
            commentText = " 2 points made";
            message = new Message((MessageType.RECEIVE_SCORE_2));
        }else{
            stats.setMade3p(stats.getMade3p()+1);
            commentText = " 3 points made";
            message = new Message((MessageType.RECEIVE_SCORE_3));
        }
        stats.computeEfficiency();
        Stats newStats = service.saveStats(stats);
        liveGame.setTime(time);
        LiveGame newLiveGame = service.saveLiveGame(liveGame);

        createAndBroadcastComment(idGame, time, commentText, message, newStats, newLiveGame);
    }

    private void sendSubstitution(String playersId, int idGame, String time){
        String[] playersIdComponents= playersId.split(",");
        Player playerOut = service.findPlayerById(Integer.parseInt(playersIdComponents[0]));
        playerOut.setOnCourt(false);
        service.savePlayer(playerOut);
        Player playerIn = service.findPlayerById(Integer.parseInt(playersIdComponents[1]));
        playerIn.setOnCourt(true);
        service.savePlayer(playerIn);
        LiveGame liveGame = getGameById(idGame).getLiveGame();
        String object = playersId + ","+playerIn.getIdTeam();
        Comments comments = new Comments(
                "#"+playerOut.getNumber()+ " "+playerOut.getName()+
                " OUT, #" + playerIn.getNumber()+ " " + playerIn.getName() + " IN",
                idGame,liveGame.getQuater(),time,
                liveGame.getPoints1()+"-"+ liveGame.getPoints2(), playerIn.getIdTeam());
        Comments savedComment = service.saveComments(comments);
        Message message = new Message(MessageType.RECEIVE_SUBSTITUTION, savedComment, (Object)object);
        broadcastMessage(message, users.get(idGame));
    }

    private void createAndBroadcastComment(int idGame, String time, String commentText, Message message, Stats newStats, LiveGame liveGame) {
        Comments comments = new Comments(
                "#"+newStats.getPlayer().getNumber()+
                        " "+newStats.getPlayer().getName()+
                        commentText,
                idGame,liveGame.getQuater(),time,
                liveGame.getPoints1()+"-"+ liveGame.getPoints2(), newStats.getPlayer().getIdTeam());
        Comments savedComment = service.saveComments(comments);
        message.setComment(savedComment);
        message.setObject(newStats);
        broadcastMessage(message, users.get(idGame));
    }

    private Game getGameById(int idGame){
        for (Game game1 : games) {
            if(game1.getIdGame()==idGame){
                return game1;
            }
        }
        return null;
    }

    private void setReconnectAdmin(int idGame, WebSocket conn){
        List<WebSocket> list = users.get(idGame);
        list.set(0, conn);
    }

    private void broadcastMessage(Message msg, List<WebSocket> guests) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String messageJson = mapper.writeValueAsString(msg);
            for (WebSocket guest : guests) {
                guest.send(messageJson);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageToOneUser(Message msg, WebSocket conn){
        ObjectMapper mapper = new ObjectMapper();
        try {
            String messageJson = mapper.writeValueAsString(msg);
            conn.send(messageJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }



}
