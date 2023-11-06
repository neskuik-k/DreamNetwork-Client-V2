package be.alexandre01.dreamnetwork.api.connection.core.communication.packets;


import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.connection.core.communication.packets.exceptions.PacketParameterCastException;
import be.alexandre01.dreamnetwork.api.connection.core.communication.packets.exceptions.PacketParameterNullException;
import be.alexandre01.dreamnetwork.api.connection.core.request.Packet;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 04/11/2023 at 22:11
*/
public abstract class HandlerChecker {
    protected PacketGlobal packetGlobal;
    protected PacketHandlerParameter[] parameters;
    protected Method method;

    protected PacketHandlingFactory factory = DNCoreAPI.getInstance().getPacketFactory();

    public HandlerChecker(PacketGlobal packetGlobal, PacketHandlerParameter[] parameters, Method method) {
        this.packetGlobal = packetGlobal;
        this.parameters = parameters;
        this.method = method;
    }

    public abstract void execute(Message message, ChannelHandlerContext ctx);

    public void execute(Message message, ChannelHandlerContext ctx,String headerValue,String[] channels,String objCastOption) {
        List<Object> objects = new ArrayList<>();
        if(!message.getHeader().equals(headerValue) && !headerValue.isEmpty()){
            return;
        }
        // check si il y a un channel
        if (message.hasChannel() && channels.length > 0) {
            boolean hasChannel = false;
            for (String channel : channels) {
                if (channel.equalsIgnoreCase(message.getChannel())) {
                    hasChannel = true;
                    break;
                }
            }
            if (!hasChannel) {
                return;
            }
        }

        // check des parameters
        for (PacketHandlerParameter parameter : parameters) {
            if (parameter.parameter.getType() == Message.class) {
                objects.add(message);
                continue;
            }
            if (parameter.parameter.getType() == ChannelHandlerContext.class) {
                objects.add(ctx);
                continue;
            }
            String key = parameter.key;
            // setting value of parameter
            Class<?> type = parameter.parameter.getType();
            PacketGlobal.PacketCastOption castOption = packetGlobal.castOption();

            if (!objCastOption.equals("NOT_SET")) {
                castOption = PacketGlobal.PacketCastOption.valueOf(objCastOption);
            }
            if (parameter.parameter.getAnnotation(PacketCast.class) != null) {
                PacketCast packetCast = parameter.parameter.getAnnotation(PacketCast.class);
                if (packetCast.castOption() != PacketCast.PacketCastOption.NOT_SET) {
                    castOption = PacketGlobal.PacketCastOption.valueOf(packetCast.castOption().name());
                }
            }

            boolean isOptional = false;
            if (type == Optional.class) {
                isOptional = true;
                type = (Class) ((java.lang.reflect.ParameterizedType) parameter.parameter.getParameterizedType()).getActualTypeArguments()[0];
            }
            Object value = null;
            if(packetGlobal.castType() == PacketGlobal.PacketType.SMART){
                key = key.toLowerCase();
                for (String keys : message.keySet()) {
                    keys = keys.substring(1);
                    if(keys.toLowerCase().equals(key)){
                        key = keys;
                        break;
                    }
                }
            }
            if (message.contains(key)) {
                value = message.get(key, type);
            } else {
                if (type != Optional.class && castOption == PacketGlobal.PacketCastOption.NOT_NULL) {
                    try {
                        throw new PacketParameterNullException("Cannot cast null to " + key +"/"+ type.getName()+ " from "+ message);
                    } catch (PacketParameterNullException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (isOptional) {
                value = Optional.ofNullable(value);
            }

            if (castOption != PacketGlobal.PacketCastOption.IGNORE_ALL) {
                if (value != null) {
                    if (!value.getClass().isAssignableFrom(parameter.parameter.getType())) {
                        try {
                            throw new PacketParameterCastException("Cannot cast " + value.getClass().getName() + " to " + parameter.parameter.getType().getName() +" from "+ message);
                        } catch (PacketParameterCastException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    if (castOption == PacketGlobal.PacketCastOption.NOT_NULL) {
                        throw new RuntimeException("Cannot cast null to " + parameter.parameter.getType().getName());
                    }
                }

                objects.add(message.get(key, parameter.parameter.getType()));
            }
        }
        try {
            Object object = method.invoke(factory.getMethods().get(method), objects.toArray());

            if(object instanceof Packet){
                ((Packet) object).dispatch();
                return;
            }

            if(object instanceof Message){
                ctx.writeAndFlush((Message) object);
                return;
            }

            if(object instanceof Optional){
                Optional<?> optional = (Optional<?>) object;
                if(optional.isPresent()){
                    Object obj = optional.get();
                    if(obj instanceof Packet){
                        ((Packet) obj).dispatch();
                    }
                    if(obj instanceof Message){
                        ctx.writeAndFlush((Message) obj);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
