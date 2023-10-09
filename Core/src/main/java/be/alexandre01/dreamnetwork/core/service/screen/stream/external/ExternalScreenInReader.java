package be.alexandre01.dreamnetwork.core.service.screen.stream.external;

import be.alexandre01.dreamnetwork.api.connection.core.communication.CoreResponse;
import be.alexandre01.dreamnetwork.api.connection.core.communication.UniversalConnection;
import be.alexandre01.dreamnetwork.api.connection.core.request.DNCallback;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestBuilder;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestType;
import be.alexandre01.dreamnetwork.api.connection.core.request.TaskHandler;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenInReader;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import be.alexandre01.dreamnetwork.core.connection.core.communication.services.BaseResponse;
import be.alexandre01.dreamnetwork.core.connection.external.service.VirtualExecutor;
import be.alexandre01.dreamnetwork.core.connection.external.service.VirtualService;
import be.alexandre01.dreamnetwork.core.service.screen.Screen;
import io.netty.channel.ChannelHandlerContext;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 07/10/2023 at 19:38
*/
public class ExternalScreenInReader implements IScreenInReader {
    List<ReaderLine> readerLines = new ArrayList<>();
    Console console;
    IService server;

    IScreen screen;
    CoreResponse coreResponse = new CoreResponse() {};
    RequestBuilder.RequestData requestData;

    public ExternalScreenInReader(Console console, IService server,  IScreen screen) {
        this.console = console;
        this.server = server;
        this.screen = screen;

        coreResponse.addRequestInterceptor(RequestType.DEV_TOOLS_VIEW_CONSOLE_MESSAGE, (message, ctx, client) -> {
            receive(message.getString("DATA"));
        });
    }

    public void receive(String message){
        console.printNL(message);
    }

    @Override
    public List<ReaderLine> getReaderLines() {
        return readerLines;
    }

    @Override
    public FileHandler getFileHandler() {
        return null;
    }

    @Override
    public void run() {
        VirtualService virtualService = (VirtualService) server;
        VirtualExecutor virtualExecutor = (VirtualExecutor) server.getJvmExecutor();


        DNCallback.single(virtualExecutor.getExternalCore().getRequestManager().getRequest(RequestType.DEV_TOOLS_VIEW_CONSOLE_MESSAGE, virtualService.getTrueFullName()), new TaskHandler() {
            @Override
            public void onAccepted() {
                console.fPrint("The request to view the console has been accepted", Level.INFO);
                virtualService.getJvmExecutor().getExternalCore().getCoreHandler().getResponses().add(coreResponse);
            }

            @Override
            public void onFailed() {
                console.fPrint("The request to view the console has been rejected", Level.WARNING);
            }
        }).send();
    }

    @Override
    public void stopReader() {
        VirtualService virtualService = (VirtualService) server;
        VirtualExecutor virtualExecutor = (VirtualExecutor) server.getJvmExecutor();
        DNCallback.single(virtualExecutor.getExternalCore().getRequestManager().getRequest(RequestType.DEV_TOOLS_VIEW_CONSOLE_MESSAGE, virtualService.getTrueFullName()), new TaskHandler() {
            @Override
            public void onAccepted() {
                Console.debugPrint("The request to stop to read the console has been accepted");
            }

            @Override
            public void onRejected() {
                Console.debugPrint("The request to stop to read the console has been rejected");
            }
        }).send();
        virtualService.getJvmExecutor().getExternalCore().getCoreHandler().getResponses().remove(coreResponse);
    }
}
