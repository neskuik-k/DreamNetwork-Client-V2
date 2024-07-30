package be.alexandre01.dreamnetwork.core.connection.core.interceptors;

import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;
import be.alexandre01.dreamnetwork.api.connection.core.communication.CoreReceiver;
import be.alexandre01.dreamnetwork.api.connection.core.request.AbstractRequestManager;
import be.alexandre01.dreamnetwork.api.connection.core.request.TaskHandler;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.service.*;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import be.alexandre01.dreamnetwork.core.Core;

import java.util.Optional;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 03/10/2023 at 23:09
*/
public class StartTask {
    public static CoreReceiver.RequestInterceptor get(){
        CoreReceiver.RequestInterceptor start;
        AbstractRequestManager.getTasks().put("start", start = (message, ctx, client) -> {
            Optional<IExecutor> startExecutor = Core.getInstance().getJvmContainer().findExecutor(message.getString("SERVERNAME"));

            System.out.println("Searching if start executor of "+ message.getString("SERVERNAME")+ " is present");
            if (!startExecutor.isPresent()) {
                return;
            }


            IStartupConfig config = startExecutor.get().getStartupConfig();
            if(message.contains("DATA")){
                ConfigData configData = message.get("DATA", ConfigData.class);
                // merge with configData
                System.out.println("Merging config data");
                System.out.println(configData.getType());
                config = IStartupConfigBuilder.builder(config).buildFrom(configData);
                System.out.println("Merged => "+ config.getType());
                System.out.println("Merged config data");
            }

            ExecutorCallbacks executorCallbacks = startExecutor.get().startService(config);
            Console.debugPrint("RECEIVED REQUEST");
            message.getCallback().ifPresent(callback -> {
                Console.debugPrint("Callback present");
                executorCallbacks.whenStart(new ExecutorCallbacks.ICallbackStart() {
                    @Override
                    public void whenStart(IService service) {
                        Console.debugPrint("STARTED");
                        Message msg = new Message().set("name",service.getFullName());
                        service.getCustomName().ifPresent(s -> msg.set("customName",s+"-"+service.getId()));
                        callback.mergeAndSend(msg, "STARTED");
                    }
                });

                executorCallbacks.whenConnect(new ExecutorCallbacks.ICallbackConnect() {
                    @Override
                    public void whenConnect(IService service, AServiceClient client) {
                        Console.debugPrint("LINKED");
                        callback.send("LINKED");
                    }
                });

                executorCallbacks.whenFail(new ExecutorCallbacks.ICallbackFail() {
                    @Override
                    public void whenFail() {
                        Console.debugPrint("FAILED");
                        callback.send(TaskHandler.TaskType.FAILED);
                    }
                });
            });
        });
        return start;
    }
}
