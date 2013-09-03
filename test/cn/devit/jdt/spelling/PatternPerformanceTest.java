package cn.devit.jdt.spelling;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.internal.ui.text.spelling.SpellCheckIterator;
import org.eclipse.jface.text.Region;
import org.junit.Test;

public class PatternPerformanceTest {

    Pattern methodSignaturePattern = Pattern
            .compile("\\s*(?:public|protected|private)?(?:\\s+static)?\\s+(?:<[\\w<>\\s,\\?]+?>\\s+)?(?:\\w[\\w<>,\\?]+>?)\\s+((?:\\$|\\w)+)\\((?:[^)(]*)(\\))[^}]+");

    @Test(timeout=1000)
    public void test() throws IOException {
        File file = new File(
                "src/cn/devit/jdt/spelling/NameSpellingChecker.java");
        FileInputStream is = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        StringBuilder sb = new StringBuilder();
        char[] tmp = new char[100];

        while (isr.ready()) {
            int len = isr.read(tmp);
            sb.append(tmp, 0, len);
        }
        System.out.println(sb.toString());

        // method
        Matcher matcher = methodSignaturePattern.matcher(sb);
        while (matcher.find()) {
            int offset = matcher.start(1);
            // from first group to end of string. will check
            // method name and every parameter name.
            int length = matcher.end(2) - matcher.start(1);
            System.out.println(offset);
            System.out.println(length);

            System.out.println("check method signature:"
                    + sb.substring(offset, offset + length));

        }

    }
}
