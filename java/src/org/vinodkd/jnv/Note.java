package org.vinodkd.jnv;

import java.util.HashMap;
import java.util.Date;

public class Note implements Model{
	private String 	title;
	private String 	contents;
	private Date	lastModified;
	public Note(String title, String contents){
		this.title 			= title;
		this.contents 		= contents;
		this.lastModified 	= new Date();
	}

	public String getTitle() {return title;}
	public String getContents() { return contents; }
	public Date getLastModified() { return lastModified;}

	public void setTitle(String title) {
		this.title = title;
		this.lastModified = new Date();
	}

	public void setContents(String contents){
		this.contents = contents;
		this.lastModified = new Date();
	}

	public Object getInitialValue() {
		// delegated to Notes, but a different implementation might have something here.
		return null;
	}
	public Object getCurrentValue() {
		// delegated to Notes, but a different implementation might have something here.
		return null;
	}
}