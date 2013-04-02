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

- basic ui done
- notes can be created and saved (mostly)
- notes can be searched by substring of name and then edited

TODOs
-----

- Functional
	- keyboard accelerators for all ui controls. TBD: find out what NV has for the controls
		- [FIXED] shift tab from note contents should switch to note list.
	- on start, the following is true:
		- [FIXED] focus is on note name textfield
		- [FIXED] list has all notes, 
			- [FIXED] first note is selected.
		- [FIXED] selected note's contents are shown in text area
	- on change of text field (enter for now, autocomplete later):
		- [FIXED] list has notes that match, 
			- [FIXED] focus is on list's first row
		- [FIXED] that row's contents are shown
		- [FIXED] if no matches, a new note is created with the search text as title and focus is on text area for input of note contents.
	- [FIXED] selecting a row in the list should set that as the note title as well. 
	- [FIXED] keyboard scrolling of list's rows should change the contents view instantly

	- [FIXED] search results still displays wierd - text is centered and gray bkgnd shows thru
	- [FIXED] cannot run as executable
	- [NOMINALLY FIXED] save doesnt work correctly; not all text saved.
	- save doesnt work correctly; not all text saved.
- Technical
	- [FIXED] sphagetti code; needs reorg within the groovy file and possibly move to multiple files
	- change autosave logic to be time-based instead of event-based as it is currently.
	- [FIXED] save on exit as well
	- remove duplication in save on exit/ incremental save
	- save on exit as well
	- see if there's a more groovy way of doing setNoteContent()
	- remove debug prints
