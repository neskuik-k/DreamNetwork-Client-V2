package be.alexandre01.dreamnetwork.core.websocket.sessions;

import be.alexandre01.dreamnetwork.core.rest.DreamRestAPI;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 11/12/2023 at 15:19
*/
public class SessionManager {
    private DreamRestAPI dreamRestAPI;
    private String currentKey;

    public void refreshCurrentKey() {
        this.currentKey = "key";
    }
}
