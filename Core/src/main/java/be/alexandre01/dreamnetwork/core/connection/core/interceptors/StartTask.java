package be.alexandre01.dreamnetwork.core.connection.core.interceptors;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
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
    static {
        AbstractRequestManager.getTasks().put("start", (message, ctx, client) -> {
            Optional<IJVMExecutor> startExecutor = Core.getInstance().getJvmContainer().tryToGetJVMExecutor(message.getString("SERVERNAME"));

            System.out.println("Searching if start executor of "+ message.getString("SERVERNAME")+ "is present");
            if (!startExecutor.isPresent()) {
                return;
            }


            IStartupConfig config = startExecutor.get().getStartupConfig();
            if(message.contains("DATA")){
                ConfigData configData = message.get("DATA", ConfigData.class);
                // merge with configData
                config = IStartupConfigBuilder.builder(config).buildFrom(configData);
            }

            ExecutorCallbacks executorCallbacks = startExecutor.get().startServer(config);
            Console.debugPrint("RECEIVED REQUEST");
            message.getCallback().ifPresent(callback -> {
                Console.debugPrint("Callback present");
                executorCallbacks.whenStart(new ExecutorCallbacks.ICallbackStart() {
                    @Override
                    public void whenStart(IService service) {
                        callback.mergeAndSend(new Message().set("name",service.getFullName()), "STARTED");
                    }
                });

                executorCallbacks.whenConnect(new ExecutorCallbacks.ICallbackConnect() {
                    @Override
                    public void whenConnect(IService service, IClient client) {
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
    }
}
