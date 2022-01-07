package be.alexandre01.dreamnetwork.client.commands.lists.sub.gui;

import be.alexandre01.dreamnetwork.client.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.client.commands.sub.SubCommandExecutor;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.graphics.StyleSet;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.virtual.DefaultVirtualTerminal;
import lombok.NonNull;

import java.io.IOException;

public class Stats extends SubCommandCompletor implements SubCommandExecutor {
    @Override
    public boolean onSubCommand(@NonNull String[] args) {



        return false;
    }


}
