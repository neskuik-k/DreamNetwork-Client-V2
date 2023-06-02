package be.alexandre01.dreamnetwork.core.console.accessibility;

import be.alexandre01.dreamnetwork.core.console.accessibility.AccessibilityMenu.Operation.OperationType;

public class AcceptOrRefuse implements AccessibilityMenu.ValueInput{
    AcceptOrRefuseListener listener;
    String noArg;
    String yesArg;

    public AcceptOrRefuse(String yes, String no, AcceptOrRefuseListener listener){
        this.listener = listener;
        this.noArg = no;
        this.yesArg = yes;
    }

    @Override
    public void onTransition(AccessibilityMenu.ShowInfos infos) {
        listener.transition(infos);
    }



    @Override
    public AccessibilityMenu.Operation received(AccessibilityMenu.PromptText prompt, String[] args, AccessibilityMenu.ShowInfos infos) {
        String value = prompt.getValue();
        if(value.equalsIgnoreCase(yesArg)){
            return AccessibilityMenu.Operation.set(listener.accept(value, args, infos));
        }else if(value.equalsIgnoreCase(noArg)){
            return AccessibilityMenu.Operation.set(listener.refuse(value, args, infos));
        }
        return AccessibilityMenu.Operation.set(OperationType.RETRY);
    }

    public interface AcceptOrRefuseListener{

        public void transition(AccessibilityMenu.ShowInfos infos);
        public OperationType accept(String value, String[] args,AccessibilityMenu.ShowInfos infos);

        public OperationType refuse(String value, String[] args,AccessibilityMenu.ShowInfos infos);


    }



}
