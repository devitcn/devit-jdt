devit-jdt
=========

patch org.eclipse.jdt to support some feature needed by everybody.

对org.eclipse.jdt做一些寻常开发中用到的补丁。

feature
-----------
1. Extend default spelling engine to support java package name,class name,method name,variable name spell check.

![spelling support camel case](doc/spelling.png "camel case spelling check.")

Different form IDEA word check, the plugin only check **only** check variable declare segment for minimum developer's attention.

**note** Sine spelling checker can not get AST of java file. I have to use three regular expression pattern to match package,class name,method name and variable. So there maybe some performance problem if file is large.

planing
----------

1. code assist to generate map to bean access. for example map.put("props" bean.getProps);
2. code complete sorter to access method unique in a block of code.

License
---------------

[Eclipse Public License - v 1.0](http://www.eclipse.org/legal/epl-v10.html "Eclipse Public License - v 1.0")
