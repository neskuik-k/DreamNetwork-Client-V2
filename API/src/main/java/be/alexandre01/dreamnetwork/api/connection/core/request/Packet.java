package be.alexandre01.dreamnetwork.api.connection.core.request;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 06/09/2023 at 10:16
*/
public interface Packet {
    public Message getMessage();
    public String getProvider();
    public IClient getReceiver();
}
