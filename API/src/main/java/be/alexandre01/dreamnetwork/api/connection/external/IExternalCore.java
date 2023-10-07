package be.alexandre01.dreamnetwork.api.connection.external;

import be.alexandre01.dreamnetwork.api.connection.external.handler.IExternalClientHandler;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;

import java.util.logging.Level;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 07/10/2023 at 14:43
*/
public interface IExternalCore {
    void sendMessage(String message, Level level);

    void sendMessage(String message);

    void writeAndFlush(Message message);

    void exitMode();

    void init();


    String getConnectionID();

    IExternalClientHandler getClientHandler();

    be.alexandre01.dreamnetwork.api.console.Console getConsole();

    boolean isInit();

    boolean isConnected();

    String getIp();

    CoreNetServer getServer();

    //void setServerConnection(ExternalServer serverConnection);

    void setConnectionID(String connectionID);

    void setClientHandler(IExternalClientHandler clientHandler);

    void setConsole(be.alexandre01.dreamnetwork.api.console.Console console);

    void setInit(boolean isInit);

    void setConnected(boolean isConnected);

    void setIp(String ip);

    void setServer(CoreNetServer server);
}
