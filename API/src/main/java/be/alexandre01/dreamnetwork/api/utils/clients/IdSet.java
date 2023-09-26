package be.alexandre01.dreamnetwork.api.utils.clients;

import lombok.Synchronized;

import java.util.HashSet;
import java.util.Set;

public class IdSet {
    private final Set<Integer> ids = new HashSet<>();

    public void add(int id) {
        ids.add(id);
    }
    public void remove(int id) {
        ids.remove(id);
    }

    @Synchronized
    public int getNextId() {
        int nextId = 1;

        // Find the first gap in the sequence of IDs
        for (int id : ids) {
            if (id == nextId) {
                nextId++;
            } else {
                return nextId;
            }
        }

        return nextId;
    }


}
