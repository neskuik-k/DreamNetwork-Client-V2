package be.alexandre01.dreamnetwork.core.websocket;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 20/11/2023 at 10:41
*/
public class WebSocketRun {
    public static void main(String[] args) {
        new WebSocketServer(8081, "localhost").start();
    }
}
