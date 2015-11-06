package cn.devit.eclipse.spelling;

import org.eclipse.jface.text.Region;

/**
 * Only used for spell check purpose.
 *
 * @author lxb
 *
 */
public class AClassHasWronWords implements AInterfacContainsWrongWords {

    /**
     * should be wrong words.
     */
    public static String WrogWords;

    /**
     * should be wrong method and wrong param name.
     */
    public void wronMethod(String worgParamNam) {

        //hello
        @SuppressWarnings("unused")
        Region declaration = new Region(0, 1);
        //we handle java section.
    }

    /**
     * Should only check string literal.
     */
    @AWronWordAnnotation(value = "", wor = "")
    public void methodWithAnnotation() {

    }

    /*
     * (non-Javadoc)
     *
     * @see cn.devit.eclipse.spelling.AInterfacContainsWrongWords#wron()
     */
    @Override
    public void wron() {
    }

    /**
     * Do check @@override
     */
    @Override
    public void wrongWorda() {
    }

}
