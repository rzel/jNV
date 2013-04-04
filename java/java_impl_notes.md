create model:
	note:
		- title
		- lastModified
		- contents
	notes:
		- map of notes
		- load from file
		- save to file

create gui:
	note title/search bar: fixed height
	note list: scrollable
	note contents: scrollable
default view:
	blank note title, focused
	note list populated with all existing notes
	blank note contents

add states:
	first startup -> load default notes -> waiting
	startup -> load existing notes (if exists) -> waiting
	startup -> waiting
	waiting -> searching
	waiting -> browsing list
	waiting -> view note contents (using mouse)
	searching --> browsing list
	searching --> new note entry
	browsing list --> view note contents
	new note entry --> edit contents
	view note contents --> edit contents
	edit contents --> browsing list
	edit contents --> waiting
	edit contents --> searching

add behaviors:
	to note title:
		text in note title + enter => note list has filtered note titles that match that text. if no match, note list is empty
		tab from note title => 1st item in note list selected.
	to note list:
		up/down arrow in list => item selected and:
			its contents shown in note contents
			note title changes to the resp. title
		list contents changed by search should not change the note title, only user action should.
	to note contents:
		normal editing actions: should work as expected
		shift-tab => focus switches back to note list

	to app:
		save all notes every x secs
		save all notes before exit

