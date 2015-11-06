package cn.devit.eclipse.spelling;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.is;

import java.util.regex.Matcher;

import org.junit.Test;

public class NameSpellingCheckerTest {

    @Test
    public void match_package_declaration_pattern() throws Exception {
        //@formatter:off
        String sample = "package foo;"
                + "package com.foo;"
                + "package com.bar.foo;"
                + "package com . bar . foo ; "
                + "package 中文.foo;";
        //@formatter:on
        Matcher matcher = NameSpellingChecker.packageNamePattern
                .matcher(sample);

        int count = 0;
        while (matcher.find()) {
            count++;
            matcher.group(1);
            assertThat(matcher.group(1), is("foo"));
        }
        assertThat(count, is(5));
    }
}
