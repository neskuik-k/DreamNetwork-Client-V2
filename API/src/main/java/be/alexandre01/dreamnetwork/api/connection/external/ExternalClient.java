package be.alexandre01.dreamnetwork.api.connection.external;

import be.alexandre01.dreamnetwork.api.DNUtils;
import be.alexandre01.dreamnetwork.api.connection.core.communication.UniversalConnection;
import be.alexandre01.dreamnetwork.api.connection.core.handler.ICoreHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExternalClient extends UniversalConnection {
    public ExternalClient(int port, String info, ChannelHandlerContext ctx, ICoreHandler coreHandler) {
        super(port, info, ctx);
        setRequestManager(DNUtils.get().createClientRequestManager(this));
        setCoreHandler(coreHandler);
    }
}
