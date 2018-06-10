package cn.devit.jdt.hover;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.text.java.hover.JavadocBrowserInformationControlInput;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;

/**
 * 尽可能的捕捉文档信息。
 * <p>
 * 如果POJO的get set方法没有文档注释，那么就使用field javadoc，
 * 
 * 对于Entity来说字段的注释非常重要，里面蕴含了关系模型的信息。
 * 
 * 如果连field javadoc也没有，考虑捕捉普通注释。
 * 
 * 
 * 如果是枚举字段，那么同时显示枚举字段的构造函数值。
 * 
 *
 * @author lxb
 *
 */
public class JavadocHover
        extends org.eclipse.jdt.internal.ui.text.java.hover.JavadocHover {

    @Override
    public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
        IJavaElement[] elements = getJavaElementsAt(textViewer, hoverRegion);
        if (elements == null || elements.length == 0)
            return null;

        /*
         * 寻找合适的显示
         */
        if (elements.length == 1) {
            IJavaElement member = elements[0];
            if (member instanceof IMethod) {
                try {
                    if (((IMethod) member).getJavadocRange() != null) {
                        System.out.println("has doc skip.");
                    } else {
                        String name = member.getElementName();
                        boolean isGetter = name.startsWith("get") //$NON-NLS-1$
                                && name.length() > 3;
                        boolean isBooleanGetter = name.startsWith("is") //$NON-NLS-1$
                                && name.length() > 2;
                        boolean isSetter = name.startsWith("set") //$NON-NLS-1$
                                && name.length() > 3;

                        if (isGetter || isBooleanGetter || isSetter) {
                            String propertyName = firstToLower(
                                    name.substring(isBooleanGetter ? 2 : 3));
                            IType type = ((IMember) member).getDeclaringType();
                            IField field = type.getField(propertyName);
                            elements = new IJavaElement[] { field };
                            //TODO 出不来字段上的注解，字段的注解是从第二个参数取的。
//                            JavadocBrowserInformationControlInput e = getHoverInfo(elements, getEditorInputJavaElement(), hoverRegion,
//                                    null);
                            //if the type is JPA entity class
                            IAnnotation annotation = field.getDeclaringType().getAnnotation("entity");
                            JavadocBrowserInformationControlInput e = getHoverInfo(elements, field.getTypeRoot(), hoverRegion,
                                    null);
                            return e;
                        }
                    }
                } catch (JavaModelException e) {
                    //I don't care
                }
            }
        }
        return null;
    }

    public static String firstToLower(String propertyName) {
        char[] c = propertyName.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return String.valueOf(c);
    }
}
