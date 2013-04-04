TODOs
-----

- Functional
	- keyboard accelerators for all ui controls. TBD: find out what NV has for the controls
		- [FIXED] shift tab from note contents should switch to note list.
		- [WONTFIX] down arrow from note title should switch to note list. WONTFIX BCOS tab is sufficient for now.
	- on start, the following is true:
		- [FIXED] focus is on note name textfield
		- [FIXED] list has all notes, 
			- [UNFIXED] first note is selected. UNFIXED BCOS found this to be not the best usability.
		- [FIXED] selected note's contents are shown in text area
	- on change of text field (enter for now):
		- [FIXED] list has notes that match, 
			- [FIXED] focus is on list's first row
		- [FIXED] that row's contents are shown
		- [FIXED] if no matches, a new note is created with the search text as title and focus is on text area for input of note contents.
	- implement incremental search based on contents of text field
	- [FIXED] selecting a row in the list should set that as the note title as well. 
	- [FIXED] keyboard scrolling of list's rows should change the contents view instantly

	- [FIXED] search results still displays wierd - text is centered and gray bkgnd shows thru
	- [FIXED] cannot run as executable
	- [NOMINALLY FIXED] save doesnt work correctly; not all text saved.
	- [NOMINALLY FIXED] add credits, link to original. NOMINALLY FIXED by adding an "About" note.
	- add last modified date/time stamp to each note
		- convert list to a table and show dtstamp in a column.
	- add ability to delete note
- Technical
	- [FIXED] sphagetti code; needs reorg within the groovy file and possibly move to multiple files
	- change autosave logic to be time-based instead of event-based as it is currently.
	- [FIXED] save on exit as well
	- [FIXED] remove duplication in save on exit/ incremental save. FIXED as part of issue #6.
	- see if there's a more groovy way of doing setNoteContent()
	- move to a pure java+swing solution: reduce size of binary drastically.
	- [FIXED] remove debug prints
- bugs
	1. [FIXED] on start, the note contents are loaded, but the note title is not. this results in an unnamed note with the contents of the first actual note. FIXED by not setting focus to the list after a search. user has to manually select items, upon which their contents will be shown in the textarea. The problem was that the search automatically set focus to the first item without changing the note title. I was going to change the code such that the note title is also updated after the search, but thought better of it because that would remove the search term of the **search that just happened**. Instead, the user can now browse through the search results and if none of them are satisfying, shift-tab over and and search again, or tab over and fill in content with the search term as note name.
	2. [FIXED] list not blank when no search results found
	3. [FIXED] enter a search term that results in no match and close the app. now there's a new note with the search term as title and the first note's contents as its contents. FIXED by solution to #2. 
	4. [FIXED] start up, enter a search term with no match. search term disappears after search finishes.
	5. [FIXED] start up, enter a search term with no match, then close app. now there's a note with the search term and blank contents. FIXED BY: quite a few changes, actually. Before, if the search didnt return a match, a new note with null contents was created automatically. This required the Note class and the Notes.add(Note) method. setNote did essentially the same thing, so i removed the Note class, removed its use in handling the empty search results and moved the whole "create new note" functionality to the save bits. if the title and contents are non-blank, a new note will be saved.
	6. [FIXED] start a new note, type in some text (< 20 chars) and shift-tab out. note is not saved. FIXED BY adding autosave logic to shift-tab handler as well. Used this as the time to refactor out the incremental save logic as this was to be the third place the same logic would be copy-pasted into. Finally refactored AutoSaver as well and added a new method saveIncremental() to JNVLauncher.
	7. [FIXED] on startup, all existing notes are listed and the contents of the first note are shown in the text area. Nothing should be selected or displayed except the list of notes itself. When the user navigates to the list and selects an item, *then* its contents should be displayed. FIXED by removing selectedIndex from list's def.
	8. Navigating via mouse defeats most operations: 
		a) autosave is not called when focus moves out of the text area via mouse
		b) note's contents are not blanked out when focus moves to the note title via mouse.
