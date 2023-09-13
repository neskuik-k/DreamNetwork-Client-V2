package be.alexandre01.dreamnetwork.api.connection.core.request;

public class RequestInfo  {
    private final int id;

    private final String name;
    public RequestInfo(int id,String name){
        this.id = id;
        this.name = name;
    }


    public int id() {
        return id;
    }

    public String name() {
        return name;
    }
}
