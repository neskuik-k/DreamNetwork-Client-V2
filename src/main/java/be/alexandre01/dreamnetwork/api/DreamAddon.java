package be.alexandre01.dreamnetwork.api;

import be.alexandre01.dreamnetwork.api.connection.request.IRequestManager;
import be.alexandre01.dreamnetwork.client.Client;
import lombok.Getter;

public abstract class DreamAddon {
    private Client client;

    @Getter public IRequestManager manager;
    
    public abstract void onLoad();

    public DreamAddon(){
        Client client = Client.getInstance();
    }

    public String getName(){
        return "DreamNetwork";
    }

    final void load(Client client){
        this.client = client;
        onLoad();
    }

}
