package org.vinodkd.jnv;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

class JNV{
	public static void main(String[] args) {
		JNV jnv = new JNV();
		Models logicalModels 	= jnv.createModels();
		// Models viewModels 		= jnv.createViewModels(logicalModels);
		JFrame ui = jnv.createUI(logicalModels);	// call getInitialState to build ui.
		// // ignoring the urge to overengineer with state machines for now.
		// ui.addBehaviors();
		ui.setVisible(true);
	}

	public JNV(){}

	public Models createModels(){
		Notes notes = new Notes();
		notes.getInitialValue();
		Models models = new Models();
		models.add("notes",notes);
		return models;
	}

	// public Models createViewModels(Models logicalModels){
	// 	ViewModels models = new ViewModels();

	// 	Model logicalNotes = logicalModels.get("notes");
	// 	models.add("notetitle", new NoteTitle(logicalNotes));
	// 	models.add("searchresults", new SearchResults(logicalNotes));
	// 	models.add("notecontents", new NoteContents(logicalNotes));
	// }

	private boolean SEARCHING = false;

	public JFrame createUI(Models models){
		JTextField noteName = new JTextField();
		noteName.setPreferredSize(new Dimension(500,25));
		noteName.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e){
				SEARCHING = true;
				System.out.println("searching");
			}
		}
		);

		JList<String> foundNotes = new JList<String>(new DefaultListModel<String>());
		foundNotes.setLayoutOrientation(JList.VERTICAL);
		JScrollPane foundNotesScroller = new JScrollPane(foundNotes);
		foundNotesScroller.setPreferredSize(new Dimension(500,150));

		JTextArea noteContent = new JTextArea();
		noteContent.setLineWrap(true);
		noteContent.setWrapStyleWord(true);
		JScrollPane noteContentScroller = new JScrollPane(noteContent);
		noteContentScroller.setPreferredSize(new Dimension(500,400));

		JFrame ui = new JFrame("jNV");
		ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ui.setPreferredSize(new Dimension(520,600));
		ui.getContentPane().setLayout(new BoxLayout(ui.getContentPane(),BoxLayout.PAGE_AXIS));

		ui.add(noteName);
		ui.add(foundNotesScroller);
		ui.add(noteContentScroller);
		
		ui.pack();
		ui.addWindowListener( new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				saveIncremental();
			}
		}
		);
		return ui;
	}

	private void saveIncremental(){
		System.out.println("saved!");
	}
}