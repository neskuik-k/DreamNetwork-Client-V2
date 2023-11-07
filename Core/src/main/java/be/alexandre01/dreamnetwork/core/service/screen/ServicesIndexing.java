package be.alexandre01.dreamnetwork.core.service.screen;

import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.utils.clients.IdSet;
import lombok.Data;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class ServicesIndexing {

    public HashMap<String,IndexOf> index = new HashMap<>();

    public void registerService(IService iService, BiConsumer<String,Integer> consumer){
        IndexOf indexOf = null;
        if(index.containsKey(iService.getName())){
            indexOf = index.get(iService.getName());
        }else{
            indexOf = new IndexOf();
            index.put(iService.getName(),indexOf);
        }
        consumer.accept(iService.getName(), indexOf.addService(iService));
    }

    public String getServiceName(IService service){
        if(index.containsKey(service.getName())){
            int id = index.get(service.getName()).getServiceList().get(service);
            return service.getName()+"-"+id;
        }
        return null;
    }

    public void unregisterService(IService jvmService) {
        if(index.containsKey(jvmService.getName())){
            index.get(jvmService.getName()).removeService(jvmService);
        }
    }


    @Data
    public static class IndexOf{
        IdSet idSet = new IdSet();
        HashMap<IService,Integer> serviceList = new HashMap<>();

        public int addService(IService service){
            int id = idSet.getNextId();
            serviceList.put(service,id);
            idSet.add(id);
            return id;
        }

        public void removeService(IService service){
            System.out.print("Remove service from indexing");
            int id = service.getIndexingId();
            serviceList.remove(service);
            idSet.remove(id);
        }
    }
}
