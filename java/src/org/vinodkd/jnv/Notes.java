package org.vinodkd.jnv;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

public class Notes implements Model{
	private HashMap<String,Note> notes = new HashMap<String,Note>();

	public Notes() {}

	public void add(Note n){ notes.put(n.getTitle(),n);	}
	public void set(String title, String contents) {
		Note n = new Note(title,contents);
		notes.put(title,n);
	}

	public void load(){
		// read from file here
	}

	public void store(){
		// write to file here
	}

	public Object getInitialValue(){
		load();
		return notes;
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
