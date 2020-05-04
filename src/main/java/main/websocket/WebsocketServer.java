package main.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.model.Comments;
import main.model.Game;
import main.model.LiveGame;
import main.model.Stats;
import main.model.dto.GameStatsDTO;
import main.model.dto.StatsGameDTO;
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
        Stats newStats = service.saveStats(stats);
        liveGame.setTime(time);
        LiveGame newLiveGame = service.saveLiveGame(liveGame);

        createAndBroadcastComment(idGame, time, commentText, message, newStats, newLiveGame);
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
