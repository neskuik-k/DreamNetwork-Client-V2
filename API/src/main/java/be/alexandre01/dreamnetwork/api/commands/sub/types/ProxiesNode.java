package be.alexandre01.dreamnetwork.api.commands.sub.types;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.service.IContainer;
import org.jline.builtins.Completers;

import java.util.stream.Collectors;

public class ProxiesNode extends CustomType {
    public ProxiesNode(){
        type = SubCommandCompletor.Type.SERVERS;
        IContainer jvmContainer = DNCoreAPI.getInstance().getContainer();
        setCustomType(() -> {
            //Completers.TreeCompleter

            String[] proxies = (String[]) jvmContainer.getExecutors().stream().filter(ijvmExecutor -> ijvmExecutor.isProxy()).collect(Collectors.toList()).toArray(new String[0]);

            if(proxies.length == 0){
                return new Object[]{Completers.AnyCompleter.INSTANCE};
            }
            return proxies;
        });
    }
}
