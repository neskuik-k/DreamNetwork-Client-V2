package be.alexandre01.dreamnetwork.api.connection.core.request;

import be.alexandre01.dreamnetwork.api.connection.core.communication.UniversalConnection;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.CompletableFuture;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 06/09/2023 at 10:16
*/
public interface Packet {
    public Message getMessage();
    public String getProvider();
    public UniversalConnection getReceiver();

    default Packet dispatch(){
        getReceiver().dispatch(this);
        return this;
    }

    default Packet dispatch(GenericFutureListener<? extends Future<? super Void>> future){
        getReceiver().dispatch(this,future);
        return this;
    }
}
