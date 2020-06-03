package hello.world;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.*;
import org.reactivestreams.Publisher;

import java.util.function.Predicate;

@ServerWebSocket("/chat/{boardId}/{username}")
@Controller("/")
public class ChatServer {
    private WebSocketBroadcaster broadcaster;

    public ChatServer(WebSocketBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @Get("/hello/{boardId}/{username}")
    public void getDetails(String boardId,
                           String username) {
        System.out.println("heyaaaaaaa");
        broadcaster.broadcastSync("hello everyone - i added a sticky", isValid(boardId));
    }

    @OnOpen
    public Publisher<String> onOpen(String boardId, String username, WebSocketSession session) {
        session.getOpenSessions();
        String msg = "[" + username + "] Joined!";
        return broadcaster.broadcast(msg, isValid(boardId));
    }

    @OnMessage
    public Publisher<String> onMessage(
            String boardId,
            String username,
            String message) {
        System.out.println("i am in on Message...lets broadcast something");
        String msg = "[" + username + "] " + message;
        return broadcaster.broadcast(msg, isValid(boardId));
    }

    @OnClose
    public Publisher<String> onClose(
            String boardId,
            String username,
            WebSocketSession session) {
        String msg = "[" + username + "] Disconnected!";
        return broadcaster.broadcast(msg, isValid(boardId));
    }

    @OnError
    public void onError(WebSocketSession session, Throwable throwable) {
        System.out.println("This resulted in error");
    }

    private Predicate<WebSocketSession> isValid(String boardId) {
//        System.out.println(boardId.equalsIgnoreCase(s.getUriVariables().get("boardId", String.class, null)));
        return (WebSocketSession s) -> {
            System.out.println(s.getUriVariables().names());
            return boardId.equalsIgnoreCase(s.getUriVariables().get("boardId", String.class, null));
        };
    }

}