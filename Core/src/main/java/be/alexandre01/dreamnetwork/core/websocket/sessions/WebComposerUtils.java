package be.alexandre01.dreamnetwork.core.websocket.sessions;

import be.alexandre01.dreamnetwork.api.service.IExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.utils.messages.WebMessage;

import java.util.ArrayList;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 25/01/2024 at 19:28
*/
public class WebComposerUtils {
    public static WebMessage composeExecutor(IExecutor executor){
        ArrayList<String> profiles = new ArrayList<>();
        executor.getJvmProfiles().ifPresent(iProfiles -> {
            if (iProfiles.getProfiles() != null) {
                iProfiles.getProfiles().forEach((string, iConfig) -> {
                    profiles.add(string);
                });
            }

        });
        return new WebMessage()
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
                .put("profiles", profiles);
    }

    public static WebMessage composeNewService(IService service){
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
}
