package cn.devit.jdt.hover;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
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
         * 如果get set方法没有javadoc
         */
        if (elements.length == 1) {
            IJavaElement member = elements[0];
            if (member instanceof IMethod) {
                try {
                    if (((IMethod) member).getJavadocRange() != null) {
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
                            
                            //如果有field
                            //并且field字段有javadoc
                            if(field==null || field.getJavadocRange()==null) {
                                /*
                                 * 使用返回值类型的注释
                                 */
                                if(isGetter) {
                                    return useReturnTypeDoc(hoverRegion, member,
                                            type);
                                }
                                return null;
                            }
                            elements = new IJavaElement[] { field };
                            
//                            JavadocBrowserInformationControlInput e = getHoverInfo(elements, getEditorInputJavaElement(), hoverRegion,
//                                    null);
                            //if the type is JPA entity class
                            //则显示field 字段的文档
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

    Object useReturnTypeDoc(IRegion hoverRegion, IJavaElement member,
            IType type) throws JavaModelException {
        String returnTypeQName = ((IMethod) member).getReturnType();
        Signature.toString(returnTypeQName);
        
        String elementTypeSignature = Signature.getElementType(returnTypeQName);
        if (elementTypeSignature.length() == 1) {
            // JDK内置类，不管了
            return null;
        }
        String elementTypeName = Signature.toString(elementTypeSignature);
        String[][] resolvedElementTypeNames = type.resolveType(elementTypeName);
        if (resolvedElementTypeNames == null){
            return null;
        }
        String[] types = resolvedElementTypeNames[0];
        String resolvedElementTypeName = Signature.toQualifiedName(types);
        try {
            IType returnType = type.getJavaProject().findType(resolvedElementTypeName);
            if(returnType.getJavadocRange()==null) {
                return null;
            }
            JavadocBrowserInformationControlInput e = getHoverInfo(new IJavaElement[] { returnType}, returnType.getTypeRoot(), hoverRegion,
                    null);
            return e;
        } catch (Exception e) {
            return null;
        }
    }

    public static String firstToLower(String propertyName) {
        char[] c = propertyName.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return String.valueOf(c);
    }
}
/*
 * private static String resolveTypeSignature(IMethod method, String typeSignature) throws JavaModelException {
        int count = Signature.getArrayCount(typeSignature);
        String elementTypeSignature = Signature.getElementType(typeSignature);
        if (elementTypeSignature.length() == 1) {
            // no need to resolve primitive types
            return typeSignature;
        }
        String elementTypeName = Signature.toString(elementTypeSignature);
        IType type = method.getDeclaringType();
        String[][] resolvedElementTypeNames = type.resolveType(elementTypeName);
        if (resolvedElementTypeNames == null || resolvedElementTypeNames.length != 1) {
            // check if type parameter
            ITypeParameter typeParameter = method.getTypeParameter(elementTypeName);
            if (!typeParameter.exists()) {
                typeParameter = type.getTypeParameter(elementTypeName);
            }
            if (typeParameter.exists()) {
                String[] bounds = typeParameter.getBounds();
                if (bounds.length == 0) {
                    return "Ljava/lang/Object;"; //$NON-NLS-1$
                }
                String bound = Signature.createTypeSignature(bounds[0], false);
                return Signature.createArraySignature(resolveTypeSignature(method, bound), count);
            }
            // the type name cannot be resolved
            return null;
        }

        String[] types = resolvedElementTypeNames[0];
        types[1] = types[1].replace('.', '$');

        String resolvedElementTypeName = Signature.toQualifiedName(types);
        String resolvedElementTypeSignature = "";
        if(types[0].equals("")) {
            resolvedElementTypeName = resolvedElementTypeName.substring(1);
            resolvedElementTypeSignature = Signature.createTypeSignature(resolvedElementTypeName, true);
        }
        else {
            resolvedElementTypeSignature = Signature.createTypeSignature(resolvedElementTypeName, true).replace('.', '/');
        }

        return Signature.createArraySignature(resolvedElementTypeSignature, count);
    }
*/