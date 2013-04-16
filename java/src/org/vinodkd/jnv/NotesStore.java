package org.vinodkd.jnv;

import java.util.HashMap;

public interface NotesStore{
	public void setDir(String dirspec);
	public HashMap<String,Note> load();
	public void store(HashMap<String,Note> notes);
}