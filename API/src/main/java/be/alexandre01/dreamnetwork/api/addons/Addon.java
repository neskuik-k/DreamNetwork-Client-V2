package be.alexandre01.dreamnetwork.api.addons;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.net.URLClassLoader;

@Getter
@Setter
public class Addon implements Serializable {
     private File file;
     @Expose
     private String dreamyPath;
     @Expose
     private String dreamyName;
     @Expose
     private String[] authors;
     @Expose
     private String version;
     @Expose
     private String description;
     private URLClassLoader child;
     @Expose
     private URL url;
     private Class<?> defaultClass;
     private boolean overrideLoading;
}
