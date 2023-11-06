package be.alexandre01.dreamnetwork.api.utils.messages.mapper;


import be.alexandre01.dreamnetwork.api.utils.messages.ObjectConverterMapper;

import java.util.Date;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 05/11/2023 at 20:02
*/
public class MapperOfDate extends ObjectConverterMapper<Date, Long> {

    @Override
    public Long convert(Date object) {
        return object.getTime();
    }

    @Override
    public Date read(Long object) {
        return new Date(object);
    }
}
