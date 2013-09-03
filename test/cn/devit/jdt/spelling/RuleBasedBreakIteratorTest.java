package cn.devit.jdt.spelling;

import java.text.BreakIterator;

import org.junit.Test;

public class RuleBasedBreakIteratorTest {

    @Test
    public void test() {
        String stringToExamine = " getUUIDAndAbc(String args[],Map<String,Map<String,String>> aMap)";
        // print each word in order
        // BreakIterator boundary = BreakIterator.getWordInstance();
//        BreakIterator boundary = CommonBreakIterator.getWordInstance();
        CommonBreakIterator boundary = new CommonBreakIterator(true);//CommonBreakIterator.getWordInstance();
        boundary.setText(stringToExamine);

        int start = boundary.first();
        for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary
                .next()) {
            System.out.println(stringToExamine.substring(start, end));
        }
        System.out.println("==============");
        CommonWordIterator iterator = new CommonWordIterator(true);
        iterator.setText(stringToExamine);
        start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator
                .next()) {
            System.out.println(stringToExamine.substring(start, end));
        }

    }

}
