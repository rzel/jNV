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
                        //TODO: remove cut paste from autosaver
                        def doc = swing.noteContent.document
                        def text = doc.getText(doc.startPosition.offset, doc.length)
                        NOTES.setNote(swing.noteName.text, text)
                        NOTES.save()
                    }
        ) 
        {
            vbox {
                    textField(  id              : 'noteName',
                                preferredSize   : [500,20],
                                actionPerformed : { 
                                    def nName = swing.noteName.getText()
                                    def searchResult = NOTES.findNotes(nName)
                                    //println(searchResult)
                                    // clear out list's model first regardless of search outcome.
                                    swing.foundNotes.model.removeAllElements()

                                    if(!searchResult){
                                        NOTES.add(new Note(nName))
                                        swing.noteContent.requestFocus()
                                    }
                                    else{
                                        searchResult.each { k,v ->
                                            swing.foundNotes.model.addElement k
                                        }
                                        //swing.foundNotes.selectedIndex = 0
                                    }
                                }
                    )
                    scrollPane(preferredSize: [500,150]){
                        list(   id                  : 'foundNotes',
                                layoutOrientation   : JList.VERTICAL,
                                background          : Color.WHITE,
                                model               : listModel,
                                selectedIndex       : 0,
                                valueChanged        : {
                                    // set the note title to the selected value
                                    swing.noteName.text = swing.foundNotes.selectedValue
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
                                id          : 'noteContent',
                                // this is from http://stackoverflow.com/a/5043957's 'Use a keylistener' solution
                                keyPressed  : { e ->
                                    if (e.getKeyCode() == KeyEvent.VK_TAB &&  e.isShiftDown()){
                                        e.consume();
                                        KeyboardFocusManager.
                                            getCurrentKeyboardFocusManager().focusPreviousComponent();
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
}

// TODO: MAKE THE AUTOSAVE ACTUALLY SANE. RIGHT NOW ALL IT DOES IS SAVE THE WHOLE CONTENT AGAIN AND AGAIN EVERY 20 EVENTS
class AutoSaver implements DocumentListener {

    private static int COUNT = 0;
    private static final int SAVE_AT = 20;     // save at 20 events
    def swing
    def notes
    
    public AutoSaver(swing, notes){
        this.swing = swing
        this.notes = notes
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
    public synchronized saveIfRequired(e){
        //println "saveifeqd: $COUNT"
        if(COUNT == SAVE_AT){
            def doc = swing.noteContent.document
            def text = doc.getText(doc.startPosition.offset, doc.length)
            notes.setNote(swing.noteName.text, text)
            notes.save();
            COUNT = 0
            //println "saved notecontent:$text"
        }
        COUNT++;
    }
}


class Note{
    def title
    def contents
    
    Note(title) {this.title=title}
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
        }
    }
    def add(note){
        allNotes[note.title] = note.contents
        //println "added note: ${note.    title} with contents: ${note.contents}"
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
