package cn.devit.eclipse.spelling;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class PatternPerformanceTest {

    Pattern methodSignaturePattern = Pattern
            .compile("\\s*(?:public|protected|private)?(?:\\s+static)?\\s+(?:<[\\w<>\\s,\\?]+?>\\s+)?(?:\\w[\\w<>,\\?]+>?)\\s+((?:\\$|\\w)+)\\((?:[^)(]*)(\\))[^}]+");

    @Test(timeout = 1000)
    public void should_finish_in_seconds() throws IOException {
        File file = new File(
                "src/cn/devit/jdt/spelling/NameSpellingChecker.java");
        FileInputStream is = new FileInputStream(file);
        InputStreamReader inputStreamReader = new InputStreamReader(is, "UTF-8");
        StringBuilder stringBuilder = new StringBuilder();
        char[] tmp = new char[100];

        while (inputStreamReader.ready()) {
            int length = inputStreamReader.read(tmp);
            stringBuilder.append(tmp, 0, length);
        }
        inputStreamReader.close();
        System.out.println(stringBuilder.toString());

        // method
        Matcher matcher = methodSignaturePattern.matcher(stringBuilder);
        while (matcher.find()) {
            int offset = matcher.start(1);
            // from first group to end of string. will check
            // method name and every parameter name.
            int length = matcher.end(2) - matcher.start(1);
            System.out.println(offset);
            System.out.println(length);

            System.out.println("check method signature:"
                    + stringBuilder.substring(offset, offset + length));

        }

    }
}
