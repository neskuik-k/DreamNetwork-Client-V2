package be.alexandre01.dreamnetwork.api.service;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.core.service.JVMExecutor;
import be.alexandre01.dreamnetwork.core.utils.files.yaml.Ignore;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Setter
@Getter
public class ConfigData {
    protected JVMExecutor.Mods type;
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
