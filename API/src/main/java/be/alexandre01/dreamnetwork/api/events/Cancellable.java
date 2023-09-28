package be.alexandre01.dreamnetwork.api.events;

public interface Cancellable {
    public boolean isCancelled();

    public void setCancelled(boolean cancelled);
}
