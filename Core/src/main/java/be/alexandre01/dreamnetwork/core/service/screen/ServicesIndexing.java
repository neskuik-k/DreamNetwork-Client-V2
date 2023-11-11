package be.alexandre01.dreamnetwork.core.service.screen;

import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.utils.clients.IdSet;
import lombok.Data;

import java.util.HashMap;
import java.util.Optional;
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

    public IService getService(String name, int id){
        if(index.containsKey(name)){
            return index.get(name).getServiceListById().get(id);
        }
        return null;
    }

    public Optional<IService> getService(String name){
        String[] split = name.split("-");
        name = split[0];
        int id = Integer.parseInt(split[1]);
        if(index.containsKey(name)){
            return Optional.ofNullable(index.get(name).getServiceListById().get(id));
        }
        return Optional.empty();
    }


    @Data
    public static class IndexOf{
        IdSet idSet = new IdSet();
        HashMap<IService,Integer> serviceList = new HashMap<>();
        HashMap<Integer,IService> serviceListById = new HashMap<>();
        public int addService(IService service){
            int id = idSet.getNextId();
            serviceList.put(service,id);
            serviceListById.put(id,service);
            idSet.add(id);
            return id;
        }

        public void removeService(IService service){
            System.out.print("Remove service from indexing");
            int id = service.getIndexingId();
            serviceList.remove(service);
            serviceListById.remove(id);
            idSet.remove(id);
        }
    }
}
