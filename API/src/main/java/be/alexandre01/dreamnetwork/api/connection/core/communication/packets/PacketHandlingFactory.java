package be.alexandre01.dreamnetwork.api.connection.core.communication.packets;

import be.alexandre01.dreamnetwork.api.connection.core.communication.packets.exceptions.PacketInvalidAnnotation;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 04/11/2023 at 22:09
*/
@Getter
@PacketGlobal(header = "")
public class PacketHandlingFactory {
    final HashMap<String,HandlerChecker> headers = new HashMap<>();
    final HashMap<Class<?>,RequestHandler> annotations = new HashMap<>();
    final HashMap<Method,Object> methods = new HashMap<>();
    final List<Class<?>> annotationsList = new ArrayList<>();

    public void addRequestAnnotation(Class<?> annotation) throws PacketInvalidAnnotation {
        try {
            System.out.println("Registering annotation " + annotation.getName());
            System.out.println("Methods => "+Arrays.toString(annotation.getMethods()));
            System.out.println("Declared methods => "+Arrays.toString(annotation.getDeclaredMethods()));
            annotationsList.add(annotation);
        }catch (Exception e){
            throw new PacketInvalidAnnotation("Invalid annotation",e);
        }

    }


    public void registerHandlingClass(Class<?>... classes){
        for (Class<?> clazz : classes) {
            System.out.println("Registering " + clazz.getName());
            PacketGlobal packetGlobal = clazz.getAnnotation(PacketGlobal.class);
            if(packetGlobal == null){
                packetGlobal = this.getClass().getAnnotation(PacketGlobal.class);
            }
            if(packetGlobal == null){
                throw new RuntimeException("No PacketGlobal annotation found");
            }
            try {
                searchAllMethods(packetGlobal,clazz, clazz.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

   /* public void registerRoot(Object object){
        ClassFinder classFinder = new ClassFinder();
        System.out.println("Registering root " + object.getClass().getPackage().getName());
        System.out.println("Test");
        Set<Class> classes = classFinder.findAllClassesFrom(object.getClass().getPackage().getName(),this.getClass());
        for (Class aClass : classes) {
            registerHandlingClass(aClass);
        }
    }*/

    private void searchAllMethods(PacketGlobal global,Class<?> clazz,Object object){
      /*  System.out.println("Searching all methods");
        System.out.println("Class " + clazz.getName());
        System.out.println("Methods declared => "+Arrays.toString(clazz.getDeclaredMethods()));
        System.out.println("Methods non declared => "+Arrays.toString(clazz.getMethods()));*/

        for(Method method : clazz.getDeclaredMethods()){
            PacketHandler packetHandler = method.getAnnotation(PacketHandler.class);
            RequestHandler requestHandler = null;
            for (Annotation annotation : method.getAnnotations()) {;
                if(annotationsList.contains(annotation.annotationType())){
                    try {
                        Object id = annotation.annotationType().getMethod("id").invoke(annotation);
                        String value = (String) id.getClass().getDeclaredMethod("value").invoke(id);
                        int priority = (int) annotation.annotationType().getMethod("priority").invoke(annotation);
                        String[] channels = (String[]) annotation.annotationType().getMethod("channels").invoke(annotation);
                        RequestHandler.PacketCastOption castOption = (RequestHandler.PacketCastOption) annotation.annotationType().getMethod("castOption").invoke(annotation);
                        requestHandler = RequestHandler.builder()
                                .id(value)
                                .priority(priority)
                                .channels(channels)
                                .castOption(castOption)
                                .build();
                        break;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

           // System.out.println("Found method " + method.getName());
          //  System.out.println("PacketHandler " + packetHandler+ " RequestHandler " + requestHandler);
            List<PacketHandlerParameter> packetCasts = new ArrayList<>();
            if(packetHandler != null || requestHandler != null){
                for (Parameter parameter : method.getParameters()) {
                    String key = "";
                    if(global.header() != null && !global.header().isEmpty() && global.suffix() != null && requestHandler == null){
                        key = global.header()+global.suffix();
                    }

                    if(parameter.getAnnotation(PacketCast.class) != null){
                        PacketCast packetCast = parameter.getAnnotation(PacketCast.class);
                        key += packetCast.key();
                    }else {
                        if(!parameter.isNamePresent()){
                            System.out.println("Parameter name not present");
                            throw new RuntimeException("Parameter name not present");
                        }
                        key += parameter.getName();

                        if(global.castType() == PacketGlobal.PacketType.CAPITALIZED){
                            // variableType => VARIABLETYPE
                            key = key.toUpperCase();
                        }else if(global.castType() == PacketGlobal.PacketType.LOWERCASE || global.castType() == PacketGlobal.PacketType.SMART){
                            // variableType => variabletype
                            key = key.toLowerCase();
                        }else{
                            if(global.castType() == PacketGlobal.PacketType.PRETTY){
                                key = key.substring(0, 1).toUpperCase() + key.substring(1);
                            }
                        }
                    }

                    if(key.isEmpty()){
                        throw new RuntimeException("Key is null");
                    }
                    packetCasts.add(PacketHandlerParameter.builder()
                            .parameter(parameter)
                            .key(key)
                            .build());
                }
                if(packetHandler != null){
                    if(headers.containsKey(packetHandler.header())){
                        throw new RuntimeException("Header already registered");
                    }
                 //   System.out.println("Adding packethandler " + packetHandler.header());
                    headers.put(packetHandler.header(),PacketHandlerChecker.builder()
                            .parameters(packetCasts.toArray(new PacketHandlerParameter[0]))
                            .packetGlobal(global)
                            .handler(packetHandler)
                            .method(method)
                            .build());
                    methods.put(method,object);
                }else {
                    // if requestHandler != null
                    if(headers.containsKey(requestHandler.id())){
                        throw new RuntimeException("Header already registered");
                    }
                    headers.put(requestHandler.id(),RequestHandlerChecker.builder()
                            .parameters(packetCasts.toArray(new PacketHandlerParameter[0]))
                            .packetGlobal(global)
                            .handler(requestHandler)
                            .method(method)
                            .build());
                    methods.put(method,object);
                }

            }
        }
    }

}
