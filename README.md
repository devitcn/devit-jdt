devit-jdt
=========

patch org.eclipse.jdt to support some feature needed by everybody.

对org.eclipse.jdt做一些寻常开发中用到的补丁。

Features
-----------
1. Patch default spelling engine to support check java package ,class ,method ,variable declaration and also support camel case.
![spelling support camel case](doc/spelling.png "camel case spelling check.")

We check:
- package declaration(last segment)
- class, method,parameter name
- any variable

**Note:** We only check declaration part in order to minimum developer's attention.
We ignore:

- method with `@Override`

**note** Sine spelling checker can not get AST of java file. I have to use three regular expression pattern to match package,class name,method name and variable. So there maybe some performance problem if file is large.

2. some usefule code template for java

2.1 newlist,newmap,newset

type `newlist` and hit content assistant key (alt+/ or ctrl+space) and select `newlist` template and code will show:

![newlist template](doc/newlist.png "newlist template")

    //newlist
    List<T> V = new ArrayList<T>(10);
    //newmap
    Map<K, V> map = new HashMap<K, V>(10);
    //newset
    Set<K> set = new HashSet<K>(10);
    //logger
    //create static slf4j logger for class,and automatic import org.slf4j.Logger,org.slf4j.LoggerFactory
    //test
    //create @Test method,and automatic import org.junit.Assert.* ,org.hamcrest.Matchers


Plans
----------

1. code assist to generate map to bean access. for example map.put("props" bean.getProps);
2. code complete sorter to access method unique in a block of code.
3. code iteration when c+a+up down
4. export java bean into jsdt for quick edit.
5. some code template.
6. file link: jump link in service test jsp js and properties. click link on jsp or js jump to controller or js file support expression evaluate.

 WorkbenchActionBuilder
 <menu
               commandId="org.eclipse.ui.navigate.showInQuickMenu"
               id="org.eclipse.ui.ide.markers.showInMenu"
               label="%menu.showIn.label"
               mnemonic="%menu.showIn.mnemonic">
            <dynamic
                  class="org.eclipse.ui.ExtensionFactory:showInContribution"
                  id="org.eclipse.ui.menus.dynamicShowInMenu">
            </dynamic>
         </menu>

7. copy comment from filed,or sync edit comment on getter setter field.
org.eclipse.jdt.ui.CodeGeneration.getSetterComment(ICompilationUnit, String, String, String, String, String, String, String)
AddGetterSetterAction
8. run/debug last launched

License
---------------

[Eclipse Public License - v 1.0](http://www.eclipse.org/legal/epl-v10.html "Eclipse Public License - v 1.0")
