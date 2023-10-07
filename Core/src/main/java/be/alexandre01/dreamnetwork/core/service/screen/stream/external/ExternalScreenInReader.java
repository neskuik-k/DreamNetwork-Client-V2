package be.alexandre01.dreamnetwork.core.service.screen.stream.external;

import be.alexandre01.dreamnetwork.api.connection.core.request.DNCallback;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestType;
import be.alexandre01.dreamnetwork.api.connection.core.request.TaskHandler;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.service.screen.IScreenInReader;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import be.alexandre01.dreamnetwork.core.connection.external.service.VirtualExecutor;
import be.alexandre01.dreamnetwork.core.connection.external.service.VirtualService;
import be.alexandre01.dreamnetwork.core.service.screen.Screen;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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
    InputStream reader;
    Screen screen;

    public ExternalScreenInReader(Console console, IService server, InputStream reader, Screen screen) {

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

        Message message = new Message().set("test", "test");

        message.toPacket(virtualExecutor.getExternalCore()).dispatch();
        // ou
        message.toPacket(virtualExecutor.getExternalCore()).dispatch(future -> {
            // do something
        });
        // ou
        virtualExecutor.getExternalCore().writeAndFlush(message);
        DNCallback.single(virtualExecutor.getExternalCore().getRequestManager().getRequest(RequestType.DEV_TOOLS_VIEW_CONSOLE_MESSAGE, virtualService.getTrueFullName()), new TaskHandler() {
            @Override
            public void onAccepted() {
                console.fPrint("The request to view the console has been accepted", Level.INFO);
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
            public void onFailed() {
                Console.debugPrint("The request to stop to read the console has been rejected");
            }
        });
    }
}
