package be.alexandre01.dreamnetwork.core.utils.clients;

import java.util.HashSet;
import java.util.Set;

public class IdSet {
    private Set<Integer> ids = new HashSet<>();

    public void add(int id) {
        ids.add(id);
    }
    public void remove(int id) {
        ids.remove(id);
    }
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
