package be.alexandre01.dreamnetwork.api.service;


import be.alexandre01.dreamnetwork.api.utils.files.yaml.Ignore;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Setter
@Getter

@AllArgsConstructor
@NoArgsConstructor
public class ConfigData implements Serializable {
    @Ignore
    protected String name;
    protected String bundleName;
    protected IContainer.JVMType jvmType;
    protected IJVMExecutor.Mods type;
    protected String xms;
    @Getter
    protected String startup = null;
    protected String executable = "exec";
    @Getter protected String xmx;
    protected String javaVersion = "default";
    protected int port = 0;
    protected String installInfo = null;
    protected Boolean screenEnabled = null;
    @Getter
    protected List<String> deployers = new ArrayList<>();
}
