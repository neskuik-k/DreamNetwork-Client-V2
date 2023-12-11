package be.alexandre01.dreamnetwork.api.utils.messages.mapper;


import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.utils.messages.ObjectConverterMapper;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 04/11/2023 at 23:37
*/

public class MapperOfService extends ObjectConverterMapper<IService,String> {
    @Override
    public String convert(IService object) {
        return object.getFullName();
    }

    @Override
    public IService read(String object) {
        return DNCoreAPI.getInstance().getContainer().findService(object).orElse(null);
    }
}

