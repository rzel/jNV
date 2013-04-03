import groovy.swing.SwingBuilder
import javax.swing.*
import javax.swing.event.*
import javax.swing.text.*
import java.awt.*
import java.awt.event.*

import groovy.json.*

class JNVLauncher{
    static main(args) {
        def NOTES = new Notes(System.getProperty("user.dir"))
        def JNVLauncher launcher = new JNVLauncher(NOTES)
        launcher.createAndShowUI()        
    }

    private NOTES
    private swing
    private listModel = new DefaultListModel()
    private SEARCHING = false

    JNVLauncher(notes){
        NOTES = notes
        NOTES.getNotes().each { k,v ->
            listModel.addElement(k)
        }
    }

    def createAndShowUI(){
        swing = new SwingBuilder()

        // TODO: add close hook so that unsaved data is saved before close
        def ui = swing.frame(
                    title           :'jNV', defaultCloseOperation:JFrame.EXIT_ON_CLOSE, 
                    pack            :true, 
                    show            :true,
                    preferredSize   : [520,600],
                    windowClosing   : {
                        saveIncremental()
                    }
        ) 
        {
            vbox {
                    textField(  id              : 'noteName',
                                preferredSize   : [500,20],
                                actionPerformed : { 
                                    SEARCHING =  true
                                    def nName = swing.noteName.getText()
                                    def searchResult = NOTES.findNotes(nName)
                                    //println(searchResult)
                                    // clear out list's model first regardless of search outcome.
                                    swing.foundNotes.model.removeAllElements()

                                    if(!searchResult){
                                        swing.noteContent.requestFocus()
                                    }
                                    else{
                                        searchResult.each { k,v ->
                                            swing.foundNotes.model.addElement k
                                        }
                                        //swing.foundNotes.selectedIndex = 0
                                    }
                                    SEARCHING = false
                                }
                    )
                    scrollPane(preferredSize: [500,150]){
                        list(   id                  : 'foundNotes',
                                layoutOrientation   : JList.VERTICAL,
                                background          : Color.WHITE,
                                model               : listModel,
                                selectedIndex       : 0,
                                valueChanged        : {
                                    // when still in search mode, this event is triggered by elemnts being added/removed
                                    // from the model. the title should not updated then.
                                    if(!SEARCHING){
                                        // set the note title to the selected value
                                        swing.noteName.text = swing.foundNotes.selectedValue
                                    }
                                    // now set the content to reflect the selection as well
                                    setNoteContent()
                                }
                        )
                    }
                    scrollPane(
                        preferredSize:[500,400]    //, 
                        //horizontalScrollBarPolicy: JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED,
                        //verticalScrollBarPolicy: JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
                        ){
                        // removed size to make it scrollable. Eureka link: http://stackoverflow.com/a/9624094
                        textArea(
                                id              : 'noteContent',
                                lineWrap        : true,
                                wrapStyleWord   : true,
                                // this is from http://stackoverflow.com/a/5043957's 'Use a keylistener' solution
                                keyPressed      : { e ->
                                    if (e.getKeyCode() == KeyEvent.VK_TAB &&  e.isShiftDown()){
                                        e.consume()
                                        // fix for issue #6
                                        saveIncremental()
                                        KeyboardFocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent()
                                    }
                                } 
                            )
                        //noteContent.addDocumentListener(new MyDocumentListener())
                    }
            }
        }
        // TODO: the params here are total hacks. fix this.
        swing.noteContent.document.addDocumentListener(new AutoSaver(swing, NOTES))
        setNoteContent()

        ui.show()
    }

    def setNoteContent(){
        def selectedNote = swing.foundNotes.selectedValue
        swing.noteContent.selectAll()
        swing.noteContent.replaceSelection(NOTES.getNote(selectedNote))
    }

    def synchronized saveIncremental(){
        def doc = swing.noteContent.document
        def text = doc.getText(doc.startPosition.offset, doc.length)
        if(swing.noteName.text != "" && text != ""){
            NOTES.setNote(swing.noteName.text, text)
        }
        NOTES.save()
    }
}

// TODO: MAKE THE AUTOSAVE ACTUALLY SANE. RIGHT NOW ALL IT DOES IS SAVE THE WHOLE CONTENT AGAIN AND AGAIN EVERY 20 EVENTS
class AutoSaver implements DocumentListener {

    private static int COUNT = 0;
    private static final int SAVE_AT = 20;     // save at 20 events
    def jnv
    
    public AutoSaver(jnv){
        this.jnv = jnv
    }
    public void insertUpdate(DocumentEvent e) {
        saveIfRequired(e);
    }
    public void removeUpdate(DocumentEvent e) {
        saveIfRequired(e);
    }
    public void changedUpdate(DocumentEvent e) {
        //Plain text components do not fire these events
    }
    //TODO: do both saveIfRequired()s need to be synchronized?
    public synchronized saveIfRequired(e){
        //println "saveifeqd: $COUNT"
        if(COUNT == SAVE_AT){
            jnv.saveIncremental()
            COUNT = 0
            //println "saved notecontent:$text"
        }
        COUNT++;
    }
}


class Notes{
    private allNotes = [:]
    private saveLoc
    private saveFile
    private FILENAME = "notes.json"
    
    Notes(saveLoc){
        //println "saved at: $saveLoc"
        this.saveLoc = saveLoc
        saveFile = new File(saveLoc + System.getProperty("file.separator") + FILENAME)
        if(saveFile.exists()){
            def notesSlurper = new JsonSlurper()
            allNotes = notesSlurper.parse(new FileReader(saveFile))            
        }
        else{
            saveFile.createNewFile()
            setNote("About", """
    This is a clone of Notational Velocity written in Java/Groovy.
    Please see http://notational.net for the original App.

    The text below is abridged from the NV website and provided as an overview for users not familiar with the original NV.

    NOTATIONAL VELOCITY is an application that stores and retrieves notes.
    UNIQUE CHARACTERISTICS
    ======================
        - Modeless Operation : Searching for notes is not a separate action; rather, it is the primary interface.
        - [TBD in jNV] Incremental Search : Searching encompasses all notes' content and occurs instantly with each key pressed.
        - [Non-goal in jNV] Transparent Database Encryption : All content is compressed and encrypted (enabled optionally) before it is recorded to disk.
        - Mouseless Interaction : Notational Velocity's window was designed for keyboard input above all else, and thus has no buttons.
        - Data Instead of Documents : There is no manual "saving" in Notational Velocity; all modifications take effect immediately. 

    HOW IT WORKS
    ============
    The UI has 3 areas: The search/title text field, the filtered note list and the content of the selected note.

    The search/title text field is used both for creating notes and searching. i.e., in the process of entering the title for a new note, related notes appear below, letting users file information there if they choose. Likewise, if a search reveals nothing, one need simply press return to create a note with the appropriate title.

    [TBD in jNV] If a note's title starts with the search term(s), that title will be "auto-completed". This selects the note and consequently displays it. Correspondingly, selecting a note places its title in the search area (De-selecting the note restores the search terms).

    To create a new note, just type its title and press return. Edit the note as needed in the bottom pane.

    To view or edit an existing note, type one or more words contained in its body [TBD in jNV] or title. Reveal a note's content by using the up/down arrow keys to select it.

    To make good use of NV, try to maintain one detail/fact/item per note. Notational Velocity's strength, note-filtering, is diminished when only a few notes contain most of the content in the database.        
                """)
        }
    }

    def getNote(title){
        allNotes[title]
    }
    
    // TODO: replace with iterator
    def getNotes() { allNotes }

    def setNote(title, contents){
        allNotes[title] = contents
    }
    
    def findNotes(searchString){
        allNotes.findAll{ k, v ->
            //println "key: $k, contains? ${k.contains(searchString)}"
            k.contains(searchString)
        }
    }
    
    def save(){
        // creates it if it doesnt exist
        // println "notes.save called"
        saveFile.write(JsonOutput.prettyPrint(JsonOutput.toJson(allNotes)))
    }
}
