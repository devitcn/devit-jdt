package cn.devit.eclipse.spelling;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.text.JavaBreakIterator;
import org.eclipse.jdt.internal.ui.text.spelling.JavaSpellingEngine;
import org.eclipse.jdt.internal.ui.text.spelling.SpellCheckIterator;
import org.eclipse.jdt.internal.ui.text.spelling.engine.ISpellChecker;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingContext;

public class NameSpellingChecker extends JavaSpellingEngine {

    private boolean checkClassMethodFieldName = true;

    static Pattern packageNamePattern = Pattern.compile(
            "package (?:[\\p{L}\\p{N}]+\\s*\\.\\s*)*([\\p{L}\\p{N}]+)\\s*;");

    static Pattern classNamePattern = Pattern
            .compile("(?:class|interface|enum)\\s+(\\w+)[^{]*\\{");

    /**
     * A magic pattern to match method name.
     */
    static Pattern methodSignaturePattern = Pattern.compile(
            "(?:\\w+|>)\\s+(\\w+)\\s*\\(");

    /**
     * A magic pattern to match variable name,method parameter name.
     * It doesn't exactly math then variable but quick and simple.
     */
    static Pattern variableDeclarationPattern = Pattern
            .compile("(?:\\w|>)+\\s+(\\w+)\\s*(?:\\)|,|=|;)");

    /**
     * Find user generated keywords in java source file.
     */
    @Override
    protected void check(IDocument document, IRegion[] regions,
            ISpellChecker checker, ISpellingProblemCollector collector,
            IProgressMonitor monitor) {
        // call parent to check comments section.
        super.check(document, regions, checker, collector, monitor);
        // we handle java section.
        SpellEventListener listener = new SpellEventListener(collector,
                document);
        try {

            System.out.println("document regoin count=" + regions.length);// maybe
                                                                          // 1;
            // for each part in source file.
            for (int i = 0; i < regions.length; i++) {
                IRegion region = regions[i];
                ITypedRegion[] partitions = TextUtilities.computePartitioning(
                        document, IJavaPartitions.JAVA_PARTITIONING,
                        region.getOffset(), region.getLength(), false);

                boolean firstJavaRegion = true;
                for (ITypedRegion item : partitions) {
                    if (monitor != null && monitor.isCanceled())
                        return;
                    // a pointer record end position of region scanned.use this point to avoid duplicated regular match.
                    int pointer = item.getOffset();
                    int remain = item.getLength();
//                    System.out.println(item.getType() + ":"
//                            + document.get(item.getOffset(), item.getLength()));
                    if (checkClassMethodFieldName && (item.getType()
                            .equals(IJavaPartitions.JAVA_PARTITIONING)
                            || item.getType()
                                    .equals(IDocument.DEFAULT_CONTENT_TYPE))) {

                        Matcher matcher;

                        // There is only one package declaration at file
                        // beginning,so use firstJavaRegion var boolean switcher.
                        if (firstJavaRegion) {
                            // Only check package name that last segment.
                            matcher = packageNamePattern
                                    .matcher(document.get(pointer, remain));
                            if (matcher.find()) {
                                int offset = item.getOffset()
                                        + matcher.start(1);
                                int length = matcher.end(1) - matcher.start(1);

                                pointer = item.getOffset() + matcher.end();
                                remain = remain - matcher.end();

                                Region declaration = new Region(offset, length);
                                checker.execute(listener,
                                        new SpellCheckIterator(document,
                                                declaration,
                                                checker.getLocale(),
                                                new JavaBreakIterator()));

                                // continue;
                            }
                        }

                        // Only check class name declaration.
                        matcher = classNamePattern
                                .matcher(document.get(pointer, remain));
                        int p1 = pointer;
                        while (matcher.find()) {
                            int offset = p1 + matcher.start(1);
                            int length = matcher.end(1) - matcher.start(1);
                            Region declaration = new Region(offset, length);

                            //move pointer forward. TODO this MAY be missing block between main and nested class declaration if no any comment.
                            pointer = pointer +matcher.end();
                            remain = remain - matcher.end();
                             System.out.println("check class declaration:"
                             + document.get(declaration.getOffset(),
                             declaration.getLength()));
                            checker.execute(listener,
                                    new SpellCheckIterator(document,
                                            declaration, checker.getLocale(),
                                            new JavaBreakIterator()));
                        }
                        // continue;

                        // Only check method declaration and parameter names.
                        matcher = methodSignaturePattern
                                .matcher(document.get(pointer, remain));

                        int p = pointer;
                        while (matcher.find()) {
                            int offset = pointer + matcher.start(1);
                            //skip new class block.
                            if(matcher.group(0).startsWith("new")){
                                continue;
                            }
                            //if @Override before method name, skip it.
                            if (document.get(p, matcher.start())
                                    .contains("@Override")) {
                                p = p + matcher.end();
                                continue;
                            }
                            // from first group to end of string. will check
                            // method name and every parameter name.
                            int length = matcher.end(1) - matcher.start(1);
                            Region declaration = new Region(offset, length);
                            System.out.println("check method signature:"
                                    + document.get(declaration.getOffset(),
                                            declaration.getLength()));

                            checker.execute(listener,
                                    new SpellCheckIterator(document,
                                            declaration, checker.getLocale(),
                                            new JavaBreakIterator()));

                        }

                        // variable
                        matcher = variableDeclarationPattern
                                .matcher(document.get(pointer, remain));
                        while (matcher.find()) {
                            int offset = pointer + matcher.start(1);
                            int length = matcher.end(1) - matcher.start(1);
                            Region declaration = new Region(offset, length);
                            System.out.println("check variable:"
                                    + document.get(declaration.getOffset(),
                                            declaration.getLength()));

                            checker.execute(listener,
                                    new SpellCheckIterator(document,
                                            declaration, checker.getLocale(),
                                            new JavaBreakIterator()));

                        }

                        firstJavaRegion = false;
                    }

                }

            }
        } catch (BadLocationException x) {
            // ignore: the document has been changed in another thread and will
            // be checked again
        } catch (AssertionFailedException x) {
            // ignore: the document has been changed in another thread and will
            // be checked again
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jdt.internal.ui.text.spelling.SpellingEngine#check(org.
     * eclipse .jface.text.IDocument, org.eclipse.jface.text.IRegion[],
     * org.eclipse.ui.texteditor.spelling.SpellingContext,
     * org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector,
     * org.eclipse.core.runtime.IProgressMonitor) this will create new final
     * real word checker and invoke check(IDocument document, IRegion[] regions,
     * ISpellChecker checker, ISpellingProblemCollector collector,
     * IProgressMonitor monitor) {
     */
    @Override
    public void check(IDocument document, IRegion[] regions,
            SpellingContext context, ISpellingProblemCollector collector,
            IProgressMonitor monitor) {
        super.check(document, regions, context, collector, monitor);
    }

}
