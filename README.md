JNV: Java Notational Velocity
=============================

JNV is my java-based [Notational Velocity](http://notational.net) clone. I built it so that I'd have a cross-platform note-taking software; having rejected the idea of installing NV on the mac and Resophnotes on Windows. It was also an exercise at trying Groovy out as a GUI development language, but quickly moved past Groovy to produce a lean Java-based version.

Goal
----
Create a NV clone in Java/Groovy that:

- can be run from its jar, like so: `java -jar jnv.jar`. The only dependency should be the JRE.
- will create a notes file in the folder its run from, where it will store all notes created; preferably in json format.
- look and and feel as well as usability as close as possible to the original, ie:
	- keyboard access to everything
	- flow from top to bottom
	- autosave

Future Goals
------------

- tags

Non-goals
---------

- Simplenote integration
- Ability to read original NV files
- Rich text, bullets etc.

Status
-----

### Groovy version

The groovy version was what I started with and is in a pretty good state.

- basic ui done
- notes can be created and saved
- notes can be searched by substring of name and then edited

If you have Groovy installed, you can download `JNV.groovy` and run it like so:
		
		groovy JNV.groovy

Alternatively, you can run it as an independent java jar. To run the jar in `groovy/bin`, download
it to any folder and copy `groovy-all-2.1.0.jar` from `GROOVY_HOME/embeddable` there. Then use the standard java invocation:

	java -jar jnv.jar

Any 2+ version of jar should work as well, but since the jar refers to a specific version in its `MANIFEST.MF`,
you'll have to either rename the jar and hope for the best or build the jar after modifying the
file.

Building the source is easy, though. Assuming you have JDK and Groovy installed, run

		cd jnv/groovy/ ./build.sh

... to create `bin/jnv.jar`.


### Java version

#### Why

While the groovy version was nice, I found myself tending towards a java version for a few reasons:

1. The groovy-all-x.jar adds about 6.7 mb to the app's size. For a simple app like this, that's unacceptable.
2. While developing the groovy version, I had as many tabs with the original Javadocs and Swing tutorials opened as I had tabs with Groovy docs; not to mention quite a few stackoveflow tabs for specific groovy questions that the official docs either didnt have answers to or had hidden them well. This is not just a documentation problem, but a comprehension problem: with Java I have to keep "just" the Java platform in my head; with Groovy I have to keep not just the Groovy platform, but also how it interacts with the underlying Java platform and how that platform works in and of itself.
3. Further, while SwingBuilder is a nice sugar layer over the Swing api's, things like the DocumentListener still had to be written in a Java style because SwingBuilder used reflection to detect apis to expose in sugared form: those that followed the Bean convention were readily available (eg, actionPerformed() is available without the need to create a Listener class) but those that didnt were not (eg, DocumentListeners are added using addDocumentListener(), not setDocumentListener()).
4. Finally, while all code being in one groovy file is nice during the initial stages, it makes it unweildy as the code gets bigger. Yes, I could create separate groovy files, but then I'm yet to get the classpath working on groovy files directly (apparently this is a windows issue); requiring the code to be compiled down to java. Why not then move to native java itself?

#### Status

The Java version has superceded the Groovy one in features. It now sports 2 modes of storage: Java Serialization and Json.The jar is ~15k with Java serialization and ~20k with Json support, meeting the original intent of moving to Java. 

The same set of instructions as above apply for building and running it, except that you run the commands from the `java` folder. 

Json support uses [Stringtree.org's json Reader and Writer](http://www.stringtree.org/stringtree-json.html) in source form.


Credits
-------

1. **Source Idea**: As mentioned above, the original inspiration for jNV is [Notational Velocity](http://notational.net). The implementation, however, uses only the concepts, not the source itself. Since it is open source, however, I might "pick its brains" when/if jNV matures to use the thousands of notes that NV is known to be used for. 
2. **JSON support**: This version uses [Stringtree.org's json Reader and Writer](http://www.stringtree.org/stringtree-json.html) in source form to enable read/write from/to Json. Again, since this is closer to "proof of concept" and way afar from "production ready" code, such a library is apt. If jNV rises to the levels of NV's use, I'll definitely revisit the json support - maybe convert it to an export format similar to how NV allows exporting to text files. At that point, this credit might be moot; but for now this implementation (and its allowance of using the code in source form, not to mention writing self-contained reader and writer code) is a godsend because the alternative would be to make this a two jar distribution or to use something like One-jar or Shade/Assembly. Shudder.Many thanks, therefore, to the original authors of Stringtree json.
