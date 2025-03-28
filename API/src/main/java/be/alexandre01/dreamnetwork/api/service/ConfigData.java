package be.alexandre01.dreamnetwork.api.service;


import be.alexandre01.dreamnetwork.api.utils.files.yaml.CanBeNull;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.Ignore;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.SkipInitCheck;
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
    @CanBeNull protected String defaultName = null;
    @Ignore
    protected String name;
    @Ignore protected String bundleName;
    protected IContainer.JVMType jvmType;
    protected IExecutor.Mods type;
    protected String xms;
    @Getter
    protected String startup = "%java% -Xms%xms% -Xmx%xmx% -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 %args% -jar %exec% nogui";
    protected String executable = "exec";
    @Getter protected String xmx;
    protected String javaVersion = "default";
    protected int port = 0;
    @SkipInitCheck protected String installInfo = null;
    @SkipInitCheck protected Boolean screenEnabled = null;
    @Getter
    protected List<String> deployers = new ArrayList<>();
    @Getter @Setter
    protected List<String> staticDeployers = new ArrayList<>();


    public Optional<String> getCustomName(){
        return Optional.ofNullable(defaultName);
    }
}
