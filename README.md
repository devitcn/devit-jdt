devit-jdt
=========

org.eclipse.jdt patch to support some feature needed by everybody.

feature
-----------
1. extend default splleing to support java packge name,class name,method name,variable name splle check.

*note* Current now this feature just extend org.eclipse.jdt.* internal class, so plugin can not run in strict mode.


planing
----------

1. code assits to generate map to bean accessor. eg. map.put("props" bean.getProps);
2. code complete sorter to access method uqniue in a block of code.
