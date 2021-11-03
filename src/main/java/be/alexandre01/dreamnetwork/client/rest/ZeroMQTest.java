package be.alexandre01.dreamnetwork.client.rest;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ZeroMQTest {
    public static void main(String[] args) throws Exception
    {
        try (ZContext context = new ZContext()) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < 5; i++) {
                sb.append("ABCDEFGHIJKLMNOPQRST");
            }
            // Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(SocketType.PUB);
            socket.connect("tcp://4.tcp.ngrok.io:16633");
            int i = 0;
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(1000/30);
                // Send a response
                i++;
                String response = sb.toString()+" Req:("+i+")";

                socket.sendMore("kitty cats");
                socket.send(response.getBytes(ZMQ.CHARSET), 0);
                System.out.println("Req: "+ i);
            }
        }
    }
}
