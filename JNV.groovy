// was figuring out how to get the list to display contents of note when an item is selected
import groovy.swing.SwingBuilder
import javax.swing.*
import javax.swing.event.*
import javax.swing.text.*
import java.awt.*

import groovy.json.*

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
        println "saved at: $saveLoc"
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
        println "added note: ${note.    title} with contents: ${note.contents}"
    }
    
    def getNote(title){
        allNotes[title]
    }
    
    def setNote(title, contents){
        allNotes[title] = contents
    }
    
    def findNotes(searchString){
        allNotes.findAll{ k, v ->
            println "key: $k, contains? ${k.contains(searchString)}"
            k.contains(searchString)
        }
    }
    
    def save(){
        // creates it if it doesnt exist
        println "notes.save called"
        saveFile.write(JsonOutput.toJson(allNotes))
    }
}

// TODO: moving this line below swing.frame fails. fix that and make the code better organized.
def NOTES = new Notes(System.getProperty("user.dir"))

def swing = new SwingBuilder()

// TODO: add close hook so that unsaved data is saved before close
ui = swing.frame(title:'jNV', defaultCloseOperation:JFrame.DISPOSE_ON_CLOSE, 
            pack:true, show:true,
            preferredSize: [520,600]
            ) {
        vbox {
                 textField( id: 'noteName',
                            preferredSize: [500,20],
                            actionPerformed: { 
                                def nName = swing.noteName.getText()
                                def searchResult = NOTES.findNotes(nName)
                                println(searchResult)
                                if(!searchResult){
                                    NOTES.add(new Note(nName))
                                    swing.noteContent.requestFocus()
                                }
                                else{
                                    searchResult.each { k,v ->
                                        swing.foundNotes.model.addElement k
                                    }
                                }
                            }
                       )
            list(   id: 'foundNotes',
                    preferredSize: [500,150], 
                    layoutOrientation: JList.VERTICAL,
                    background: Color.WHITE,
                    model: new DefaultListModel(),
                    valueChanged: { lse ->
                        def selectedNote = swing.foundNotes.selectedValue
                        swing.noteContent.selectAll()
                        swing.noteContent.replaceSelection(NOTES.getNote(selectedNote))
                    }
            )
            scrollPane(
                preferredSize:[500,400]    //, 
                //horizontalScrollBarPolicy: JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED,
                //verticalScrollBarPolicy: JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
                ){
                // removed size to make it scrollable. Eureka link: http://stackoverflow.com/a/9624094
                textArea(id: 'noteContent')
                //noteContent.addDocumentListener(new MyDocumentListener())
            }
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
        println "saveifeqd: $COUNT"
        if(COUNT == SAVE_AT){
            def doc = swing.noteContent.document
            def text = doc.getText(doc.startPosition.offset, doc.length)
            notes.setNote(swing.noteName.text, text)
            notes.save();
            COUNT = 0
            println "saved notecontent:$text"
        }
        COUNT++;
    }
}

// TODO: the params here are total hacks. fix this.
swing.noteContent.document.addDocumentListener(new AutoSaver(swing, NOTES))

ui.show()