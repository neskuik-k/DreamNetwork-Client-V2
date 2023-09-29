package be.alexandre01.dreamnetwork.api.service.tasks;

import be.alexandre01.dreamnetwork.api.utils.files.yaml.Ignore;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 03/09/2023 at 11:00
*/
public class GlobalTasksData {
    @Getter
    public final List<TaskData> tasks = new ArrayList<>();
    @Getter @Ignore
    public final List<TaskData> alwaysONs = new ArrayList<>();
    @Ignore
    private HashMap<String, TaskData> withNames = new HashMap<>();
}
