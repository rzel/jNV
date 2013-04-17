package org.vinodkd.jnv;

import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.HashMap;

public class SerializedStore implements NotesStore{

	private File saveFile;
	private final String FILENAME = "notes.ser";

	public SerializedStore(){}

	public void setDir(String dirspec){
		saveFile = new File(dirspec +  System.getProperty("file.separator") + FILENAME);
	}

	@SuppressWarnings("unchecked")
	public HashMap<String,Note> load(){
		if(saveFile.exists()){
			HashMap<String,Note>notes = null;
			try{
				ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(saveFile)));
				notes = (HashMap<String,Note>)ois.readObject();
				ois.close();
			}catch(Exception ioe){
				//TODO: HANDLE IT IF REQD
				// didnt feel like catching each exception separately: IOException, FileNotFoundException.
			}
			return notes;
		}
		return null;
	}

	public void store(HashMap<String,Note> notes){
		try{	
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile));
			oos.writeObject(notes);
			oos.flush();
			oos.close();
			System.out.println("stored in " + saveFile.toString());
		}catch(Exception e){
			System.out.println("Problems storing to " + saveFile.toString() + ".\n Exception:" + e);
		}
	}
}