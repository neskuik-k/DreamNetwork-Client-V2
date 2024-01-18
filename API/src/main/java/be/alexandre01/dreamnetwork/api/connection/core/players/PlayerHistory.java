package be.alexandre01.dreamnetwork.api.connection.core.players;

import be.alexandre01.dreamnetwork.api.utils.buffers.FixedSizeRingBuffer;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 16/01/2024 at 13:56
*/
public class PlayerHistory extends FixedSizeRingBuffer<Integer> {
    long historyTime;
    final long updateInterval;
    public PlayerHistory(Integer[] table, long updateInterval) {
        super(table);
        this.updateInterval = updateInterval;
        historyTime = System.currentTimeMillis();
    }


    @Override
    public void fill(Integer value) {
        long calc = System.currentTimeMillis() - historyTime;
        long iterations = (calc / updateInterval);
        if(calc > updateInterval){
            getLast().ifPresent(integer -> {
                // iterations - 1 because the last one is gonna be filled by the value in the parameter
                for (int i = 0; i < (iterations-1); i++) {
                    super.fill(integer);
                }
            });
            super.fill(value);
            historyTime = System.currentTimeMillis();
        }
    }
}
