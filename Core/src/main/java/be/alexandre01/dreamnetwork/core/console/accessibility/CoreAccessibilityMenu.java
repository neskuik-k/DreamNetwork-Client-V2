package be.alexandre01.dreamnetwork.core.console.accessibility;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeContainer;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.console.ConsolePath;
import be.alexandre01.dreamnetwork.api.console.IConsoleReader;
import be.alexandre01.dreamnetwork.core.console.ConsoleReader;
import be.alexandre01.dreamnetwork.api.console.accessibility.AccessibilityMenu;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.api.config.GlobalSettings;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

public class CoreAccessibilityMenu extends AccessibilityMenu {

    List<FinishCatch> finishCatches = new ArrayList<>();

    ScheduledExecutorService executor;

    public AccessibilityMenu.Operation[] getArray(){
        return operations;
    }
    public CoreAccessibilityMenu(){
        // Ignore
    }

    public void executeFinishCatches(){
        for (int i = 0; i < finishCatches.size(); i++) {
            FinishCatch finishCatch = finishCatches.get(0);
            finishCatches.remove(0);
            finishCatch.onFinish();
        }
    }

    public CoreAccessibilityMenu(String consoleName){
        setConsoleName(consoleName);
    }
    public void show(boolean clear){
        if(clear){
            currentPage = -1;
            skipToNext();
        }
        Console.setActualConsole(console.name,true,false);
    }

    public void show(){
        show(true);
    }

    public void insertArgumentBuilder(String value, NodeContainer... n){
        this.arguments.putAll(value, Arrays.asList(n));
    }

    public void setArgumentsBuilder(NodeContainer... objects){
        for (NodeContainer object : objects) {
            new NodeBuilder(object, console);
        }
    }





    @Override
    public AccessibilityMenu addValueInput(PromptText prompt, int pos, ValueInput valueInput){

        infos.put(prompt.getValue(), new ShowInfos());
        prompt.setInput(valueInput);
        if(currentInput == null){
            currentInput = prompt.getValue();
        }
        if(pos != -1){
            insertPage(pos,prompt.getValue());
        }else{
            pages.put(pages.size(), prompt.getValue());
        }
        prompts.put(prompt.getValue(),prompt);

        return this;
    }

    public AccessibilityMenu injectValueAfter(PromptText prompt, ValueInput valueInput){
        return addValueInput(prompt,currentPage+1,valueInput);
    }


    private void insertPage(int newPosition, String newVal) {
        int maxKey = pages.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);

        for (int i = maxKey; i >= newPosition; i--) {
            String value = pages.get(i);
            pages.put(i + 1, value);
        }

        pages.put(newPosition, newVal);
    }
    public AccessibilityMenu addValueInput(PromptText prompt, ValueInput valueInput){
        return addValueInput(prompt,-1,valueInput);
    }

    public void addFinishCatch(FinishCatch finishCatch){
        finishCatches.add(finishCatch);
    }
    public Operation retry(){
        Operation op = wroteCurrent(Operation.OperationType.RETRY);
        redrawScreen();
        return op;
    }


    @Override
    public Operation errorAndRetry(ShowInfos infos){
        Operation op = wroteCurrent(Operation.OperationType.ERROR);
        redrawScreen();
        return op;
    }

    private Operation wroteCurrent(Operation.OperationType type){
        if(currentOperation != null){
            currentOperation.setType(type);
            return currentOperation;
        }
        return currentOperation = Operation.set(type);
    }

    public Operation skip(){
        return wroteCurrent(Operation.OperationType.SKIP);
    }

    public void redrawScreen(){
        Console.clearConsole();
        drawInfos();
    }

    public void drawInfos(){
        ShowInfos info = infos.get(currentInput);
        if(currentOperation != null){
            if(currentOperation.getType() == Operation.OperationType.ERROR){
                Console.debugPrint(Colors.RED_UNDERLINED+info.getError()+Colors.RESET);
            }
        }
        if(info.getHeadMessage() != null){
            Console.debugPrint(info.getHeadMessage().replace("%data%",currentInput));
        }
        if(info.getWritingMessage() != null){
            if(Main.getGlobalSettings().getTermMode() == GlobalSettings.TerminalMode.SAFE){
                Console.debugPrint(info.getWritingMessage());
                return;
            }
            console.setWriting(info.getWritingMessage());
        }


    }

    @Override
    public AccessibilityMenu.Operation switchTo(AccessibilityMenu value) {
        return null;
    }

    public Operation switchTo(CoreAccessibilityMenu value){
        return Operation.set(Operation.OperationType.SWITCH,value);
    }
    public Operation finish() {
        return wroteCurrent(Operation.OperationType.FINISH);
    }

    public void exitConsole(){
        Console.setActualConsole(ConsolePath.Main.DEFAULT);
    }

    private void playCurrentTransition(boolean macro){
        PromptText text = prompts.get(currentInput);


        if(text.getMacro() != null && macro){
            IConsoleReader.getReader().runMacro(text.getMacro());
        }

        console.completorNodes.clear();
        if(text.getSuggestions() != null){
            insertArgumentBuilder(currentInput, text.getSuggestions());
        }
        text.getInput().onTransition(infos.get(currentInput));
        setArgumentsBuilder(arguments.get(currentInput).toArray(new NodeContainer[0]));
        console.reloadCompletors();
    }

    public Operation getOperation(){
        return currentOperation;
    }

    public Operation getOperation(String value){
        return values.get(value);
    }

    public void changeTo(Console console){
        Console.setActualConsole(console.name);
    }
    public void changeTo(CoreAccessibilityMenu menu){
        changeTo(menu.console);
    }

    protected void setConsoleName(String consoleName) {
        this.consoleName = consoleName;
    }

    public void buildAndRun(){
        buildAndRun(null);
    }
    public void buildAndRun(String consoleName){
        if(consoleName != null){
            this.consoleName = consoleName;
        }
        //Console.debugPrint("Building console "+this.consoleName);
        console = Console.load(this.consoleName);
        console.setNoHistory(true);
        try {
            if(currentInput == null){
                throw new NullPointerException("No input found");
            }

          //  playCurrentTransition(false);
            console.setConsoleAction(new Console.IConsole() {
                @Override
                public void listener(String[] args) {
                    StringBuilder builder = new StringBuilder();
                    //Console.print("console name "+console.name);
                    for (int i = 0; i < args.length; i++) {
                        builder.append(args[i]);
                        if(i != args.length - 1){
                            builder.append(" ");
                        }

                        PromptText promptText = prompts.get(currentInput);
                      //  Console.print(currentInput);
                        if(promptText == null){
                            Console.fine("Prompt text is null");
                            return;
                        }
                        promptText.setValue( builder.toString());

                        if(args[0].equalsIgnoreCase(":exit")){
                            forceExit();
                            return;
                        }

                        Operation operation = promptText.getInput().received(promptText, args, infos.get(currentInput));
                        if(operation != null){
                            injectOperation(operation);
                        }
                    }

                }

                @Override
                public void consoleChange() {

                }
            });

            console.setKillListener(reader -> {
                //Shutdown other things
                console.addOverlay(new Console.Overlay() {
                    @Override
                    public void on(String data) {
                        disable();
                        if(data.equalsIgnoreCase("y") ||data.equalsIgnoreCase("yes")){
                            // quit
                            forceExit();
                        }else {
                            Console.debugPrint(Console.getFromLang("menu.cancel"));
                        }
                    }
                }, Console.getFromLang("menu.cancelWriting"));
                return true;
            });
        }catch (Exception e){
            Console.bug(e);
        }

    }

    public void clearData(){
        pages.clear();
        values.clear();
        infos.clear();
        arguments.clear();
        switchedFrom = null;
        finishCatches.clear();
        currentOperation = null;
        currentPage = 0;
        currentInput = null;
    }

    public void forceExit(){
        Console.setBlockConsole(false);
        exitConsole();
        clearData();
        injectOperation(Operation.set(Operation.OperationType.FINISH));
    }
    public void skipToNext(){
        Console.print("Skip to next");
        currentPage++;
        Console.print("Skip to page "+currentPage);
        currentInput = pages.get(currentPage);
        Console.print("Skip to input "+currentInput);

        playCurrentTransition(true);
        redrawScreen();
        currentOperation = null;
    }


    public void injectOperation(Operation operation){

        if(Console.isBlockConsole()){
            Console.setBlockConsole(false);
        }

        if(operation.getType() == Operation.OperationType.ACCEPTED){
            values.put(currentInput,operation);
            skipToNext();
            return;
        }

        if(operation.getType() == Operation.OperationType.RETRY){
            redrawScreen();
            return;
        }

        if(operation.getType() == Operation.OperationType.SKIP){
            skipToNext();
            return;
        }

        if(operation.getType() == Operation.OperationType.ERROR){
            return;
        }

        if(operation.getType() == Operation.OperationType.WAIT){
            Console.setBlockConsole(true);
            return;
        }

        if(operation.getType() == Operation.OperationType.SWITCH){
            CoreAccessibilityMenu menu = (CoreAccessibilityMenu) operation.getReturnValue();
            menu.buildAndRun();
            menu.switchedFrom = this;

            if(safeRemove){
                menu.addFinishCatch(() -> {
                    clearData();
                    exitConsole();
                });
            }
            menu.show();
            return;
        }

        if(operation.getType() == Operation.OperationType.FINISH){
            executeFinishCatches();
            if(switchedFrom != null)
                switchedFrom.executeFinishCatches();
            return;
        }
    }



}
