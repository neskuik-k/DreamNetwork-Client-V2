package be.alexandre01.dreamnetwork.api.connection.core.players;

import be.alexandre01.dreamnetwork.api.utils.buffers.FixedSizeRingBuffer;
import be.alexandre01.dreamnetwork.api.utils.optional.Facultative;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 16/01/2024 at 13:56
*/
public class PlayerHistory extends FixedSizeRingBuffer<Integer> {
    Integer lastData;
    long historyTime;
    final long updateInterval;
    public PlayerHistory(Integer[] table, long updateInterval) {
        super(table);
        this.updateInterval = updateInterval;
        historyTime = System.currentTimeMillis();
    }


    @Override
    public void fill(Integer value) {
        if(refreshExecution(true)){
            super.fill(value);
            lastData = null;
        }else {
            lastData = value;
        }
    }

    public boolean refreshExecution(boolean hasToUpdateData){
        long calc = System.currentTimeMillis() - historyTime;
        int minus = hasToUpdateData || lastData != null ? 1 : 0;
        long iterations = (calc / updateInterval);
        if(calc > updateInterval){
           /* System.out.println("calc = " + calc);
            System.out.println("updateInterval = " + updateInterval);
            System.out.println("iterations = " + iterations);*/
            Facultative.ifPresentOrElse(getLast(),integer -> {
                for (int i = 0; i < (iterations-minus); i++) {
                    super.fill(integer);
                }
            },() -> {
                for (int i = 0; i < (iterations-minus); i++) {
                    super.fill(0);
                }
            });
            if(!hasToUpdateData && lastData != null){
                super.fill(lastData);
                lastData = null;
            }
            // get rest of the division
            historyTime = System.currentTimeMillis() - (calc % updateInterval);
            return true;
        }
        return false;
    }

    @Override
    public Integer[] getTable() {
        refreshExecution(false);
        if(!isFullFilled){
                // get T class with param T
                Integer[] table = new Integer[lastData != null ? this.index+1 : this.index];
                for(int i = 0; i < this.index; i++){
                    table[i] = this.table[i];
                }
                if(lastData != null)
                    table[this.index] = lastData;
                return table;
        }
        Integer[] table = new Integer[lastData != null ? this.table.length+1 : this.table.length];
        int nIndex = index-1;
        for (int i = 0; i < this.table.length; i++) {
            if(nIndex == this.table.length - 1){
                nIndex = 0;
            }else {
                nIndex++;
            }
            table[i] = this.table[nIndex];
        }
        if(lastData != null)
            table[this.table.length+1] = lastData;
        return table;
    }

}
