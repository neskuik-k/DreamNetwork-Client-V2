package be.alexandre01.dreamnetwork.client.service.bundle;

import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import lombok.Data;

import java.util.ArrayList;

@Data
public class BundleData {
    private final JVMContainer.JVMType jvmType;
    private final String name;
    private final ArrayList<String> servers = new ArrayList<>();
    private boolean autoStart;
}
