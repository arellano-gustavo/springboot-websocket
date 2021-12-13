package mx.qbits.websocket.config;

import java.io.IOException;
//import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

//import org.springframework.stereotype.Component; // como le hice un "new" no es necesario que sea componente
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

//@Component
public class SocketTextHandler extends TextWebSocketHandler {
    private List<WebSocketSession> sessions = new CopyOnWriteArrayList<>(); // antes era sólo ArrayList
    
    public SocketTextHandler() {
        this.avisoPeriodico();
    }
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.sessions.add(session);
        this.broadcastMsg("Nueva sesión con ID: "+session.getId());
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus cs) throws Exception {
        this.broadcastMsg("Cerrando sesión con ID: "+session.getId());
        this.sessions.remove(session);
    }
    
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)    throws InterruptedException, IOException {
        this.broadcastMsg("Hola " + message.getPayload() + " muy buenos dias !!!");
    }

    private void broadcastMsg(String msg) throws IOException {
        for(WebSocketSession webSocketSession : this.sessions) {
            webSocketSession.sendMessage(new TextMessage(msg));
        }               
    }
    
    private void avisoPeriodico() {
      Thread thread = new Thread(){
        public void run(){
            try {
                while(true) {
                    broadcastMsg("La hora es:" + System.currentTimeMillis());
                    sleep(2*60*1000);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
      };
      thread.start();
    }
    
}
