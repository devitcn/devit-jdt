package cn.devit.eclipse.spelling;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.text.spelling.DefaultSpellingEngine;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.texteditor.spelling.ISpellingEngine;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingContext;

public class DefaultSpellingEnginePatch extends DefaultSpellingEngine
        implements ISpellingEngine {

    private NameSpellingChecker javaSpellingEngine;

    public DefaultSpellingEnginePatch() {
        super();
        javaSpellingEngine = new NameSpellingChecker();

    }

    @Override
    public void check(IDocument document, IRegion[] regions,
            SpellingContext context, ISpellingProblemCollector collector,
            IProgressMonitor monitor) {
        // System.out.println("==document:" + document);
        // System.out.println(context.getContentType());
        // for (IRegion item : regions) {
        // System.out.println(item.getOffset() + "," + item.getLength());
        // }

        if (JavaCore.JAVA_SOURCE_CONTENT_TYPE
                .equals(context.getContentType().getId())) {
            // System.out.println("checking use " + javaSpellingEngine);
            javaSpellingEngine.check(document, regions, context, collector,
                    monitor);
        } else {
            // System.out.println("check with super.*");
            super.check(document, regions, context, collector, monitor);
        }
    }
}
