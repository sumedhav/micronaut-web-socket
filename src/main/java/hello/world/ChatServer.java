package hello.world;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.*;
import org.reactivestreams.Publisher;

import java.util.function.Predicate;

@ServerWebSocket("/chat/{spaceId}/{username}")
@Controller("/")
public class ChatServer {
    private WebSocketBroadcaster broadcaster;

    public ChatServer(WebSocketBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @Get("/hello/{spaceId}/{username}")
    public void getDetails(String spaceId,
                           String username) {
        System.out.println("heyaaaaaaa");
        broadcaster.broadcastSync("hello everyone - i added a sticky", isValid(spaceId));
    }

    @OnOpen
    public Publisher<String> onOpen(String spaceId, String username, WebSocketSession session) {
        session.getOpenSessions();
        String msg = "[" + username + "] Joined!";
        return broadcaster.broadcast(msg, isValid(spaceId));
    }

    @OnMessage
    public Publisher<String> onMessage(
            String spaceId,
            String username,
            String message) {
        System.out.println("i am in on Message...lets broadcast something");
        String msg = "[" + username + "] " + message;
        return broadcaster.broadcast(msg, isValid(spaceId));
    }

    @OnClose
    public Publisher<String> onClose(
            String spaceId,
            String username,
            WebSocketSession session) {
        String msg = "[" + username + "] Disconnected!";
        return broadcaster.broadcast(msg, isValid(spaceId));
    }

    @OnError
    public void onError(WebSocketSession session, Throwable throwable) {
        System.out.println("This resulted in error");
    }

    private Predicate<WebSocketSession> isValid(String spaceId) {
        return s -> spaceId.equalsIgnoreCase(s.getUriVariables().get("spaceId", String.class, null));
    }

}