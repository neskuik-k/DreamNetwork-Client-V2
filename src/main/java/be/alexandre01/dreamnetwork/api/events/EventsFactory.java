package be.alexandre01.dreamnetwork.api.events;

import be.alexandre01.dreamnetwork.utils.Tuple;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class EventsFactory {
    @Getter
    private Multimap<Class<?>, Tuple<Method,EventCatcher>> methods = ArrayListMultimap.create();

    private HashMap<Method,Listener> listeners = new HashMap<>();


    public void callEvent(Event event) {
      if(!methods.containsKey(event.getClass()))
          return;


        Collection<Tuple<Method,EventCatcher>> tuples = methods.get(event.getClass());

        tuples.forEach(tuple -> {
            Method method = tuple.a();
            if(!listeners.containsKey(method))
                return;

            EventCatcher eventCatcher = tuple.b();
            try {
                method.invoke(listeners.get(method), event);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void registerListener(Listener listener) {
        findEventCatchers(listener);
    }

    private void findEventCatchers(Listener listener){
        Set<Method> methods;

        Method[] publicMethods = listener.getClass().getMethods();
        Method[] privateMethods = listener.getClass().getDeclaredMethods();
        methods = new HashSet<Method>(publicMethods.length + privateMethods.length, 1.0F);
        Method[] arrayOfMethod1;
        int i;
        byte b;
        for (i = (arrayOfMethod1 = publicMethods).length, b = 0; b < i; ) {
            final Method method = arrayOfMethod1[b];
            methods.add(method);
            b++;
        }
        for (i = (arrayOfMethod1 = privateMethods).length, b = 0; b < i; ) {
            final Method method = arrayOfMethod1[b];
            methods.add(method);


            b++;
        }
        for (Method method : methods) {
            EventCatcher up = method.<EventCatcher>getAnnotation(EventCatcher.class);

            if(method.getParameterTypes().length != 1)
                continue;

            if(!Event.class.isAssignableFrom(method.getParameterTypes()[0])){
                continue;
            }
            if (up == null)
                continue;
            this.methods.put(method.getParameterTypes()[0], new Tuple<>(method,up));
            listeners.put(method,listener);
        }
    }


}
