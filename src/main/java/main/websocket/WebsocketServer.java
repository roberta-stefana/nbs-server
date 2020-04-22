package main.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.model.Game;
import main.model.LiveGame;
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
    private Map<Integer, List<WebSocket>> users;
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

        //System.out.println("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Message msg = mapper.readValue(message, Message.class);

            switch (msg.getType()) {
                case ADMIN_JOINED:
                    adminJoined(Integer.parseInt((String)msg.getObject()), conn);
                    break;
                case USER_JOINED:
                    addUser(Integer.parseInt((String)msg.getObject()), conn);
                    break;
                case USER_LEFT:
                    removeUser(conn);
                    break;
                case SCORE_1:
                    //broadcastMessage(msg);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        //System.out.println("ERROR from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
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
        }else{
            setReconnectAdmin(idGame, conn);
            Message message = new Message(MessageType.ADMIN_SUCCESSFULLY_JOINED, game);
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
        game.setLiveGame(newLiveGame);


        Message messageUser = new Message(MessageType.GUEST_SUCCESSFULLY_JOINED, game);
        sendMessageToOneUser(messageUser, conn);
        broadcastMessage(message, guests);
    }

    private void removeUser(WebSocket conn)  {
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
