package Coco;

import at.jku.ssw.coco.intellij.CocoLanguage;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;

/**
 * Created by Thomas on 29/12/2014.
 */
public class CocoRAction extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        VirtualFile file = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        String path = file.getParent().getPath();
        String fileContent = e.getData(PlatformDataKeys.FILE_TEXT);
        String filePath = file.getPath();

        String nsName = null, frameDir = null, outDir = null, ddtString = null;

        Scanner scanner = new Scanner(filePath);
        Parser parser = new Parser(scanner);

        parser.trace = new Trace(path);
        parser.tab = new Tab(parser);
        parser.dfa = new DFA(parser);
        parser.pgen = new ParserGen(parser);

        parser.tab.srcName = filePath;
        parser.tab.srcDir = path;
        parser.tab.nsName = nsName;
        parser.tab.frameDir = (frameDir != null) ? frameDir : path;
        parser.tab.outDir = (outDir != null) ? outDir : path;
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
        PsiFile data = e.getData(PlatformDataKeys.PSI_FILE);
        if (data != null && data.getLanguage() == CocoLanguage.INSTANCE) {
        }
    }
}
