package be.alexandre01.dreamnetwork.client.console.language;

import lombok.Getter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class LanguageFile {

    @Getter private InputStream input;
    public void loadLanguage(File language) {

    }

    public void loadLanguage(FileInputStream language) {

    }
}
