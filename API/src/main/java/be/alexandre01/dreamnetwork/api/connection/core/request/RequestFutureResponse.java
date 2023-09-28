package be.alexandre01.dreamnetwork.api.connection.core.request;


import be.alexandre01.dreamnetwork.api.connection.core.channels.AChannelPacket;

public interface RequestFutureResponse {
    void onReceived(AChannelPacket receivedPacket);
}
