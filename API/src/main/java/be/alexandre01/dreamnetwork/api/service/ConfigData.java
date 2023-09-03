package be.alexandre01.dreamnetwork.api.service;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Setter
@Getter
public class ConfigData {
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
