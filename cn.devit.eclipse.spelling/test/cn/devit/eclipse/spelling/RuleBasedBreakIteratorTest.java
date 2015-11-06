package cn.devit.eclipse.spelling;

import java.text.BreakIterator;

import org.eclipse.jdt.internal.ui.text.JavaBreakIterator;
import org.junit.Test;

public class RuleBasedBreakIteratorTest {

    @Test
    public void test() {

        String stringToExamine = " getUUIDAndAbc(String args[],Map<String,Map<String,String>> aMap)";
        // print each word in order
        // BreakIterator boundary = BreakIterator.getWordInstance();
//        BreakIterator boundary = CommonBreakIterator.getWordInstance();
        JavaBreakIterator boundary = new JavaBreakIterator();//CommonBreakIterator.getWordInstance();
        boundary.setText(stringToExamine);

        int start = boundary.first();
        for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary
                .next()) {
            System.out.println(stringToExamine.substring(start, end));
        }
        //TODO add assert.
    }

}
