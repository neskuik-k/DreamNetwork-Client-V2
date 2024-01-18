package be.alexandre01.dreamnetwork.core.websocket.sessions.frames;

import be.alexandre01.dreamnetwork.api.connection.core.players.Player;
import be.alexandre01.dreamnetwork.api.service.IExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.utils.messages.WebMessage;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.service.JVMExecutor;
import be.alexandre01.dreamnetwork.core.websocket.sessions.FrameAbstraction;
import be.alexandre01.dreamnetwork.core.websocket.sessions.WebSession;

import java.util.ArrayList;
import java.util.function.Consumer;

public class ExecutorsFrame extends FrameAbstraction {


    public ExecutorsFrame(WebSession session) {
        super(session, "executors");
    }

    @Override
    public void handle(WebMessage webMessage) {

    }

    @Override
    public void onEnter() {
        System.out.println("Enter ExecutorsFrame");
        System.out.println("Executors : " + Core.getInstance().getJvmContainer().getExecutors().size());
        for (IExecutor executor : Core.getInstance().getJvmContainer().getExecutors()){
            System.out.println("Executor : " + executor.getName());
            /*int players = 0; Dev player API in CORE
            for (IService service : executor.getServices()){
                players += Core.getInstance().getServicePlayersManager().
            }*/
            ArrayList<String> profiles = new ArrayList<>();
            executor.getJvmProfiles().ifPresent(iProfiles -> {
                System.out.println("present !");
                if (iProfiles.getProfiles() != null) {
                    iProfiles.getProfiles().forEach((string, iConfig) -> {
                        profiles.add(string);
                    });
                }

            });
            System.out.println("Profiles : " + profiles);


            getSession().send(new WebMessage()
                    .put("bundle", executor.getBundleData().getName())
                    .put("name", executor.getName())
                    .put("type", executor.getType().name())
                    .put("instances", executor.getServices().size())
                    .put("xmx", executor.getXmx())
                    .put("xms", executor.getXms())
                    .put("startup", executor.getStartup())
                    .put("customName", executor.getCustomName())
                    .put("port", executor.getPort())
                    .put("jvmType",executor.getBundleData().getJvmType())
                    .put("profiles", profiles)
            );

            System.out.println("Sended message");

        }
    }

    @Override
    public void onLeave() {
        System.out.println("Leave ExecutorsFrame");
    }
}
