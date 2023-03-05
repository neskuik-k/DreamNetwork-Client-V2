package be.alexandre01.dreamnetwork.api.commands.sub.types;

import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.service.JVMContainer;
import org.apache.commons.lang.ArrayUtils;
import org.jline.builtins.Completers;

import java.util.stream.Collectors;

public class ServersNode extends CustomType {
    public ServersNode(){
        type = SubCommandCompletor.Type.SERVERS;
        JVMContainer jvmContainer = Core.getInstance().getJvmContainer();
        setCustomType(() -> {
            //Completers.TreeCompleter
          //  String[] servers = jvmContainer.getJVMExecutorsServers().keySet().toArray(new String[0]);
            String[] servers = (String[]) jvmContainer.jvmExecutors.stream().filter(ijvmExecutor -> !ijvmExecutor.isProxy()).map(IJVMExecutor::getName).collect(Collectors.toList()).toArray(new String[0]);
            if(servers.length == 0){
                return new Object[]{Completers.AnyCompleter.INSTANCE};
            }
            return servers;
        });
    }
}
