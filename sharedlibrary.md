# cicdjenkins

## documentation of pipeline syntax
https://jenkins.io/doc/book/pipeline/shared-libraries/

## example of using shared library in declarative pipeline
https://jenkins.io/blog/2017/02/15/declarative-notifications/

For Arlo, we use declarative pipeline. Scripted pipeline will NOT be used.

## to use the shared pipeline sitting in the same repository:
https://stackoverflow.com/questions/46213913/load-jenkins-pipeline-shared-library-from-same-repository/49112612#49112612


## directory structure
```
(root)
+- src                     # Groovy source files
|   +- org
|       +- foo
|           +- Bar.groovy  # for org.foo.Bar class
+- vars
|   +- foo.groovy          # for global 'foo' variable
|   +- foo.txt             # help for 'foo' variable
+- resources               # resource files (external libraries only)
|   +- org
|       +- foo
|           +- bar.json    # static helper data for org.foo.Bar
```
#webhook trigger
