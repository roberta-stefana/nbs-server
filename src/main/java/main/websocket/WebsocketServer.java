package main.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.net.InetSocketAddress;
import java.util.*;

@Component
public class WebsocketServer extends WebSocketServer {
    @Autowired
    private IService service;
    private Map<Integer, List<WebSocket>> users; //idGame si lista de guests
    private List<Game> games;

    public WebsocketServer() {
        super(new InetSocketAddress(443));
        users = new HashMap<>();
        games = new ArrayList<>();
        this.setReuseAddr(true);
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
            Game newGame = service.adminJoined(idGame);
            games.add(newGame);

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

        GameStatsDTO gameStatsDTO = service.addUser(game, activeUsers);
        games.set( games.indexOf(game) , gameStatsDTO.getGame());

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
            }else{
                boolean isRemoved = list.remove(conn);
                if(isRemoved) {
                    Game game = getGameById(idGame);
                    Game newGame = service.setActiveUsersGame(game, game.getLiveGame().getActiveUsers()-1);
                    games.set( games.indexOf(game) , newGame);

                    Message message = new Message(MessageType.ACTIVE_USERS, newGame.getLiveGame().getActiveUsers());
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
        service.endGame(game);

        List<WebSocket> list = users.get(idGame);
        Message message = new Message(MessageType.RECEIVE_END_GAME);
        broadcastMessage(message, list);

        users.remove(idGame);
        games.remove(game);
    }

    private void sendChangeQuater(int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_CHANGE_QUATER);
        Game game = getGameById(idGame);
        List<Object> gameAndComment = service.changeQuater(game, time);
        games.set( games.indexOf(game) , (Game)gameAndComment.get(0));
        message.setComment((Comments)gameAndComment.get(1));
        broadcastMessage(message, users.get(idGame));
    }

    private void sendOffRebound(Stats stats, int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_STATS_UPDATE);
        String textComment =" OFFENSIVE REBOUND";
        Stats newStats= service.updateStats(stats, "OFF REB");
        LiveGame liveGame = getGameById(idGame).getLiveGame();
        createAndBroadcastComment(idGame,time, textComment, message, newStats, liveGame);
    }

    private void sendDefRebound(Stats stats, int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_STATS_UPDATE);
        String textComment =" DEFENSIVE REBOUND";
        Stats newStats= service.updateStats(stats, "DEF REB");
        LiveGame liveGame = getGameById(idGame).getLiveGame();
        createAndBroadcastComment(idGame,time, textComment, message, newStats, liveGame);
    }

    private void sendBlockedShot(Stats stats, int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_STATS_UPDATE);
        String textComment =" BLOCKED SHOT";
        Stats newStats= service.updateStats(stats, "BS");
        LiveGame liveGame = getGameById(idGame).getLiveGame();
        createAndBroadcastComment(idGame,time, textComment, message, newStats, liveGame);
    }

    private void sendAssist(Stats stats, int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_STATS_UPDATE);
        String textComment =" ASSIST";
        Stats newStats= service.updateStats(stats, "AS");
        LiveGame liveGame = getGameById(idGame).getLiveGame();
        createAndBroadcastComment(idGame,time, textComment, message, newStats, liveGame);
    }

    private void sendSteal(Stats stats, int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_STATS_UPDATE);
        String textComment =" STEAL";
        Stats newStats= service.updateStats(stats, "ST");
        LiveGame liveGame = getGameById(idGame).getLiveGame();
        createAndBroadcastComment(idGame,time, textComment, message, newStats, liveGame);
    }

    private void sendTurnover(Stats stats, int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_STATS_UPDATE);
        String textComment =" TURNOVER";
        Stats newStats= service.updateStats(stats, "TO");
        LiveGame liveGame = getGameById(idGame).getLiveGame();
        createAndBroadcastComment(idGame,time, textComment, message, newStats, liveGame);
    }

    private void sendFoul(Stats stats, int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_STATS_UPDATE);
        String textComment =" PERSONAL FOUL";
        Stats newStats= service.updateStats(stats, "PF");
        LiveGame liveGame = getGameById(idGame).getLiveGame();
        createAndBroadcastComment(idGame,time, textComment, message, newStats, liveGame);
    }

    private void sendFoulDrawn(Stats stats, int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_STATS_UPDATE);
        String textComment =" FOUL DRAWN";
        Stats newStats= service.updateStats(stats, "FD");
        LiveGame liveGame = getGameById(idGame).getLiveGame();
        createAndBroadcastComment(idGame,time, textComment, message, newStats, liveGame);
    }

    private void sendPlayersTime(List<Stats> statsList, int idGame, String time){
        String[] timeComponents= time.split("/");
        String gameTime = timeComponents[1];

        Game game = getGameById(idGame);
        List<Object> objects = service.updatePlayersTime(game, statsList, time);
        games.set( games.indexOf(game) , (Game)objects.get(0));
        List<Stats> savedStats = (List<Stats>)objects.get(1);

        Message message = new Message(MessageType.RECEIVE_PLAYERS_TIME, savedStats, gameTime);
        broadcastMessage(message,users.get(idGame));
    }

    private void sendMiss(Stats stats, int idGame, String time, int points){
        List<Object> objects = service.updateMissShot(points, stats);
        String commentText = (String)objects.get(0);
        Stats newStats = (Stats)objects.get(1);
        LiveGame liveGame= getGameById(idGame).getLiveGame();
        Message message;
        if(points == 1){
            message = new Message((MessageType.RECEIVE_MISS_1));
        }else if(points==2){
            message = new Message((MessageType.RECEIVE_MISS_2));
        }else{
            message = new Message((MessageType.RECEIVE_MISS_3));
        }
        createAndBroadcastComment(idGame, time, commentText, message, newStats, liveGame);
    }

    private void sendScore(Stats stats, int idGame, String time, int points){
        Message message;
        if(points == 1){
            message = new Message((MessageType.RECEIVE_SCORE_1));
        }else if(points==2){
            message = new Message((MessageType.RECEIVE_SCORE_2));
        }else{
            message = new Message((MessageType.RECEIVE_SCORE_3));
        }
        Game game = getGameById(idGame);
        List<Object> objects = service.updateScoreShot(game, stats, points, time);
        Game newGame = (Game)objects.get(0);
        games.set( games.indexOf(game) , newGame);
        Stats newStats = (Stats) objects.get(1);
        String commentText = (String)objects.get(2);

        createAndBroadcastComment(idGame, time, commentText, message, newStats, newGame.getLiveGame());
    }

    private void sendSubstitution(String playersId, int idGame, String time){
        List<Object> objects =  service.updateSubstitution(playersId, getGameById(idGame), time);
        String playersIdAndTeam = (String)objects.get(0);
        Comments savedComment = (Comments)objects.get(1);
        Message message = new Message(MessageType.RECEIVE_SUBSTITUTION, savedComment, (Object)playersIdAndTeam);
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
