package be.alexandre01.dreamnetwork.client.rest;

import io.nats.client.Connection;
import io.nats.client.Nats;

import java.io.IOException;

public class NatsTest {

    public static void main(String[] args){
        try {
            Connection nc = Nats.connect("nats://185.171.202.240:4222");
            System.out.println(nc.getStatus());
            System.out.println(nc);

            nc.publish("Hello","Hello".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
