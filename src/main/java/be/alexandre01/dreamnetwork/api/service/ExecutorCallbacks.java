package be.alexandre01.dreamnetwork.api.service;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;

public class ExecutorCallbacks {
    public ICallbackStart onStart;
    public ICallbackStop onStop;
    public ICallbackConnect onConnect;

    public ICallbackFail onFail;
    public ExecutorCallbacks whenStart(ICallbackStart onStart){
        this.onStart = onStart;
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

    public abstract class ICallbackConnect{
        public abstract void whenConnect(IService service, IClient client);
    }

    public abstract static class ICallbackFail{
        public abstract void whenFail();
    }

}
