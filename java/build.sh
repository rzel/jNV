# build.sh

# This build file started off as a simple, 2 line script that compiled all the classes and jar-ed them up.
# To build two separate jars and keep them tight (ie, with no extra files) required the script to grow.
# Since the intent of jNV as a project is to produce an app that works anywhere a JRE is available, 
# it seemed disigenuous to have its source depend on bash. If the jar command had an option to exclude specific
# classes, I would have used it, but it doesnt. Instead of using sed or similar unix tools to exclude files, this 
# script copies the classes over to working directories and uses standard file delete commands to arrive at
# the directory structure that can be fed to jar readily. So these commands could be easily replaced by their DOS 
# batch commands equivalents, for example. Alternativey, you can use MSYS-Git (like I do) on Windows and run the
# bash scripts as-is.

rm -rf classes/*

echo compiling...
if javac -Xlint:deprecation -Xlint:unchecked -d classes -cp classes src/org/stringtree/json/*.java src/org/vinodkd/jnv/*.java; then

	echo building jnv_json.jar
	echo "===================="
	# clear work dir, taking care not to delete the manifest file.
	rm -rf jar/jnv_json/*class

	# copy new files
	cp -R classes/* jar/jnv_json

	# remove files not required for this jar
	rm jar/jnv_json/org/vinodkd/jnv/JNV.class
	rm jar/jnv_json/org/vinodkd/jnv/SerializedStore.class

	# build the jar
	jar cvfm bin/jnv_json.jar jar/jnv_json/MANIFEST.MF -C jar/jnv_json/ . res

	echo building jnv.jar
	echo "==============="
	# clear work dir, taking care not to delete the manifest file.
	rm -rf jar/jnv/*class

	# copy new files
	cp -R classes/* jar/jnv

	# remove files not required for this jar
	rm -rf jar/jnv/org/stringtree
	rm jar/jnv/org/vinodkd/jnv/*Json*.class

	# build the jar
	jar cvfm bin/jnv.jar jar/jnv/MANIFEST.MF -C jar/jnv/ . res

fi


