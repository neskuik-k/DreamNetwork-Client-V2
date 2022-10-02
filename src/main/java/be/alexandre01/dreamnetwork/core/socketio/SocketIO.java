package be.alexandre01.dreamnetwork.core.socketio;

import io.socket.client.IO;
import io.socket.client.Socket;

import java.io.InputStream;
import java.net.URI;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class SocketIO {
    static Socket socket;
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {

        System.out.println("Hello World!");
        InputStream is = SocketIO.class.getClassLoader().getResourceAsStream("pub_key");
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();
        PublicKey publicKey = pair.getPublic();
        // read all bytes from the input stream
           byte[] bytes = new byte[0];
              try {
                bytes = new byte[is.available()];
                is.read(bytes);
              } catch (Exception e) {
                e.printStackTrace();
              }
// convert the bytes to a key
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bytes);
        keyFactory.generatePublic(publicKeySpec);

        System.out.println(keyFactory.getAlgorithm());


    }

    public static void connectSocket(){
        new Thread(){
            @Override
            public void run() {
                URI uri = URI.create("http://localhost:8080/");
                IO.Options options = IO.Options.builder()
                        .setPath("/audio/")
                        .build();

                socket = IO.socket(uri, options);
                System.out.println(socket.isActive());
                long start = System.currentTimeMillis();

                socket.connect();
                /*while (socket.isActive()){
                    if(System.currentTimeMillis() - start > 5000){
                        socket.disconnect();
                        return;
                    }
                    System.out.println(socket.isActive());
                }*/
                socket.emit("connected", "Hello World!");
            }
        }.start();

    }
}
