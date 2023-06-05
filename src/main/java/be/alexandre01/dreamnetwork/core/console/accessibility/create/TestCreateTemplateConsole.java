package be.alexandre01.dreamnetwork.core.console.accessibility.create;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlesNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.RamNode;
import be.alexandre01.dreamnetwork.api.service.IContainer;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.ConsoleReader;
import be.alexandre01.dreamnetwork.core.console.accessibility.AcceptOrRefuse;
import be.alexandre01.dreamnetwork.core.console.accessibility.AccessibilityMenu;
import be.alexandre01.dreamnetwork.core.console.accessibility.install.InstallTemplateConsole;
import be.alexandre01.dreamnetwork.core.service.JVMExecutor;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleData;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleInfo;
import be.alexandre01.dreamnetwork.core.utils.ASCIIART;
import be.alexandre01.dreamnetwork.core.utils.clients.NumberArgumentCheck;
import be.alexandre01.dreamnetwork.core.utils.clients.RamArgumentsChecker;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;
import static be.alexandre01.dreamnetwork.core.console.Console.getFromLang;

public class TestCreateTemplateConsole extends AccessibilityMenu {
    String[] opt;
    JVMExecutor jvmExecutor;
    BundleData bundleData;
    int xms;
       public TestCreateTemplateConsole(String bundleName, String serverName,String mods, String ramMin, String ramMax, String port){
           opt = new String[]{bundleName,serverName,mods,ramMin,ramMax,port};
           insertArgumentBuilder("bundleName", create(new BundlesNode(false, false,false)));
           insertArgumentBuilder("yes1", create("yes","no"));
           addValueInput(PromptText.create("bundleName").setMacro(opt[0]), new ValueInput() {
               @Override
               public void onTransition(ShowInfos infos) {
                     infos.onEnter("Please enter the name of the bundle");
               }

               @Override
               public Operation received(PromptText prompt, String[] args,ShowInfos infos) {
                   BundleData current = Core.getInstance().getBundleManager().getBundleData(args[0]);

                   if(current == null){
                       infos.error(getFromLang("bundle.noBundle", args[0]));
                       //Console.debugPrint(errorLine);
                       return retry();
                   }
                    bundleData = current;
                   console.completorNodes.clear();
                   console.reloadCompletors();
                   return Operation.accepted(current);
               }
           });



           addValueInput(PromptText.create("serviceName"), new ValueInput() {
               @Override
               public void onTransition(ShowInfos infos) {
                   ConsoleReader.sReader.runMacro(opt[1]);
                //   infos.onEnter("Please enter the name of the service");
                   if(getOperation("bundleName").getFrom(BundleData.class).getJvmType().equals(IContainer.JVMType.SERVER)){
                       infos.onEnter(getFromLang("service.creation.ask.serverName"));
                   }else {
                       infos.onEnter(getFromLang("service.creation.ask.proxyName"));
                   }
               }
               @Override
               public Operation received(PromptText prompt, String[] args,ShowInfos infos) {
                   String[] illegalChars = {"\\", "/", ":", "*", "?", "\"", "<", ">", "|", "-"};
                   for (String illegalChar : illegalChars) {
                       if (illegalChar.contains(args[0])) {
                           infos.error(getFromLang("service.creation.badCharacter"));
                           return errorAndRetry(infos);
                       }
                   }
                   return Operation.accepted(args[0]);
               }
           });

           addValueInput(PromptText.create("mode"), new ValueInput() {
               @Override
               public void onTransition(ShowInfos infos) {
                     infos.onEnter("Please enter the mode of the service");
                        ConsoleReader.sReader.runMacro(opt[2]);
                     insertArgumentBuilder("mode", create("STATIC","DYNAMIC"));
               }

               @Override
               public Operation received(PromptText value, String[] args, ShowInfos infos) {
                   IJVMExecutor.Mods mods;
                     try {
                          mods = IJVMExecutor.Mods.valueOf(args[0]);
                     }catch (Exception e){
                            infos.error(getFromLang("service.creation.badMode"));
                            return errorAndRetry(infos);
                     }
                     return Operation.accepted(mods);
               }
           });

           addValueInput(PromptText.create("xms"), new ValueInput() {
               @Override
               public void onTransition(ShowInfos infos) {
                   infos.onEnter("Please enter the xms of the service");
                   ConsoleReader.sReader.runMacro(opt[3]);
                   insertArgumentBuilder("xms", create(new RamNode(0)));
               }

               @Override
               public Operation received(PromptText value, String[] args, ShowInfos infos) {
                   if(!RamArgumentsChecker.check(args[0])){
                       infos.error(getFromLang("service.creation.incorrectRamArgument"));
                       return errorAndRetry(infos);
                   }

                   xms = RamArgumentsChecker.getRamInMB(args[0]);

                   return Operation.accepted(args[0]);
               }
           });

              addValueInput(PromptText.create("xmx"), new ValueInput() {
                @Override
                public void onTransition(ShowInfos infos) {
                     infos.onEnter("Please enter the xmx of the service");
                     //RamMode get the xms data to determine what is upper xms.
                     insertArgumentBuilder("xmx", create(new RamNode(xms)));
                     ConsoleReader.sReader.runMacro(opt[4]);
                }

                @Override
                public Operation received(PromptText value, String[] args, ShowInfos infos) {
                     if(!RamArgumentsChecker.check(args[0])){
                          infos.error(getFromLang("service.creation.incorrectRamArgument"));
                          return errorAndRetry(infos);
                     }

                     float xmx = RamArgumentsChecker.getRamInMB(args[0]);

                     if(xmx < xms){
                          infos.error(getFromLang("service.creation.xmxInferiorToXms"));
                          return errorAndRetry(infos);
                     }

                     return Operation.accepted(args[0]);
                }
              });

              addValueInput(PromptText.create("port").setMacro(opt[5]), new ValueInput() {
                @Override
                public void onTransition(ShowInfos infos) {
                     infos.onEnter("Please enter the port of the service");
                }

                @Override
                public Operation received(PromptText value, String[] args, ShowInfos infos) {
                    if(!NumberArgumentCheck.check(args[0]) && !args[0].equalsIgnoreCase("auto")){
                        infos.error(getFromLang("service.creation.wrongPort").replaceFirst("%var%", args[0]));
                        return errorAndRetry(infos);
                    }
                    int port = 0;
                    if(!args[0].equalsIgnoreCase("auto")) {
                        port = Integer.parseInt(args[0]);
                    }
                    String serverName = getOperation("serviceName").getFrom(String.class);

                    IJVMExecutor.Mods mods = getOperation("mode").getFrom(IJVMExecutor.Mods.class);

                    String xms = getOperation("xms").getFrom(String.class);
                    String xmx = getOperation("xmx").getFrom(String.class);
                    // BEGIN OF ADDING SERVER
                    BundleInfo bundleInfo = bundleData.getBundleInfo();
                    Console.debugPrint(getFromLang("service.creation.addingServerOnBundle", serverName, bundleData.getName()));

                    IContainer.JVMType jvmType = bundleInfo.getType();

                    boolean proxy = bundleInfo.getType() == IContainer.JVMType.PROXY;

                    jvmExecutor = (JVMExecutor) Core.getInstance().getJvmContainer().getJVMExecutor(serverName, bundleData);
                    if (jvmExecutor == null) {
                        Console.printLang("service.creation.creatingServerOnBundle", serverName, bundleInfo.getName());
                        Config.createDir("bundles/"+bundleData.getName()+"/"+serverName,false);
                        jvmExecutor = new JVMExecutor(bundleData.getName(), serverName, mods, xms,  xmx,  port, proxy, true,bundleData);
                    }else {
                        jvmExecutor.updateConfigFile(bundleData.getName(), serverName, mods,xms, xmx, port, proxy, null, null, null);
                    }

                    return Operation.accepted(args[0]);
                }
              });
           addValueInput(PromptText.create("yes1")
                           .setMacro("yes")
                           .setSuggestions(create("yes", "no")),
                   new AcceptOrRefuse("yes", "no", new AcceptOrRefuse.AcceptOrRefuseListener() {
                       @Override
                       public void transition(ShowInfos infos) {
                           infos.onEnter("Please enter yes or no");
                           infos.writing(getFromLang("service.creation.ask.installServer"));
                       }

                       @Override
                       public Operation accept(String value, String[] args, ShowInfos infos) {
                           injectOperation(switchTo(new InstallTemplateConsole(jvmExecutor)));
                           return null;
                       }

                       @Override
                       public Operation refuse(String value, String[] args, ShowInfos infos) {
                           return null;
                       }
                   }));
       }
       @Override
       public void redrawScreen(){
            Console.clearConsole();
            ASCIIART.sendAdd();
            drawInfos();
        }

    public interface Future{
        public void onResponse();
        public void finish();
    }
}
