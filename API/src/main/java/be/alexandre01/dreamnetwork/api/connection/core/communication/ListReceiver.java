package be.alexandre01.dreamnetwork.api.connection.core.communication;

import java.util.ArrayList;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 06/11/2023 at 19:01
*/
public class ListReceiver extends ArrayList<CoreReceiver> {
    @Override
    public boolean add(CoreReceiver coreReceiver) {
        super.addAll(coreReceiver.getSubReceivers());
        return super.add(coreReceiver);
    }
}
