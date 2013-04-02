groovyc -d classes JNV.groovy
jar cvfm bin/jnv.jar MANIFEST.MF -C classes/ .