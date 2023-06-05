package be.alexandre01.dreamnetwork.core.console.accessibility;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.core.console.accessibility.AccessibilityMenu.Operation.OperationType;

public class AcceptOrRefuse implements AccessibilityMenu.ValueInput{
    AcceptOrRefuseListener listener;
    String noArg;
    String yesArg;
    AccessibilityMenu menu;
    @Deprecated

    public AcceptOrRefuse(String yes, String no, AcceptOrRefuseListener listener){
        this.listener = listener;
        this.noArg = no;
        this.yesArg = yes;
    }
    public AcceptOrRefuse(AccessibilityMenu menu,String yes, String no, AcceptOrRefuseListener listener){
        this.menu = menu;
        this.listener = listener;
        this.noArg = no;
        this.yesArg = yes;
    }
    @Override
    public void onTransition(AccessibilityMenu.ShowInfos infos) {
        if(menu != null){
            menu.setArgumentsBuilder(NodeBuilder.create(yesArg,noArg));
        }
        listener.transition(infos);
    }



    @Override
    public AccessibilityMenu.Operation received(AccessibilityMenu.PromptText prompt, String[] args, AccessibilityMenu.ShowInfos infos) {
        String value = prompt.getValue();
        if(value.equalsIgnoreCase(yesArg)){
            return listener.accept(value, args, infos);
        }else if(value.equalsIgnoreCase(noArg)){
            return listener.refuse(value, args, infos);
        }
        return AccessibilityMenu.Operation.set(OperationType.RETRY);
    }

    public interface AcceptOrRefuseListener{

        public void transition(AccessibilityMenu.ShowInfos infos);
        public AccessibilityMenu.Operation accept(String value, String[] args, AccessibilityMenu.ShowInfos infos);

        public AccessibilityMenu.Operation refuse(String value, String[] args, AccessibilityMenu.ShowInfos infos);


    }



}
