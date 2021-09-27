package be.alexandre01.dreamnetwork.client.service.jvm;

import be.alexandre01.dreamnetwork.client.console.Console;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class JavaVersion {
    private String name;
    private String path;
    private int version = -1;

    public int getVersion() {
        return version;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        try{
            int[] ver = {8,9,10,11,12,13,14,15,16,17};
            name = name.replace(",", ".");
                double decimal = parsingStringToDouble(name);
                int number = (int) decimal;
                double fractional = number - decimal;
                if(fractional == 0){

                    if(Arrays.stream(ver).anyMatch(value -> value == number)){
                        this.version = number;
                    }
                    return;
                }

            fractional = Math.abs(fractional)*10;
            DecimalFormat df = new DecimalFormat("###.#");
            fractional = Integer.parseInt(df.format(fractional).replaceAll("\\.",""));


            int total = (int) fractional;

            if(Arrays.stream(ver).anyMatch(value -> value == total)){
                this.version = total;

            }
        }catch (Exception ignored){
        }
    }

    private double parsingStringToDouble(String d){
        try {
            String[] splitted = d.split("\\.");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < splitted.length; i++) {

                sb.append(splitted[i]);
                if(i == 0){
                    sb.append(".");
                }
            }
            return Double.parseDouble(sb.toString());
        }catch (Exception e){
        }
        return -1;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
