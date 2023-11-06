package be.alexandre01.dreamnetwork.api.utils.messages.mapper;


import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.utils.messages.ObjectConverterMapper;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 04/11/2023 at 23:37
*/

public class MapperOfExecutor extends ObjectConverterMapper<IJVMExecutor,String> {
    @Override
    public String convert(IJVMExecutor object) {
        return object.getFullName();
    }

    @Override
    public IJVMExecutor read(String object) {
        return DNCoreAPI.getInstance().getContainer().tryToGetJVMExecutor(object).orElse(null);
    }
}

