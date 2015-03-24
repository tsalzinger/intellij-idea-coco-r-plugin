package Coco;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;

/**
 * Created by Thomas on 29/12/2014.
 */
public class CocoRAction extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        e.getDataContext();
        String nsName = null, frameDir = null, outDir = null, ddtString = null;
        String srcName = "";
        String srcDir = new File(srcName).getParent();

        Scanner scanner = new Scanner(srcName);
        Parser parser = new Parser(scanner);

        parser.trace = new Trace(srcDir);
        parser.tab = new Tab(parser);
        parser.dfa = new DFA(parser);
        parser.pgen = new ParserGen(parser);

        parser.tab.srcName = srcName;
        parser.tab.srcDir = srcDir;
        parser.tab.nsName = nsName;
        parser.tab.frameDir = frameDir;
        parser.tab.outDir = (outDir != null) ? outDir : srcDir;
        if (ddtString != null) parser.tab.SetDDT(ddtString);

        parser.Parse();

        parser.trace.Close();
        System.out.println(parser.errors.count + " errors detected");
        if (parser.errors.count != 0) {
            throw new FatalError("failure");
        }
    }

    @Override
    public void update(AnActionEvent e) {
        VirtualFile file = (VirtualFile) e.getDataContext().getData(DataConstants.VIRTUAL_FILE);
    }
}
