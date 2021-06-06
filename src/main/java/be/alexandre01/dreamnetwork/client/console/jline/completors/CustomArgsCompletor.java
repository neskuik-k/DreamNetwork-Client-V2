package be.alexandre01.dreamnetwork.client.console.jline.completors;

import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.internal.Log;
import jline.internal.Preconditions;

import java.util.LinkedList;
import java.util.List;

public class CustomArgsCompletor extends ArgumentCompleter {
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        Preconditions.checkNotNull(candidates);
        ArgumentCompleter.ArgumentDelimiter delim = this.getDelimiter();
        ArgumentCompleter.ArgumentList list = delim.delimit(buffer, cursor);
        int argpos = list.getArgumentPosition();
        int argIndex = list.getCursorArgumentIndex();
        if (argIndex < 0) {
            return -1;
        } else {
            List<Completer> completers = this.getCompleters();
            Completer completer;
            if (argIndex >= completers.size()) {
                completer = (Completer)completers.get(completers.size() - 1);
            } else {
                completer = (Completer)completers.get(argIndex);
            }

            int ret;
            for(ret = 0; this.isStrict() && ret < argIndex; ++ret) {
                Completer sub = (Completer)completers.get(ret >= completers.size() ? completers.size() - 1 : ret);
                String[] args = list.getArguments();
                String arg = args != null && ret < args.length ? args[ret] : "";
                List<CharSequence> subCandidates = new LinkedList();
                if (sub.complete(arg, arg.length(), subCandidates) == -1) {
                    return -1;
                }

                if (!subCandidates.contains(arg)) {
                    return -1;
                }
            }

            ret = completer.complete(list.getCursorArgument(), argpos, candidates);
            if (ret == -1) {
                return -1;
            } else {
                int pos = ret + list.getBufferPosition() - argpos;
                if (cursor != buffer.length() && delim.isDelimiter(buffer, cursor)) {
                    for(int i = 0; i < candidates.size(); ++i) {
                        CharSequence val;
                        for(val = (CharSequence)candidates.get(i); val.length() > 0 && delim.isDelimiter(val, val.length() - 1); val = val.subSequence(0, val.length() - 1)) {
                        }

                        candidates.set(i, val);
                    }
                }

                Log.trace(new Object[]{"Completing ", buffer, " (pos=", cursor, ") with: ", candidates, ": offset=", pos});
                return pos;
            }
        }
    }

}
