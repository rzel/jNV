# build.sh
if javac -Xlint:deprecation -Xlint:unchecked -d classes src/org/vinodkd/jnv/*.java; then
	jar cvfm bin/jnv.jar MANIFEST.MF -C classes/ . res
fi


