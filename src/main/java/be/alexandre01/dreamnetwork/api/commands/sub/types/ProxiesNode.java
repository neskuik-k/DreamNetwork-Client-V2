package be.alexandre01.dreamnetwork.api.commands.sub.types;

import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.service.JVMContainer;
import org.jline.builtins.Completers;

import java.util.stream.Collectors;

public class ProxiesNode extends CustomType {
    public ProxiesNode(){
        type = SubCommandCompletor.Type.SERVERS;
        JVMContainer jvmContainer = Core.getInstance().getJvmContainer();
        setCustomType(() -> {
            //Completers.TreeCompleter

            String[] proxies = (String[]) jvmContainer.jvmExecutors.stream().filter(ijvmExecutor -> ijvmExecutor.isProxy()).collect(Collectors.toList()).toArray(new String[0]);

            if(proxies.length == 0){
                return new Object[]{Completers.AnyCompleter.INSTANCE};
            }
            return proxies;
        });
    }
}
