package cn.devit.jdt.hover;

import java.io.IOException;

import org.eclipse.jdt.core.*;
import org.eclipse.jdt.internal.core.manipulation.util.Strings;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.corext.util.MethodOverrideTester;
import org.eclipse.jdt.internal.corext.util.SuperTypeHierarchyCache;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.text.JavaCodeReader;
import org.eclipse.jdt.internal.ui.text.java.hover.JavadocBrowserInformationControlInput;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
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
        /**
         * 返回的可以是字符串，也可以是JavadocBrowserInformationControlInput#getHtml();
         */
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

                        if(((IMethod) member).isConstructor()) {
                            if(((IMethod) member).getDeclaringType().getJavadocRange()!=null) {
                                Object e = getHoverInfo(new IJavaElement[]{((IMethod) member).getDeclaringType()}, 
                                        ((IMethod)member).getTypeRoot(), hoverRegion,
                                        null);
                                return e;
                            }
                        }
                        
                        //显示字段的注解
                        boolean isGetter = name.startsWith("get") //$NON-NLS-1$
                                && name.length() > 3;
                        boolean isBooleanGetter = name.startsWith("is") //$NON-NLS-1$
                                && name.length() > 2;
                        boolean isSetter = name.startsWith("set") //$NON-NLS-1$
                                && name.length() > 3;
                                
                        if (isGetter || isBooleanGetter || isSetter && ((IMethod) member).getDeclaringType().isClass()) {
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
                                    return useJavaDocFromReturnType(hoverRegion, member,
                                            type);
                                }
                                return null;
                            }
                            elements = new IJavaElement[] { field };
                            
//                            JavadocBrowserInformationControlInput e = getHoverInfo(elements, getEditorInputJavaElement(), hoverRegion,
//                                    null);
                            //if the type is JPA entity class
                            //则显示field 字段的文档
                            //IAnnotation annotation = field.getDeclaringType().getAnnotation("entity");
                            Object e = getHoverInfo(elements, field.getTypeRoot(), hoverRegion,
                                    null);
                            
                            if(isSetter) {
                                //TODO try to attach small uncommon setter source code.
                            }
                            return e;
                        }
                        
                        //如果覆盖了方法，上一层的方法上有注解，则显示他们
                        if(canInheritJavadoc(member)) {
                            //String content= JavadocContentAccess2.getHTMLContent(element, true);
                            JavadocBrowserInformationControlInput html =  getHoverInfo(elements, getEditorInputJavaElement(), hoverRegion, null);
                            String stringHtml  = html.getHtml();
                            StringBuilder buffer= new StringBuilder();
                            IMethod declaration = findMethodDeclaration(member);
                            if(declaration!=null) {
                                addAnnotations(buffer, declaration, declaration.getTypeRoot(), hoverRegion);
                                stringHtml = stringHtml.replace("</body></html>", buffer.toString()+"</body></html>");
                            }
                            return new JavadocBrowserInformationControlInput((JavadocBrowserInformationControlInput) html.getPrevious(), member, stringHtml, 0);
                        }

                    }
                } catch (JavaModelException e) {
                    //I don't care
                }
            }
        }
        return null;
    }

    private IMethod findMethodDeclaration(IJavaElement member) throws JavaModelException {
        IMethod method = (IMethod) member;
        IType classType = method.getDeclaringType();
        ITypeHierarchy fTypeHierarchy = SuperTypeHierarchyCache.getTypeHierarchy(classType);
        final MethodOverrideTester tester= SuperTypeHierarchyCache.getMethodOverrideTester(classType);
        //TODO 取巧了，只取得上一级的interface就够了。
        IType[] allInterface = fTypeHierarchy.getAllSuperInterfaces(classType);
        for (IType iType : allInterface) {
            IMethod overridden= tester.findOverriddenMethodInType(iType, method);
            if(overridden !=null) {
                //gocha
                return overridden;
            }
        }
        return method;
    }

    /**
     * Getter 没有注解的话，优先使用返回类型的注释。
     * 
     * @param hoverRegion
     * @param member
     * @param type
     * @return
     * @throws JavaModelException
     */
    Object useJavaDocFromReturnType(IRegion hoverRegion, IJavaElement member,
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

    /**
     * TODO 如果set get 不简单，则显示源码。
     * 
     * @param member
     * @return
     */
    String tryAttachUnCommonSetterSourceCode(IJavaElement member){
        if ((member instanceof IMember || member instanceof ILocalVariable || member instanceof ITypeParameter) && member instanceof ISourceReference) {
            try {
                String source= ((ISourceReference) member).getSource();

                String[] sourceLines= getTrimmedSource(source, member);
                if (sourceLines == null)
                    return null;

                String delim= StubUtility.getLineDelimiterUsed(member);
                source= Strings.concatenate(sourceLines, delim);

                return source;
            } catch (JavaModelException ex) {
                //do nothing
            }
        }
        return null;
    }
    
    public static String firstToLower(String propertyName) {
        char[] c = propertyName.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return String.valueOf(c);
    }
    
    /**
     * COPY from  org.eclipse.jdt.internal.ui.text.javadoc.JavadocContentAccess2.canInheritJavadoc(IMember)
     */
    private static boolean canInheritJavadoc(IJavaElement member) {
        if (member instanceof IMethod && member.getJavaProject().exists()) {
            /*
             * Exists test catches ExternalJavaProject, in which case no hierarchy can be built.
             */
            try {
                return ! ((IMethod) member).isConstructor();
            } catch (JavaModelException e) {
                JavaPlugin.log(e);
            }
        }
        return false;
    }
    
    private String[] getTrimmedSource(String source, IJavaElement javaElement) {
        if (source == null)
            return null;
        source= removeLeadingComments(source);
        String[] sourceLines= Strings.convertIntoLines(source);
        Strings.trimIndentation(sourceLines, javaElement.getJavaProject());
        return sourceLines;
    }

    private String removeLeadingComments(String source) {
        final JavaCodeReader reader= new JavaCodeReader();
        IDocument document= new Document(source);
        int i;
        try {
            reader.configureForwardReader(document, 0, document.getLength(), true, false);
            int c= reader.read();
            while (c != -1 && (c == '\r' || c == '\n')) {
                c= reader.read();
            }
            i= reader.getOffset();
            reader.close();
        } catch (IOException ex) {
            i= 0;
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                JavaPlugin.log(ex);
            }
        }

        if (i < 0)
            return source;
        return source.substring(i);
    }

}