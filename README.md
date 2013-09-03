devit-jdt
=========

org.eclipse.jdt patch to support some feature needed by everybody.

feature
-----------
1. extend default spelling to support java package name,class name,method name,variable name spell check.

**note** Current now this feature just extend org.eclipse.jdt.* internal class, so plugin can not run in strict mode.

**note2** Sine spelling checker can not get AST of java file. I have to use three regular expression pattern to match package,class name,method name and variable. So there maybe some performance problem of large class file.

planing
----------

1. code assist to generate map to bean access. for example map.put("props" bean.getProps);
2. code complete sorter to access method unique in a block of code.
