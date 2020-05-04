package main.websocket;

public enum  MessageType {
    ADMIN_JOINED, USER_JOINED, USER_LEFT, ACTIVE_USERS,
    SEND_START_GAME, SEND_END_GAME, RECEIVE_START_GAME, RECEIVE_END_GAME,
    ADMIN_SUCCESSFULLY_JOINED, GUEST_SUCCESSFULLY_JOINED, ADMIN_SUCCESSFULL_REFRESH,
    SEND_SCORE_1, SEND_SCORE_2, SEND_SCORE_3, RECEIVE_SCORE_1, RECEIVE_SCORE_2, RECEIVE_SCORE_3,
    SEND_MISS_1, SEND_MISS_2, SEND_MISS_3, RECEIVE_MISS_1, RECEIVE_MISS_2, RECEIVE_MISS_3,
    SEND_REBOUND,RECEIVE_REBOUND,
    SEND_ASSSIST, RECEIVE_ASSIST,
    SEND_TURNOUVER, RECEIVE_TURNOVER,
    SEND_TIMEOUT, RECEIVE_TIMEOUT,



}
