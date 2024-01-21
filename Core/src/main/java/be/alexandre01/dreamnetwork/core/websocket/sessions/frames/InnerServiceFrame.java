package be.alexandre01.dreamnetwork.core.websocket.sessions.frames;

import be.alexandre01.dreamnetwork.api.service.ExecutorCallbacks;
import be.alexandre01.dreamnetwork.api.service.IExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenInReader;
import be.alexandre01.dreamnetwork.api.utils.messages.WebMessage;
import be.alexandre01.dreamnetwork.api.utils.optional.Facultative;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.websocket.sessions.FrameAbstraction;
import be.alexandre01.dreamnetwork.core.websocket.sessions.WebSession;

import java.io.IOException;

public class InnerServiceFrame extends FrameAbstraction {
    IService currentService;
    IScreenInReader.ReaderLine readerLine = line -> {
        System.out.println("Instruction : " + line);
        getSession().send(new WebMessage()
                .put("instruction","console")
                .put("line", line)
        );
        return line;
    };

    public InnerServiceFrame(WebSession session) {
        super(session, "services");
    }

    Runnable stopHandler = () -> {
        getSession().send(new WebMessage()
                .put("instruction","remove")
        );
    };
    @Override
    public void handle(WebMessage webMessage) {
        System.out.println("Handling ServicesFrame : " + webMessage);
        if(webMessage.containsKey("service")) {
            if(webMessage.containsKey("instruction")){
                if(webMessage.getString("instruction").equals("stop")){
                    currentService.stop().whenComplete((aBoolean, throwable) -> {
                        if(aBoolean){
                            System.out.println("Stop succeed");
                            /*getSession().send(new WebMessage()
                                    .put("instruction","remove")
                            );*/
                        }else{
                            System.out.println("Stop failed");
                        }
                    });
                    return;
                }
            }
            System.out.println("Service : " + webMessage.getString("service"));
            IService service = Core.getInstance().getJvmContainer().findService(webMessage.getString("service")).orElse(null);
            if(service != null){
                System.out.println("Yey service found !");
                currentService = service;
                handlingConsole();
                System.out.println("Screen : " + service.getScreen());
                service.onStop(stopHandler);
                System.out.println("Stop handler added");

            }
        }

        if(webMessage.containsKey("console")){
            if(webMessage.containsKey("command")){
                try {
                    currentService.getScreen().getScreenStream().getScreenOutWriter().writeOnConsole(webMessage.getString("command"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void handlingConsole(){
        IScreen screen = currentService.getScreen();
        System.out.println("Screen : " + screen);
        if(screen == null || !screen.isViewing()) {
            System.out.println("Bouhouhou no screen");
            getSession().send(new WebMessage()
                    .put("instruction","noConsole")
            );
            return;
        }
        System.out.println("Handling console");
        // sending history
        try {
            screen.getScreenStream().getConsole().getHistory().forEach(s -> {
                if(s == null) return;
                if(s.content == null) return;
                getSession().send(new WebMessage()
                        .put("instruction","history")
                        .put("line", s.content)
                );
            });
        }catch (Exception e){
            System.out.println("Error while sending history");
            e.printStackTrace();
        }
        System.out.println("Add screen reader");
        screen.getScreenStream().getScreenInReader().getReaderLines().add(readerLine);
    }
    @Override
    public void onEnter() {

    }

    @Override
    public void onLeave() {
        System.out.println("Leave ServicesFrame");
        if(currentService == null) return;
        currentService.removeStopCallback(stopHandler);
        if(currentService.getScreen() == null) return;
        if(!currentService.getScreen().isViewing()) return;
        currentService.getScreen().getScreenStream().getScreenInReader().getReaderLines().remove(readerLine);
    }
}
