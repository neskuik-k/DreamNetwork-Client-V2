package be.alexandre01.dreamnetwork.core.service.bundle;

import be.alexandre01.dreamnetwork.core.service.JVMContainer;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class BundleData {
    private final JVMContainer.JVMType jvmType;
    private final String name;
    private final ArrayList<BService> services;
    private final boolean autoStart;

    public BundleData(JVMContainer.JVMType jvmType,String name,  ArrayList<BService> services,boolean autoStart) {
        this.jvmType = jvmType;
        this.name = name;
        this.autoStart = autoStart;
        this.services = services;
    }
    public BundleData(JVMContainer.JVMType jvmType,String name, boolean autoStart) {
        this.jvmType = jvmType;
        this.name = name;
        this.autoStart = autoStart;
        this.services = new ArrayList<>();
    }

    public String hashToString(){
        Map<Integer, Object> stringMap = new HashMap<>();
        stringMap.put(1,jvmType.name());
        stringMap.put(2,name);
        StringBuilder servicesString = new StringBuilder("[");

        for (int i = 0; i < services.size(); i++) {
            BService bService = services.get(i);
            servicesString.append(bService.getServiceName()+":"+bService.getTotalCount());

            if(i < services.size()-1){
                servicesString.append(";");
            }
        }
        servicesString.append("]");

        stringMap.put(3,servicesString.toString());
        stringMap.put(4,autoStart);


        return convertWithGuava(stringMap);
    }
    public String convertWithGuava(Map<Integer, ?> map) {
        return Joiner.on(",").withKeyValueSeparator("=").join(map);
    }
    public Map<String, String> convertWithGuava(String mapAsString) {
        return Splitter.on(',').withKeyValueSeparator('=').split(mapAsString);
    }
    public String convertWithStream(Map<Integer, ?> map) {
        String mapAsString = map.keySet().stream()
                .map(key -> key + "=" + map.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
        return mapAsString;
    }
    public String formatToString(){
        StringBuilder servicesString = new StringBuilder("[");

        for (int i = 0; i < services.size(); i++) {
            BService bService = services.get(i);
            servicesString.append(bService.getServiceName()+":"+bService.getTotalCount());

            if(i > services.size()){
                servicesString.append(",");
            }
        }


        return jvmType.name()+","+name+","+servicesString+","+autoStart;
    }

    public static BundleData createFromHashMap(Map<String,String> hashMap){

        JVMContainer.JVMType type;
        try {
            type = JVMContainer.JVMType.valueOf(hashMap.get("1"));
        }catch (Exception e){
            return null;
        }
        String name = hashMap.get("2");
        String s = hashMap.get("3");
        s = s.substring(1,s.length()-1);

        ArrayList<BService> bServices = new ArrayList<>();
        for (String l : s.split(";")){
            String[] sep = l.split(":");
            Integer active = null;
            if(sep.length > 2){
                try {
                    active = Integer.parseInt(sep[2]);
                }catch (Exception e){
                }
            }
            try{
                BService bService = new BService(sep[0],Integer.parseInt(sep[1]),active);
                bServices.add(bService);
            }catch (Exception ignored){
            }
        }

        String bo = hashMap.get("4");
        boolean b;
        try {
            b = Boolean.parseBoolean(bo);
        }catch (Exception ignored){
            return null;
        }


        return new BundleData(type,name,bServices,b);
    }
    public static BundleData createFromString(String formatted){
        String[] tot1 = formatted.split("\\[");
        String[] arr1 = tot1[0].split(",");
        String[] tot2 = tot1[1].split("]");
        String[] arr2 = tot2[0].split(",");
        String[] arr3 = tot2[1].split(",");


        JVMContainer.JVMType type;
        try {
            type = JVMContainer.JVMType.valueOf(arr1[0]);
        }catch (Exception e){
            return null;
        }

        String name = arr1[1];


        ArrayList<BService> bServices = new ArrayList<>();

        for (int i = 0; i < arr2.length; i++) {
            String[] sep = arr2[i].split(":");
            Integer active = null;
            if(sep.length > 2){
                try {
                    active = Integer.parseInt(sep[2]);
                }catch (Exception e){
                }
            }
            try{
                BService bService = new BService(sep[0],Integer.parseInt(sep[1]),active);
                bServices.add(bService);
            }catch (Exception ignored){
            }
        }

        String bo = arr3[0];
        boolean b;
        try {
            b = Boolean.parseBoolean(bo);
        }catch (Exception ignored){
            return null;
        }
        return new BundleData(type,name,bServices,b);
    }
}
