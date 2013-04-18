JNV journal
===========
Tue Apr 16 10:28:46 2013 : Basic version working both in Java and Groovy. Current work is focused on the Java version because its the smaller of the two. Todos as of date:

[x] Switch to java serialization to remove dependence on json and related conversion issues (esp date)
[x] Modularize storage such that it can switch between different storage formats.
[x] Create separate json store version
[-] Add json back as an optional format
[x] Make the search results display the last modified date as well
[] Make the contents rich text
[] Convert the About note into rich text
[] Release v 1.0
[] Revert GUI code to the simpler "coding patterns" described at the end of [the GUI essay](./On \creating \a \gui.md)
[] Allow "export to json" from main app.
 
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
