package be.alexandre01.dreamnetwork.api.service;

import be.alexandre01.dreamnetwork.api.service.IConfig;

import java.io.File;

public interface IProfiles {
    void save();

    java.util.HashMap<String, IConfig> getProfiles();

    File getFile();
}
