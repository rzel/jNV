package org.vinodkd.jnv;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;

import java.util.HashMap;
import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Map;
import java.util.Set;

import org.stringtree.json.*;

public class JsonStore implements NotesStore{

	private File saveFile;
	private final String FILENAME = "notes.json";

	public JsonStore(){}

	public void setDir(String dirspec){
		saveFile = new File(dirspec +  System.getProperty("file.separator") + FILENAME);
	}

	public HashMap<String,Note> load(){
		if(saveFile.exists()){
			BufferedReader br;
			StringBuffer notesAsStr = new StringBuffer("");
			char[] buf = new char[4000];
			int chars;

			try{
				br = new BufferedReader(new FileReader(saveFile));
				while((chars = br.read(buf,0,4000))!= -1){
					notesAsStr.append(buf,0,chars);
				}
				br.close();
			}catch(Exception ioe){
				//TODO: HANDLE IT IF REQD
				// didnt feel like catching each exception separately: IOException, FileNotFoundException.
			}

			JSONReader notesReader = new JSONReader();

			// System.out.println("read json:" + notesAsStr);

			//notes = (HashMap<String,Note>)
			Object allNotes = notesReader.read(notesAsStr.toString());
			if(allNotes != null){
				return fromJson(allNotes);
			}
		}
		return null;
	}

	private HashMap<String,Note> fromJson(Object allNotes){
		HashMap<String,Note> notes = new HashMap<String,Note>();
		@SuppressWarnings("unchecked")
		Map<Object,Object> map = (Map<Object,Object>) allNotes;

		 Set<Object> keys = map.keySet();
		 for(Object o: keys){
		 	Note n = noteFromJson(map.get(o));
		 	//JSONWriter jsw = new JSONWriter();
		 	//System.out.println("note:"+ n.getTitle() + ",date:" + n.getLastModified() + ",date from json:" + jsw.write(n.getLastModified()));
		 	notes.put(n.getTitle(),n);
		 }
		 return notes;
	}

	private Note noteFromJson(Object json){
		Note n = new Note("","");

		@SuppressWarnings("unchecked")
		Map<String,Object> jsonNote = (Map<String,Object>)json;
		
		n.setTitle((String)jsonNote.get("title"));
		n.setContents((String)jsonNote.get("contents"));
		n.setLastModified(dateFromJson(jsonNote.get("lastModified")));
		return n;
	}

	private Date dateFromJson(Object jsonObj){
		@SuppressWarnings("unchecked")
		Map<String,Object> jsonDate = (Map<String,Object>)jsonObj;
		
		Calendar cal = Calendar.getInstance();
		//System.out.println("year:" + jsonDate.get("year"));
		int year 	= ((Long)jsonDate.get("year")).intValue();
		int month 	= ((Long)jsonDate.get("month")).intValue();
		int date 	= ((Long)jsonDate.get("date")).intValue();
		int hour 	= ((Long)jsonDate.get("hours")).intValue();
		int mins 	= ((Long)jsonDate.get("minutes")).intValue();
		int secs 	= ((Long)jsonDate.get("seconds")).intValue();
		cal.set(year, month, date, hour, mins, secs);
		// TimeZone calTZ = cal.getTimeZone();
		// calTZ.setRawOffset(((Long)jsonDate.get("timezoneOffset")).intValue());
		return cal.getTime();
	}

	public void store(HashMap<String,Note> notes){
		try{	
			FileWriter fw = new FileWriter(saveFile);
			String notesAsStr = new JSONWriter().write(notes);
			// System.out.println("writing json:" + notesAsStr);
			fw.write(notesAsStr);
			fw.close();
			System.out.println("stored in " + saveFile.toString());
		}catch(Exception e){
			System.out.println("Problems storing to " + saveFile.toString());
		}
	}
}