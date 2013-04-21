JNV journal
===========
Tue Apr 16 10:28:46 2013 : Basic version working both in Java and Groovy. Current work is focused on the Java version because its the smaller of the two. Todos as of date:

- [x] Switch to java serialization to remove dependence on json and related conversion issues (esp date)
- [x] Modularize storage such that it can switch between different storage formats.
- [x] Create separate json store version
- [-] Add json back as an optional format
- [x] Make the search results display the last modified date as well
- [] Handle mouse events
- [] Make the contents rich text
- [] Convert the About note into rich text
- [] Release v 1.0
- [] Revert GUI code to the simpler "coding patterns" described at the end of [the GUI essay](docs/On_creating_a_gui.md)
- [] Allow "export to json" from main app.
 
Wed Apr 17 06:53:39 2013 : In a bit of a quandry getting this modularized storage working.I've done the work required to externalize storage and written JsonStore and SerializedStore, but I'd like to do the following:

- jnv.jar has only java serialization as storage
- jnv_json.jar has only json storage
- jnv_all.jar has both options and somehow they can be switched between

its the somehow part that's still in flux. to make the first two work itself i'd have to make JNV an abstract class, with an abstract getStore() method, which specific subclasses would then override with appopriately returned store objects. But this means main needs to change as well, so maybe the current main's body should be put into JNV itself so it too can be internalized? dont know. seems like a lot for too less a change. I'm toying with just using token substitution on JNV.java to replace JsonStore with SerializedStore during compile, but that wouldnt handle the jnv_all case, nor would it be portable across OSs. Not that build.sh is currently portable, but its sufficiently simple to replace with appropriate commands in Windows, for example because the commands are still standard java commands. But adding sed to the mix will definitely cross the line. I'll probably make JNV abstract and create 3 subclasses: JNV_All, JNV_Ser and JNV_Json. That'll keep the build steps simple as well.

Wed Apr 17 08:19:03 2013 : On second thoughts, jnv_all will need *some* user interface to trigger the switch between supported formats. until such time as the ui (or command line) is modified to have such an option, I'll table the ability to switch between formats.

Thu Apr 18 17:50:05 2013 : todos:

- remove blank rows from table
- [x] make the table read-only
- [x] show actual dates

Fri Apr 19 17:50:58 2013 : in moving to the table, I reintroduced an issue from the groovy days: start fresh, select the about note in the list, then go back to the text box, type a new title. now the list doesnt clear nor are the about note's contents cleared. closing the app now saves a new note with the new title and the about note's contents. fix required:

- [x] move setNoteContent() out of the the if(SEARCHING)... block
- [-] implement a true removeAll similar to the JList's removeall. Right now getting by with setItemCount(0).
	May not be needed. setItemCount seems to work. we'll see.

Fri Apr 19 18:03:55 2013 : Found an issue thats likely in the groovy version as well. Type something like "a" in the text box and hit enter. App shows search results, which *should* be there after some use of the app because the letter a is in almost every text. Now you will not be able to create a note with that title. Or, more generally: a title which is a substring of an existing title will not be allowed currently as a separate title.

Fri Apr 19 18:09:28 2013 : table specific issue: shift tab out of table not working. More precisely, JList did not capture shift tab, so it naturally went to the previous component. JTable might need special handling. Fixed this by copying over the key listener from noteContents minus the autosave. seems to work fine.

Fri Apr 19 18:19:13 2013 : Found another issue that is a consequence of the "hit enter to search" simplification: scrolling up and down the search list updates the title. shift tab to the title box leaves the last browsed title as-is. must at least select all text so it can be replaced easily.

Fri Apr 19 18:34:37 2013 : code in branch show_lastModified looks good to be merged.

Fri Apr 19 22:43:52 2013 : After reading up a bit, it seems like making mouse work well with jNV really doesnt need handling the mouse; instead it needs focus listeners to be written. Specifically:

- [x] when the text area loses focus due to a mouse click elsewhere, it should save the content again.
- [x] when the text box gains focus due to a mouse click, it should select all text.

Sat Apr 20 08:13:11 2013 : Spent a lot of time meandering for a solution to keep the title textbox constrained in its height through window resizes. This involved writing code like:

		window.addComponentListener(new ComponentAdapter(){
			public void componentResized(ComponentEvent e){
				Dimension wd = window.getSize();
				APPWIDTH = (int) wd.getWidth();
				APPHEIGHT = (int) wd.getHeight();
				// calculateContolHeights();

				noteName.setPreferredSize(new Dimension(APPWIDTH,NOTENAMEHEIGHT));
				// foundNotes.setSize(new Dimension(APPWIDTH,NOTESLISTHEIGHT));
				// noteContent.setSize(new Dimension(APPWIDTH,NOTECONTENTHEIGHT));
			}
		});

.. finally the solution turned out to be to set the maximum size to intmax while constraining the height to what i'd wanted. apparently this is not a Swing best practice. But after going through all the layout managers (especially SpringLayout) I decided that solving this the best way in Swing is an exercise for a later date. Moving on after setting max size on the textbox, therefore :)

Sat Apr 20 08:32:34 2013 : I'm starting a new tradition of adding all the browser tabs that were open at the time of coding. This is a poor substitute of saving the whole session itself, but imo it gives an idea of the context surrounding development.

Sun Apr 21 09:13:59 2013 : Purging out the jar, ser and json files. Reasons:

1. Binaries dont belong in repos in general. I'd originally kept them in for ease of distribution. jNV is an individual itch being scratched; the only person I expect to "download and play" is me from my other machine.
2. Even the purpose of sharing with myself didnt quite work: my other machine's jdk version was different. So source version it is.

In prep for this, added readmes to the /bin and /classes directories. Now trying instructions from [Github on purging files](https://help.github.com/articles/remove-sensitive-data). For reason this didnt change anything when I tried to delete the notes.ser file. I'm going to include the files in the ignore file for now.

