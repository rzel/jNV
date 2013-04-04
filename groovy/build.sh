if groovyc -d classes JNV.groovy; then
	jar cvfm bin/jnv.jar MANIFEST.MF -C classes/ .
fi
