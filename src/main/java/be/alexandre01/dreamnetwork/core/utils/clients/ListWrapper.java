package be.alexandre01.dreamnetwork.core.utils.clients;

import java.util.ArrayList;
import java.util.List;

public class ListWrapper<T> {
    public List<T> toUpdate = new ArrayList<>();
    public List<T> objects = new ArrayList<>();

    public ListWrapper(List<T> objects) {
        this.objects = objects;
    }

    public void add(T object) {
        toUpdate.add(object);
    }

    public void remove(T object) {
        toUpdate.remove(object);
    }

    public void clear() {
        toUpdate.clear();
    }

    public ArrayList<T> getUpdateData() {
        return (ArrayList<T>) toUpdate;
    }

    public ArrayList<T> updated(){
        objects.addAll(toUpdate);
        return (ArrayList<T>) objects;
    }
}
