package com.nobody.nobodyplace.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/notice/{userId}")
public class WebSocketServer {

    //存储客户端session信息
    public static Map<String, Session> clients = new ConcurrentHashMap<>();

    //存储把不同用户的客户端session信息集合
    public static Map<String, Set<String>> connection = new ConcurrentHashMap<>();

    //会话id
    private String sid = null;

    //建立连接的用户id
    private String userId;

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId){
        this.sid = UUID.randomUUID().toString();
        this.userId = userId;
        clients.put(this.sid, session);
        //判断该用户是否存在会话信息，不存在则添加
        Set<String> clientSet = connection.get(userId);
        if (clientSet == null){
            clientSet = new HashSet<>();
            connection.put(userId,clientSet);
        }
        clientSet.add(this.sid);
        log.info("User: " + this.userId + " connect with sid: " + this.sid);
    }

    @OnClose
    public void onClose(){
        clients.remove(this.sid);
        log.info("User: " + this.userId + " disconnect with sid: " + this.sid);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("User: " + this.userId + " with sid: " + this.sid + " sent msg: " + message);
    }

    @OnError
    public void onError(Throwable error){
        log.info("Err");
        error.printStackTrace();
    }

    /**
     * 通过userId向用户发送信息
     * 该类定义成静态可以配合定时任务实现定时推送
     **/
    public static void sendMessageByUserId(String userId, String message){
//        log.info("Informing User: {} with msg: {}", userId, message);
        if (!StringUtils.isEmpty(userId)) {
            Set<String> clientSet = connection.get(userId);
            //用户是否存在客户端连接
            if (Objects.nonNull(clientSet)) {
                for (String sid : clientSet) {
                    Session session = clients.get(sid);
                    //向每个会话发送消息
                    if (Objects.nonNull(session)) {
                        try {
                            //同步发送数据，需要等上一个sendText发送完成才执行下一个发送
                            session.getBasicRemote().sendText(message);
                        } catch (IOException e) {
                            log.info("sendMessageByUserId... Err {}", e.getMessage());
                        }
                    }
                }
            }
        }
    }
}
