todo.md
=======
Note: The todos were extracted out of journal.md quite late in the Java development, so Groovy todos are in a separate file. The journal itself was started when I realized that I need to start keeping track of my thoughts and actions.

- [x] Switch to java serialization to remove dependence on json and related conversion issues (esp date)
- [x] Modularize storage such that it can switch between different storage formats.
- [x] Create separate json store version
- [-] Add json back as an optional format
- [x] Make the search results display the last modified date as well
- [x] Handle mouse events
- [x] Make UI look proper on maximize.
- [x] Release v 1.0
- [ ] Make the contents rich text
- [ ] Convert the About note into rich text
- [ ] Revert GUI code to the simpler "coding patterns" described at the end of [the GUI essay](docs/On_creating_a_gui.md)
- [ ] Allow "export to json" from main app.

Bugs:

1. Type something like "a" in the text box and hit enter. App shows search results, which *should* be there after some use of the app because the letter a is in almost every text. Now you will not be able to create a note with that title. Or, more generally: a title which is a substring of an existing title will not be allowed currently as a separate title.
