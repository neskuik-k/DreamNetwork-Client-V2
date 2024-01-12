package be.alexandre01.dreamnetwork.core.websocket.ssl.read;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;
import java.io.File;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 11/01/2024 at 16:12
*/
public class CustomSSL extends AutoReadSSL{

    @Override
    public SslContext read() throws RuntimeException {

        try {
            return SslContextBuilder.forServer(new File("data/certs/certfile.crt"),new File("data/certs/certfile.key")).build();
        } catch (SSLException e) {
            throw new RuntimeException(e);
        }
    }
}
