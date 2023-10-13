package be.alexandre01.dreamnetwork.api.service;

import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ExecutorCallbacks {

    @Setter private boolean hasFailed = false;
    public List<ICallbackStart> onStart;
    public List<ICallbackStop> onStop;
    public List<ICallbackConnect> onConnect;
    public List<ICallbackFail> onFail;

    @Setter IService jvmService = null;
    boolean hasStarted = false;


    public ExecutorCallbacks whenStart(ICallbackStart onStart){
        if(this.onStart == null){
            this.onStart = new ArrayList<>();
        }
        this.onStart.add(onStart);
        if(jvmService != null && !hasStarted){
            onStart.whenStart(jvmService);
            hasStarted = !hasStarted;
        }
        return this;
    }

    public ExecutorCallbacks whenStop(ICallbackStop onStop){
        if(this.onStop == null){
            this.onStop = new ArrayList<>();
        }
        this.onStop.add(onStop);
        return this;
    }

    public ExecutorCallbacks whenConnect(ICallbackConnect onConnect){
        if(this.onConnect == null){
            this.onConnect = new ArrayList<>();
        }
        this.onConnect.add(onConnect);
        return this;
    }

    public ExecutorCallbacks whenFail(ICallbackFail onFail){
        if(this.onFail == null){
            this.onFail = new ArrayList<>();
        }
        this.onFail.add(onFail);
        if(hasFailed){
            onFail.whenFail();
        }
        return this;
    }



    public interface ICallbackStart{
        void whenStart(IService service);
    }

    public interface ICallbackStop{
        void whenStop(IService service);
    }

    public interface ICallbackConnect{
        void whenConnect(IService service, AServiceClient client);
    }

    public interface ICallbackFail{
        void whenFail();
    }

}
