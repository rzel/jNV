# build.sh
rm -rf classes/*

echo building jnv_json.jar
if javac -Xlint:deprecation -Xlint:unchecked -d classes -cp classes src/org/stringtree/json/*.java src/org/vinodkd/jnv/*.java; then
	jar cvfm bin/jnv_json.jar MANIFEST_JSON.MF -C classes/ . res
fi


