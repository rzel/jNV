package org.vinodkd.jnv;

import javax.swing.table.*;
import java.util.HashMap;

public class NoteListTableModel extends DefaultTableModel{

	NoteListTableModel(HashMap<String,Note> notes){
		super(new Object[] {"Title", "Last Modified"},0);
		// TODO: figure out why an extra blank rows shows up
		for(String title:notes.keySet()){
			addRow(new Object[] {title, notes.get(title).getLastModified()});
		}
	}

	@Override
	public boolean isCellEditable(int row, int col)	{return false;}
}
