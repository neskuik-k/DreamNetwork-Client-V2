package be.alexandre01.dreamnetwork.core.commands.lists.sub.hypervisor;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommand;
import be.alexandre01.dreamnetwork.api.commands.sub.types.CustomType;
import be.alexandre01.dreamnetwork.api.config.WSSettings;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.console.IConsoleReader;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.YamlFileUtils;
import be.alexandre01.dreamnetwork.core.connection.external.ExternalCore;
import be.alexandre01.dreamnetwork.core.websocket.WebSocketRun;
import be.alexandre01.dreamnetwork.core.websocket.WebSocketServer;
import be.alexandre01.dreamnetwork.core.websocket.sessions.WebSessionManager;
import be.alexandre01.dreamnetwork.core.websocket.sessions.frames.*;
import lombok.NonNull;

import java.util.Optional;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Open extends SubCommand {
    public Open(Command command) {
        super(command);
        String[] nodeClazz = CustomType.getCustomTypes().keySet().stream().map(Class::getSimpleName).toArray(String[]::new);
        NodeBuilder nodeBuilder = new NodeBuilder(
                create(value,
                        create("open")));
    }

    @Override
    public boolean onSubCommand(@NonNull String[] args) {


        boolean b = when(sArgs -> {
            if(sArgs.length == 1){
                Console.printLang("commands.hypervisor.specifyModule");
                return true;
            }

            String token = args[1];

            int port = 2352;

            if(args.length > 2){
                port = Integer.parseInt(args[2]);
            }else {
                Optional<WSSettings> settings = YamlFileUtils.getStaticFile(WSSettings.class);
                if(settings.isPresent()){
                    port = settings.get().getPort();
                }
            }

            new WebSocketServer(port, "localhost",token).start();
            WebSessionManager.getInstance().onNewSession(session -> {
                session.getFrameManager().addFrame("overview", new OverViewFrame(session));
                session.getFrameManager().addFrame("players", new PlayersFrame(session));
                session.getFrameManager().addFrame("executors", new ExecutorsFrame(session));
                session.getFrameManager().addFrame("services", new ServicesFrame(session));
                session.getFrameManager().addFrame("innerService", new InnerServiceFrame(session));
                session.onRead(message -> {

                    System.out.println("New message read !");
                    System.out.println("Are you sure ?");
                    session.getFrameManager().handleCurrentFrame(message);
                });
            });

            return true;
        }, args,"open","[ip]");
        return b;
    }

    public void reloadNode(){
        IConsoleReader.reloadCompleters();
    }
}
