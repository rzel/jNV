package org.vinodkd.jnv;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;
import java.io.Serializable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Notes implements Model, Serializable{
	private HashMap<String,Note> notes = new HashMap<String,Note>();

	private transient final String ABOUTNOTE_lOC = "res/about.txt";

	private transient String saveLoc;
	private transient File saveFile;
	private transient final String FILENAME = "notes.ser";

	public Notes(String saveLoc) {
		this.saveLoc = saveLoc;
		saveFile = new File(saveLoc + System.getProperty("file.separator") + FILENAME);
		load();
	}

	public void add(Note n){ notes.put(n.getTitle(),n);	}
	public void set(String title, String contents) {
		Note n = new Note(title,contents);
		notes.put(title,n);
	}

	public Note get(String title)	{ return notes.get(title);}

	@SuppressWarnings("unchecked") 
	public void load(){
		if(saveFile.exists()){
			try{
					ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(saveFile)));
					notes = (HashMap<String,Note>)ois.readObject();
					ois.close();
			}catch(Exception ioe){
				//TODO: HANDLE IT IF REQD
				// didnt feel like catching each exception separately: IOException, FileNotFoundException.
			}
		}
	}

	public void store(){
		try{	
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile));
			oos.writeObject(notes);
			oos.flush();
			oos.close();
			System.out.println("stored in " + saveFile.toString());
		}catch(Exception e){
			System.out.println("Problems storing to " + saveFile.toString());
		}
	}

	public Object getInitialValue(){
		load();
		if(!notes.containsKey("About")){
			Note aboutNote = new Note("About", getAboutText());
			add(aboutNote);
		}
		return notes;
	}

	private String getAboutText(){
		InputStream is = getClass().getClassLoader().getResourceAsStream(ABOUTNOTE_lOC);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		StringBuffer aboutText = new StringBuffer("");
		String line;
		try{
			while((line=br.readLine())!=null){
				aboutText.append(line);aboutText.append("\n");
			}
			br.close();
		}catch(IOException ioe){
			// TODO: PUT IN DEFAULT TEXT IF THIS FAILS
		}
		return aboutText.toString();

	}

	public Object getCurrentValue(){
		return notes;
	}

	public List<String> search(String searchText){
		ArrayList<String> searchResult = new ArrayList<String>();
		Set<String> titles = notes.keySet();
		for(String title: titles){
			if(title.contains(searchText)){
				searchResult.add(title);
			}
		}
		return searchResult;
	}
}
