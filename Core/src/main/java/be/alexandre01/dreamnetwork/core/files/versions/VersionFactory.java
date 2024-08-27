package be.alexandre01.dreamnetwork.core.files.versions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 24/03/2024 at 19:53
*/
public class VersionFactory {
    private HashMap<String, Stack<ServerVersion>> versions = new HashMap<>();

    public void addVersion(ServerVersion version){
        if(versions.containsKey(version.getVersion())){
            versions.get(version.getVersion()).push(version);
        }else{
            Stack<ServerVersion> stack = new Stack<>();
            stack.push(version);
            versions.put(version.getVersion(),stack);
        }
    }
}
