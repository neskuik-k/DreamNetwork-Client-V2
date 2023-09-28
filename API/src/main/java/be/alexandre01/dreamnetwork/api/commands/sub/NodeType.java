package be.alexandre01.dreamnetwork.api.commands.sub;

import lombok.Getter;

@Getter
public class NodeType {
    protected SubCommandCompletor.Type type;
    protected Object[] objects;
}
