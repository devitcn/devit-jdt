package cn.devit.jdt.spelling;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.text.spelling.DefaultSpellingEngine;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.texteditor.spelling.ISpellingEngine;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingContext;

@SuppressWarnings({ "restriction" })
public class Engine1 extends DefaultSpellingEngine implements ISpellingEngine {

    /** Java source content type */
    @SuppressWarnings("unused")
    private static final IContentType JAVA_CONTENT_TYPE = Platform
            .getContentTypeManager().getContentType(
                    JavaCore.JAVA_SOURCE_CONTENT_TYPE);

    private NameSpellingChecker javaSpellingEngine;

    public Engine1() {
        super();
        javaSpellingEngine = new NameSpellingChecker();

    }

    void abc() {

    }

    @Override
    public void check(IDocument document, IRegion[] regions,
            SpellingContext context, ISpellingProblemCollector collector,
            IProgressMonitor monitor) {
        System.out.println("==document:" + document);
        System.out.println(context.getContentType());
        for (IRegion item : regions) {
            System.out.println(item.getOffset() + "," + item.getLength());
        }

        if (JavaCore.JAVA_SOURCE_CONTENT_TYPE.equals(context.getContentType()
                .getId())) {
            System.out.println("checking use " + javaSpellingEngine);
            javaSpellingEngine.check(document, regions, context, collector,
                    monitor);
        } else {
            System.out.println("check with super.*");
            super.check(document, regions, context, collector, monitor);
        }
    }
}
