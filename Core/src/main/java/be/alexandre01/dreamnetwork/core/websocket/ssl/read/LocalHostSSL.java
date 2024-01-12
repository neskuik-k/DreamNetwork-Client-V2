package be.alexandre01.dreamnetwork.core.websocket.ssl.read;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;
import java.io.InputStream;
import java.io.InputStreamReader;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 11/01/2024 at 16:21
*/
public class LocalHostSSL extends AutoReadSSL {
    @Override
    public SslContext read() throws RuntimeException {
        InputStream crt = getClass().getClassLoader().getResourceAsStream("ssl/localhost.crt");
        InputStream key = getClass().getClassLoader().getResourceAsStream("ssl/localhost.key");
        try {
            return SslContextBuilder.forServer(crt,key).build();
        } catch (SSLException e) {
            throw new RuntimeException(e);
        }
    }
}
