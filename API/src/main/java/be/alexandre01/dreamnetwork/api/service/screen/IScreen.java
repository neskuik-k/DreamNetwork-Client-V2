package be.alexandre01.dreamnetwork.api.service.screen;

import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;
import be.alexandre01.dreamnetwork.api.connection.core.communication.UniversalConnection;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.utils.buffers.FixedSizeRingBuffer;

import java.util.ArrayList;

public interface IScreen extends Runnable {
    @Override
    void run();

    void destroy(boolean fromService);

    IService getService();



    IScreenStream getScreenStream();

    ArrayList<UniversalConnection> getDevToolsReading();

    Integer getScreenId();

    String getScreenName();

    void setService(IService service);
    void setViewing(boolean viewing);



    void setDevToolsReading(ArrayList<UniversalConnection> devToolsReading);

    void setScreenStream(IScreenStream screenStream);

    void setScreenId(Integer screenId);

    void setScreenName(String screenName);

    boolean isViewing();

    String toString();

    boolean equals(Object o);

    int hashCode();
}
