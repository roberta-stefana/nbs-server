package main.websocket;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.model.Comments;
import main.model.Game;
import main.model.Stats;
import main.model.dto.GameStatsDTO;
import main.service.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SocketController {

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @Autowired
    IService service;

    @MessageMapping("/send/{idGame}")
    public void gameNotifications(@DestinationVariable String idGame, @Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        switch (message.getType()) {
            case HOST_GAME:
                hostGame(Integer.parseInt(message.getIdGame()),headerAccessor.getUser().getName());
                break;
            case USER_JOINED:
                addUser(Integer.parseInt(message.getIdGame()),headerAccessor.getUser().getName());
                break;
            case USER_LEFT:
                removeUser(Integer.parseInt(message.getIdGame()));
                break;
            case SEND_START_GAME:
                sendStartGame(Integer.parseInt(message.getIdGame()));
                break;
            case SEND_END_GAME:
                sendEndGame(Integer.parseInt(message.getIdGame()));
                break;
            case SEND_SCORE_1:
                Stats stats = mapper.convertValue(message.getObject(), Stats.class);
                sendScore(stats,Integer.parseInt(message.getIdGame()), message.getTime(), 1);
                break;
            case SEND_SCORE_2:
                Stats stats2 = mapper.convertValue(message.getObject(), Stats.class);
                sendScore(stats2,Integer.parseInt(message.getIdGame()), message.getTime(), 2);
                break;
            case SEND_SCORE_3:
                Stats stats3 = mapper.convertValue(message.getObject(), Stats.class);
                sendScore(stats3,Integer.parseInt(message.getIdGame()), message.getTime(), 3);
                break;
            case SEND_MISS_1:
                Stats stats4 = mapper.convertValue(message.getObject(), Stats.class);
                sendMiss(stats4,Integer.parseInt(message.getIdGame()), message.getTime(), 1);
                break;
            case SEND_MISS_2:
                Stats stats5 = mapper.convertValue(message.getObject(), Stats.class);
                sendMiss(stats5,Integer.parseInt(message.getIdGame()), message.getTime(), 2);
                break;
            case SEND_MISS_3:
                Stats stats6 = mapper.convertValue(message.getObject(), Stats.class);
                sendMiss(stats6,Integer.parseInt(message.getIdGame()), message.getTime(), 3);
                break;
            case SEND_OFF_REBOUND:
                Stats stats7 = mapper.convertValue(message.getObject(), Stats.class);
                sendOffRebound(stats7,Integer.parseInt(message.getIdGame()), message.getTime());
                break;
            case SEND_DEF_REBOUND:
                Stats stats8 = mapper.convertValue(message.getObject(), Stats.class);
                sendDefRebound(stats8,Integer.parseInt(message.getIdGame()), message.getTime());
                break;
            case SEND_BLOCKED_SHOT:
                Stats stats9 = mapper.convertValue(message.getObject(), Stats.class);
                sendBlockedShot(stats9,Integer.parseInt(message.getIdGame()), message.getTime());
                break;
            case SEND_ASSIST:
                Stats stats10 = mapper.convertValue(message.getObject(), Stats.class);
                sendAssist(stats10,Integer.parseInt(message.getIdGame()), message.getTime());
                break;
            case SEND_STEAL:
                Stats stats14 = mapper.convertValue(message.getObject(), Stats.class);
                sendSteal(stats14,Integer.parseInt(message.getIdGame()), message.getTime());
                break;
            case SEND_TURNOVER:
                Stats stats11 = mapper.convertValue(message.getObject(), Stats.class);
                sendTurnover(stats11,Integer.parseInt(message.getIdGame()), message.getTime());
                break;
            case SEND_FOUL:
                Stats stats12 = mapper.convertValue(message.getObject(), Stats.class);
                sendFoul(stats12,Integer.parseInt(message.getIdGame()), message.getTime());
                break;
            case SEND_FOUL_DRAWN:
                Stats stats13 = mapper.convertValue(message.getObject(), Stats.class);
                sendFoulDrawn(stats13,Integer.parseInt(message.getIdGame()), message.getTime());
                break;
            case SEND_PLAYERS_TIME:
                List<Stats> statsList = new ArrayList<>();
                List list = mapper.convertValue(message.getObject(), List.class);
                list.forEach(x->statsList.add(mapper.convertValue(x, Stats.class)));
                sendPlayersTime(statsList,Integer.parseInt(message.getIdGame()), message.getTime());
                break;
            case SEND_CHANGE_QUATER:
                sendChangeQuater(Integer.parseInt(message.getIdGame()), message.getTime());
                break;
            case SEND_SUBSTITUTION:
                sendSubstitution((String)message.getObject(),Integer.parseInt(message.getIdGame()), message.getTime());
                break;
        }
    }

    private void hostGame(int idGame, String currentUser){
        GameStatsDTO gameStatsDTO = service.hostGame(idGame);
        Message message = new Message(MessageType.RECEIVE_HOST_GAME, gameStatsDTO);
        messagingTemplate.convertAndSendToUser(currentUser, "/topic/receive/"+idGame, message);
    }

    private void addUser(int idGame, String currentUser){
        System.out.println("USER JOINED --> ADD USER "+currentUser);
        GameStatsDTO gameStatsDTO = service.addUser(idGame);
        Message message = new Message(MessageType.ACTIVE_USERS, String.valueOf(gameStatsDTO.getGame().getLiveGame().getActiveUsers()));
        messagingTemplate.convertAndSend("/topic/receive/"+idGame, message);
        Message messageUser = new Message(MessageType.GUEST_SUCCESSFULLY_JOINED, gameStatsDTO);
        messagingTemplate.convertAndSendToUser(currentUser, "/topic/receive/"+idGame, messageUser);
    }

    private void removeUser(int idGame)  {
        System.out.println("REMOVE USER");
        Game game = service.findGameByIdGame(idGame);
        Game newGame = service.setActiveUsersGame(game, game.getLiveGame().getActiveUsers()-1);
        Message message = new Message(MessageType.ACTIVE_USERS, newGame.getLiveGame().getActiveUsers());
        messagingTemplate.convertAndSend("/topic/receive/"+idGame, message);
    }

    private void sendStartGame(int idGame){
        Comments comments = new Comments("Start game",idGame,1,"10:00","0-0", 1);
        service.saveComments(comments);
        Message message = new Message(MessageType.RECEIVE_START_GAME,comments);
        messagingTemplate.convertAndSend("/topic/receive/"+idGame, message);
    }

    private void sendEndGame(int idGame){
        service.endGame(idGame);
        Message message = new Message(MessageType.RECEIVE_END_GAME);
        messagingTemplate.convertAndSend("/topic/receive/"+idGame, message);
    }

    private void sendChangeQuater(int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_CHANGE_QUATER);
        List<Object> gameAndComment = service.changeQuater(idGame, time);
        message.setComment((Comments)gameAndComment.get(1));
        messagingTemplate.convertAndSend("/topic/receive/"+idGame, message);
    }

    private void sendOffRebound(Stats stats, int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_STATS_UPDATE);
        String textComment =" OFFENSIVE REBOUND";
        Stats newStats= service.updateStats(stats, "OFF REB");
        Message newMessage = service.composeComment(idGame,time, textComment, message, newStats);
        messagingTemplate.convertAndSend("/topic/receive/"+idGame, newMessage);
    }

    private void sendDefRebound(Stats stats, int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_STATS_UPDATE);
        String textComment =" DEFENSIVE REBOUND";
        Stats newStats= service.updateStats(stats, "DEF REB");
        Message newMessage = service.composeComment(idGame,time, textComment, message, newStats);
        messagingTemplate.convertAndSend("/topic/receive/"+idGame, newMessage);
    }

    private void sendBlockedShot(Stats stats, int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_STATS_UPDATE);
        String textComment =" BLOCKED SHOT";
        Stats newStats= service.updateStats(stats, "BS");
        Message newMessage = service.composeComment(idGame,time, textComment, message, newStats);
        messagingTemplate.convertAndSend("/topic/receive/"+idGame, newMessage);
    }

    private void sendAssist(Stats stats, int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_STATS_UPDATE);
        String textComment =" ASSIST";
        Stats newStats= service.updateStats(stats, "AS");
        Message newMessage = service.composeComment(idGame,time, textComment, message, newStats);
        messagingTemplate.convertAndSend("/topic/receive/"+idGame, newMessage);
    }

    private void sendSteal(Stats stats, int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_STATS_UPDATE);
        String textComment =" STEAL";
        Stats newStats= service.updateStats(stats, "ST");
        Message newMessage = service.composeComment(idGame,time, textComment, message, newStats);
        messagingTemplate.convertAndSend("/topic/receive/"+idGame, newMessage);
    }

    private void sendTurnover(Stats stats, int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_STATS_UPDATE);
        String textComment =" TURNOVER";
        Stats newStats= service.updateStats(stats, "TO");
        Message newMessage = service.composeComment(idGame,time, textComment, message, newStats);
        messagingTemplate.convertAndSend("/topic/receive/"+idGame, newMessage);
    }

    private void sendFoul(Stats stats, int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_STATS_UPDATE);
        String textComment =" PERSONAL FOUL";
        Stats newStats= service.updateStats(stats, "PF");
        Message newMessage = service.composeComment(idGame,time, textComment, message, newStats);
        messagingTemplate.convertAndSend("/topic/receive/"+idGame, newMessage);
    }

    private void sendFoulDrawn(Stats stats, int idGame, String time){
        Message message = new Message(MessageType.RECEIVE_STATS_UPDATE);
        String textComment =" FOUL DRAWN";
        Stats newStats= service.updateStats(stats, "FD");
        Message newMessage = service.composeComment(idGame,time, textComment, message, newStats);
        messagingTemplate.convertAndSend("/topic/receive/"+idGame, newMessage);
    }

    private void sendPlayersTime(List<Stats> statsList, int idGame, String time){
        String[] timeComponents= time.split("/");
        String gameTime = timeComponents[1];

        List<Object> objects = service.updatePlayersTime(idGame, statsList, time);
        List<Stats> savedStats = (List<Stats>)objects.get(1);

        Message message = new Message(MessageType.RECEIVE_PLAYERS_TIME, savedStats, gameTime);
        messagingTemplate.convertAndSend("/topic/receive/"+idGame, message);
    }

    private void sendMiss(Stats stats, int idGame, String time, int points){
        List<Object> objects = service.updateMissShot(points, stats);
        String textComment = (String)objects.get(0);
        Stats newStats = (Stats)objects.get(1);

        Message message;
        if(points == 1){
            message = new Message((MessageType.RECEIVE_MISS_1));
        }else if(points==2){
            message = new Message((MessageType.RECEIVE_MISS_2));
        }else{
            message = new Message((MessageType.RECEIVE_MISS_3));
        }
        Message newMessage = service.composeComment(idGame,time, textComment, message, newStats);
        messagingTemplate.convertAndSend("/topic/receive/"+idGame, newMessage);
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
        List<Object> objects = service.updateScoreShot(idGame, stats, points, time);
        Stats newStats = (Stats) objects.get(1);
        String textComment = (String)objects.get(2);
        Message newMessage = service.composeComment(idGame, time, textComment, message, newStats);
        messagingTemplate.convertAndSend("/topic/receive/"+idGame, newMessage);
    }

    private void sendSubstitution(String playersId, int idGame, String time){
        List<Object> objects =  service.updateSubstitution(playersId, idGame, time);
        String playersIdAndTeam = (String)objects.get(0);
        Comments savedComment = (Comments)objects.get(1);
        Message message = new Message(MessageType.RECEIVE_SUBSTITUTION, savedComment, (Object)playersIdAndTeam);
        messagingTemplate.convertAndSend("/topic/receive/"+idGame, message);
    }

}
