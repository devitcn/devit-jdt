package cn.devit.jdt.spelling;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.IProgressMonitor;
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

@SuppressWarnings("restriction")
public class NameSpellingChecker extends JavaSpellingEngine {

    private boolean checkClassMethodFieldName = true;

    Pattern packageNamePattern = Pattern
            .compile("package (?:\\s*\\w+\\s*\\.\\s*)*(\\w+)\\s*;");
    Pattern classNamePattern = Pattern
            .compile("(?:class|interface|enum)\\s+(\\w+)");

    Pattern methodSignaturePattern = Pattern
            .compile("\\s*(?:public|protected|private)?(?:\\s+static)?\\s+(?:<[\\w<>\\s,\\?]+?>\\s+)?(?:\\w[\\w<>,\\?]+>?)\\s+((?:\\$|\\w)+)\\((?:[^)(]*)(\\))[^}]+");

    Pattern variableDeclarationPattern = Pattern
            .compile("(?:(?:public|private|static|final)\\s)*\\s?[\\w<>\\s,\\?]+\\s+(\\w+)\\s*(?:=|;)");

    @Override
    protected void check(IDocument document, IRegion[] regions,
            ISpellChecker checker, ISpellingProblemCollector collector,
            IProgressMonitor monitor) {
        // super.check(document, regions, checker, collector, monitor);
        super.check(document, regions, checker, collector, monitor);

        SpellEventListener listener = new SpellEventListener(collector,
                document);
        try {
            for (int i = 0; i < regions.length; i++) {
                IRegion region = regions[i];
                ITypedRegion[] partitions = TextUtilities.computePartitioning(
                        document, IJavaPartitions.JAVA_PARTITIONING,
                        region.getOffset(), region.getLength(), false);

                boolean packageFound = false;
                for (ITypedRegion item : partitions) {
                    if (monitor != null && monitor.isCanceled())
                        return;
                    // System.out.println(item.getType());
                    if (checkClassMethodFieldName
                            && (item.getType().equals(
                                    IJavaPartitions.JAVA_PARTITIONING) || item
                                    .getType().equals(
                                            IDocument.DEFAULT_CONTENT_TYPE))) {

                        Matcher matcher;
                        // package name
                        if (packageFound == false) {
                            matcher = packageNamePattern.matcher(document.get(
                                    item.getOffset(), item.getLength()));
                            if (matcher.find()) {
                                int offset = item.getOffset()
                                        + matcher.start(1);
                                int length = matcher.end(1) - matcher.start(1);
                                Region declaration = new Region(offset, length);

                                System.out.println("check package declaration:"
                                        + document.get(declaration.getOffset(),
                                                declaration.getLength()));
                                packageFound = true;

                                checker.execute(
                                        listener,
                                        new SpellCheckIterator(document,
                                                declaration, checker
                                                        .getLocale(),
                                                new CommonBreakIterator(true)));

                                continue;
                            }
                        }

                        // class name
                        matcher = classNamePattern.matcher(document.get(
                                item.getOffset(), item.getLength()));
                        while (matcher.find()) {
                            int offset = item.getOffset() + matcher.start(1);
                            int length = matcher.end(1) - matcher.start(1);
                            Region declaration = new Region(offset, length);

                            System.out.println("check class declaration:"
                                    + document.get(declaration.getOffset(),
                                            declaration.getLength()));
                            checker.execute(listener, new SpellCheckIterator(
                                    document, declaration, checker.getLocale(),
                                    new CommonBreakIterator(true)));

                            /*
                             * checker.execute(listener, new
                             * SpellCheckIterator(document, item,
                             * checker.getLocale()));
                             */
                        }
                        // continue;

                        // method
                        matcher = methodSignaturePattern.matcher(document.get(
                                item.getOffset(), item.getLength()));
                        while (matcher.find()) {
                            int offset = item.getOffset() + matcher.start(1);
                            // from first group to end of string. will check
                            // method name and every parameter name.
                            int length = matcher.end(2) - matcher.start(1);
                            Region declaration = new Region(offset, length);
                            System.out.println("check method signature:"
                                    + document.get(declaration.getOffset(),
                                            declaration.getLength()));

                            checker.execute(listener, new SpellCheckIterator(
                                    document, declaration, checker.getLocale(),
                                    new CommonBreakIterator(true)));

                        }

                        // variable
                        matcher = variableDeclarationPattern.matcher(document
                                .get(item.getOffset(), item.getLength()));
                        while (matcher.find()) {
                            int offset = item.getOffset() + matcher.start(1);
                            int length = matcher.end(1) - matcher.start(1);
                            Region declaration = new Region(offset, length);
                            System.out.println("check variable:"
                                    + document.get(declaration.getOffset(),
                                            declaration.getLength()));

                            checker.execute(listener, new SpellCheckIterator(
                                    document, declaration, checker.getLocale(),
                                    new CommonBreakIterator(true)));

                        }

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
     * @see
     * org.eclipse.jdt.internal.ui.text.spelling.SpellingEngine#check(org.eclipse
     * .jface.text.IDocument, org.eclipse.jface.text.IRegion[],
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
