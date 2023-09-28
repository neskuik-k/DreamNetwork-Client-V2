package be.alexandre01.dreamnetwork.api.service.bundle;

import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import be.alexandre01.dreamnetwork.api.service.enums.ExecType;
import com.google.gson.Gson;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 02/09/2023 at 18:29
*/
public interface IBundleInfo {


    ArrayList<BService> getBServices();

    ArrayList<BService> getServices();

    String getName();

    be.alexandre01.dreamnetwork.api.service.IContainer.JVMType getType();

    ExecType getExecType();

    File getFile();
}
