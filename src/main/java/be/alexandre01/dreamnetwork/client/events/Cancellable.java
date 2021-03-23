package be.alexandre01.dreamnetwork.client.events;

public interface Cancellable {
    public boolean isCancelled();

    public void setCancelled(boolean b);
}
