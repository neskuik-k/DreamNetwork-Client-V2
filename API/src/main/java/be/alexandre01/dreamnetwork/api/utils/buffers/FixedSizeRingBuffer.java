package be.alexandre01.dreamnetwork.api.utils.buffers;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;

public class FixedSizeRingBuffer<T> implements Iterable<T>, Cloneable {
    T[] table;
    Class<T> type;

    int index = 0;
    boolean isFullFilled = false;

    @Deprecated
    public FixedSizeRingBuffer(T[] table, Class<T> type) {
        this.table = table;
        this.type = type;
    }
    public FixedSizeRingBuffer(T[] table) {
        this.table = table;
        this.type = (Class<T>) table.getClass().getComponentType();
    }
    private Class<T> getTypeClass() {
        Type genericSuperclass = getClass().getGenericSuperclass();

        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();

            if (typeArguments.length > 0 && typeArguments[0] instanceof Class) {
                return (Class<T>) typeArguments[0];
            }
        }

        throw new IllegalArgumentException("Cannot determine the type parameter of TableSimpleStats");
    }
    public void fill(T value){

        if(index >= table.length){
            index = 0;
            isFullFilled = true;
        }
        table[index] = value;
        this.index++;
    }

    public T[] getDesorderedTable(){
        return table;
    }

    public T[] getTable(){
        return getTable(true);
    }
    public T[] getTable(boolean autoSize){
        if(!isFullFilled){
            if(autoSize){
                // get T class with param T
                T[] table = (T[]) java.lang.reflect.Array.newInstance(type, this.index);
                for(int i = 0; i < this.index; i++){
                    table[i] = this.table[i];
                }
                return table;
            }
            return table;
        }
        // get T class
        T[] table = (T[]) java.lang.reflect.Array.newInstance(type, this.table.length);
        int nIndex = index-1;
        for (int i = 0; i < this.table.length; i++) {
            if(nIndex == this.table.length - 1){
                nIndex = 0;
            }else {
                nIndex++;
            }
            table[i] = this.table[nIndex];
        }
        return table;
    }

    private T[] getTable2(){
        if(!isFullFilled){
            return table;
        }
        // get T class
        Class<T> type = (Class<T>) table[0].getClass();

        T[] table = (T[]) java.lang.reflect.Array.newInstance(type, this.table.length);
        int count = 0;
        for(int i = this.index; i < this.table.length; i++){
            table[count] = this.table[i];
            count++;
        }

        for(int i = 0; i < this.index; i++){
            table[count] = this.table[i];
            count++;
        }
        return table;
    }



    public Optional<T> getFirst(){
        if(isFullFilled)
            return Optional.ofNullable(table[0]);
        return Optional.ofNullable(table[index-1]);
    }
    public Optional<T> getLast(){
        if(isFullFilled){
            return Optional.ofNullable(table[index-2]);
        }
        if(index == 0){
            return Optional.ofNullable(table[0]);
        }
        return Optional.ofNullable(table[index-1]);
    }

    public static void main(String[] args) {
        //test with auto size
        System.out.println("Test with auto size");
        FixedSizeRingBuffer<Long> tableSimpleStats1 = new FixedSizeRingBuffer<Long>(new Long[10]);
        System.out.println("J'ai un tableau de " + tableSimpleStats1.getTable().length + " de long");
        System.out.println("Le but est de remplir le tableau de 0 à 7");
        System.out.println("Il est censé remplir le tableau de 0 à 7 et de retourner un tableau de taille 8");
        System.out.println("Filling table");
        for(int i = 0; i < 8; i++){
            tableSimpleStats1.fill((long) i);
        }
        System.out.println("From \n");
        for(Long l : tableSimpleStats1.getDesorderedTable()){
            System.out.println(l);
        }
        System.out.println("To (My custom code)\n");
        for(Long l : tableSimpleStats1.getTable()){
            System.out.println(l);
        }

        //test with array
        FixedSizeRingBuffer<Long> tableSimpleStats = new FixedSizeRingBuffer<>(new Long[999999],Long.class);
        System.out.println("J'ai un tableau de " + tableSimpleStats.getTable().length + " de long");
        System.out.println("Le but est de remplir le tableau de 0 à 1299999");
        System.out.println("Lorsque la limite est atteinte c'est à dire 999999, il a pour but de remplacer les anciens éléments par les nouveaux");
        System.out.println("C'est à dire que j'aurais un tableau de (1299999-999999 = 300000) jusqu'à 1299999");
        System.out.println("Filling table");
        long time = System.currentTimeMillis();
        for(int i = 0; i < 1299999; i++){
            tableSimpleStats.fill((long) i);
        }


        System.out.println("Filled in " + (System.currentTimeMillis() - time) + "ms");
        System.out.println("From \n");
        for(Long l : tableSimpleStats.getDesorderedTable()){
           // System.out.println(l);
        }
        System.out.println("To (My custom code)\n");
       time = System.currentTimeMillis();
        for(Long l : tableSimpleStats.getTable()){
            //System.out.println(l);

        }
        System.out.println("Calculated in " + (System.currentTimeMillis() - time) + "ms");
        System.out.println("\n To 2 (My custom code)\n");
        time = System.currentTimeMillis();
        for(Long l : tableSimpleStats.getTable2()){
           // System.out.println(l);
        }
        System.out.println("Calculated in " + (System.currentTimeMillis() - time) + "ms");

        System.out.println("====================");
        System.out.println("Test with arraylist");
        //test with arraylist
        System.out.println("From (ArrayList with add and remove index 0 on limit\n");
        time = System.currentTimeMillis();
        ArrayList<Long> longs = new ArrayList<>();
        int limit = 999999;
        for(int i = 0; i < 1299999; i++){
            longs.add((long) i);
            if(longs.size() > limit){
                longs.remove(0);
            }
        }
        System.out.println("Filled in order in " + (System.currentTimeMillis() - time) + "ms");
        System.out.println("\n To (ArrayList with add and remove index 0 on limit\n");
        time = System.currentTimeMillis();
        for(Long l : longs){
            // parcour de la liste
            //System.out.println(l);
        }
        System.out.println("Calculated in " + (System.currentTimeMillis() - time) + "ms");
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int index = 0;
            @Override
            public boolean hasNext() {
                return index < table.length;
            }

            @Override
            public T next() {
                return table[index++];
            }
        };
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return Iterable.super.spliterator();
    }

    public void clear(){
        Arrays.fill(table,null);
    }

    public boolean isEmpty(){
        return !isFullFilled && index == 0;
     }

}
