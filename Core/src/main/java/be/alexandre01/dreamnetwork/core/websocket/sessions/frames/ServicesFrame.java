package be.alexandre01.dreamnetwork.core.websocket.sessions.frames;

import be.alexandre01.dreamnetwork.api.service.ExecutorCallbacks;
import be.alexandre01.dreamnetwork.api.service.IExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import be.alexandre01.dreamnetwork.api.utils.messages.WebMessage;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.service.screen.ScreenManager;
import be.alexandre01.dreamnetwork.core.websocket.sessions.FrameAbstraction;
import be.alexandre01.dreamnetwork.core.websocket.sessions.WebSession;

public class ServicesFrame extends FrameAbstraction {
    IExecutor currentExecutor;

    ExecutorCallbacks.ICallbackStart startHandler = service -> {
        getSession().send(composeNewService(service));
    };

    ExecutorCallbacks.ICallbackStop stopHandler = service -> {
        getSession().send(new WebMessage()
                .put("instruction","remove")
                .put("name", service.getFullIndexedName())
        );
    };
    public ServicesFrame(WebSession session) {
        super(session, "services");
    }

    public WebMessage composeNewService(IService service){
        return new WebMessage()
                .put("name", service.getFullIndexedName())
                .put("type", service.getType().name())
                .put("players", "N/A")
                .put("xmx", service.getXmx())
                .put("xms", service.getXms())
                .put("startup", service.getUsedConfig().getStartup())
                .put("time",service.getElapsedTime())
                .put("instruction","add");
    }
    @Override
    public void handle(WebMessage webMessage) {
        System.out.println("Handling ServicesFrame : " + webMessage);
        if(webMessage.containsKey("executor")) {
            IExecutor executor = Core.getInstance().getJvmContainer().findExecutor(webMessage.getString("executor")).orElse(null);
            if(executor != null){
                currentExecutor = executor;
                for (IService service : executor.getServices()){
                    getSession().send(composeNewService(service));
                }
                executor.onNewServiceStart(startHandler);
                executor.onServiceStop(stopHandler);
            }
        }
    }

    @Override
    public void onEnter() {

    }

    @Override
    public void onLeave() {
        System.out.println("Leave ServicesFrame");
        if(currentExecutor == null) return;
        currentExecutor.removeCallback(startHandler);
        currentExecutor.removeCallback(stopHandler);
        currentExecutor = null;
    }
}
