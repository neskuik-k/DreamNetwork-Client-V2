package be.alexandre01.dreamnetwork.api.service;

import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;
import lombok.Setter;

public class ExecutorCallbacks {
    public ICallbackStart onStart;
    public ICallbackStop onStop;
    public ICallbackConnect onConnect;

    @Setter IService jvmService = null;
    boolean hasStarted = false;

    public ICallbackFail onFail;
    public ExecutorCallbacks whenStart(ICallbackStart onStart){
        this.onStart = onStart;
        if(jvmService != null && !hasStarted){
            onStart.whenStart(jvmService);
            hasStarted = !hasStarted;
        }
        return this;
    }

    public ExecutorCallbacks whenStop(ICallbackStop onStop){
        this.onStop = onStop;
        return this;
    }

    public ExecutorCallbacks whenConnect(ICallbackConnect onConnect){
        this.onConnect = onConnect;
        return this;
    }

    public ExecutorCallbacks whenFail(ICallbackFail onFail){
        this.onFail = onFail;
        return this;
    }



    public abstract static class ICallbackStart{
        public abstract void whenStart(IService service);
    }

    public abstract static class ICallbackStop{
        public abstract void whenStop(IService service);
    }

    public abstract static class ICallbackConnect{
        public abstract void whenConnect(IService service, AServiceClient client);
    }

    public abstract static class ICallbackFail{
        public abstract void whenFail();
    }

}
