package be.alexandre01.dreamnetwork.api.utils.messages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 13/12/2023 at 12:49
*/
public class WebMessage extends LinkedHashMap<String, Object> {
    ObjectMapper jacksonMapper = new ObjectMapper();
    public WebMessage(){
        super(new LinkedHashMap<>());
        this.jacksonMapper = new ObjectMapper();
    }

    @Override
    public WebMessage put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    @Override
    public WebMessage clone() {
        return (WebMessage) super.clone();
    }

    @Override
    public String toString() {
        try {
            //System.out.println(json);
            return jacksonMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
