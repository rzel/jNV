JNV: Java Notational Velocity
=============================

JNV is my java-based Notational Velocity clone. Its both an exercise at creating a NV clone as well as trying Groovy out as a GUI development language.

Goal
----
Create a NV clone in Java/Groovy that:

- can be run from its jar, like so: `java -jar jnv.jar`. The only dependency should be the JRE.
- will create a `notes.json` in the directory its run from, where it will store all notes created in json format.
- look and and feel as well as usability as close as possible to the original, ie:
	- keyboard access to everything
	- flow from top to bottom
	- autosave

Non-goals
---------

- Simplenote integration
- Ability to read original NV files
- Rich text, bullets etc.

Status
-----

- basic ui done
- notes can be created and saved (mostly)
- notes can be searched by substring of name and then edited

TODOs
-----

- Functional
	- keyboard accelerators for all ui controls
	- search results still displays wierd - text is centered and gray bkgnd shows thru
	- cannot run as executable
	- save doesnt work correctly; not all text saved.
- Technical
	- sphagetti code; needs reorg within the groovy file and possibly move to multiple files
	- remove debug prints
	