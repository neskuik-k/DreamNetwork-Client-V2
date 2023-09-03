package be.alexandre01.dreamnetwork.api.service.screen;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.service.IService;

import java.util.ArrayList;

public interface IScreen extends Runnable {
    @Override
    void run();

    void destroy();

    IService getService();

    ArrayList<String> getHistory();

    IScreenStream getScreenStream();

    ArrayList<IClient> getDevToolsReading();

    Integer getScreenId();

    String getScreenName();

    void setService(IService service);

    void setHistory(ArrayList<String> history);

    void setDevToolsReading(ArrayList<IClient> devToolsReading);

    void setScreenStream(IScreenStream screenStream);

    void setScreenId(Integer screenId);

    void setScreenName(String screenName);

    boolean isViewing();

    String toString();

    boolean equals(Object o);

    int hashCode();
}
