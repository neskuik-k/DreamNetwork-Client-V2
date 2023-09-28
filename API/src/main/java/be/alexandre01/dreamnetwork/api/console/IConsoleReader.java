package be.alexandre01.dreamnetwork.api.console;

import be.alexandre01.dreamnetwork.api.DNUtils;

import org.jline.builtins.Completers;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.terminal.Terminal;

import java.util.List;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 02/09/2023 at 21:26
*/
public interface IConsoleReader {
   public LineReaderImpl getSReader();
   public Terminal getTerminal();

   List<Completers.TreeCompleter.Node> getNodes();

   void setNodes(List<Completers.TreeCompleter.Node> list);
   public IConsoleHighlighter getDefaultHighlighter();

   public void reloadCompleter();

   public static LineReaderImpl getReader(){
      return DNUtils.get().getConsoleManager().getConsoleReader().getSReader();
   }

   public static void reloadCompleters(){
      DNUtils.get().getConsoleManager().getConsoleReader().reloadCompleter();
   }

}
