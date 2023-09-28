package be.alexandre01.dreamnetwork.core.gui.start;

import be.alexandre01.dreamnetwork.core.console.accessibility.CoreAccessibilityMenu;
import be.alexandre01.dreamnetwork.core.service.JVMExecutor;

public class CustomStartConsole extends CoreAccessibilityMenu {
    public CustomStartConsole(JVMExecutor jvmExecutor) {
        super("m:customStart");
    }

    @Override
    public void redrawScreen() {
        // do nothing
    }

    @Override
    public void drawInfos() {
        // do nothing
    }
}
