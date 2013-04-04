JNV: Java Notational Velocity
=============================

JNV is my java-based [Notational Velocity](http://notational.net clone). Its both an exercise at creating a NV clone as well as trying Groovy out as a GUI development language.

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

While the groovy version is nice, I found myself tending towards a java version for a few reasons:

1. The groovy-all-x.jar adds about 6.7 mb to the app's size. For a simple app like this, that's unacceptable.
2. While developing the groovy version, I had as many tabs with the original Javadocs and Swing tutorials opened as I had tabs with Groovy docs; not to mention quite a few stackoveflow tabs for specific groovy questions that the official docs either didnt have answers to or had hidden them well. This is not just a documentation problem, but a comprehension problem: with Java I have to keep "just" the Java platform in my head; with Groovy I have to keep not just the Groovy platform, but also how it interacts with the underlying Java platform and how that platform works in and of itself.

#### Status

The Java version is being designed. It will be a source port of the Groovy version for the most part, with some changes described in the implementation notes.
