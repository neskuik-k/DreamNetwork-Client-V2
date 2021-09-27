package be.alexandre01.dreamnetwork.client.connection.request;


import be.alexandre01.dreamnetwork.client.utils.messages.Message;

public interface RequestFutureResponse {
    void onReceived(ReceivedPacket receivedPacket);
}
