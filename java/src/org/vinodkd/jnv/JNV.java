package org.vinodkd.jnv;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

class JNV{
	public static void main(String[] args) {
		JNV jnv = new JNV();
		Models logicalModels 	= jnv.createModels();
		// Models viewModels 		= jnv.createViewModels(logicalModels);
		HashMap<String,Component> ui = jnv.createUI(logicalModels);	// call getInitialState to build ui.
		// ignoring the urge to overengineer with state machines for now.
		jnv.addBehaviors(ui,logicalModels);
		ui.get("window").setVisible(true);
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


	public HashMap<String,Component> createUI(Models models){
		HashMap<String,Component> controls = new HashMap<String,Component>();
		JTextField noteName = new JTextField();
		noteName.setPreferredSize(new Dimension(500,25));
		controls.put("noteName", noteName);
		}
		);

		JList<String> foundNotes = new JList<String>(new DefaultListModel<String>());
		foundNotes.setLayoutOrientation(JList.VERTICAL);
		JScrollPane foundNotesScroller = new JScrollPane(foundNotes);
		foundNotesScroller.setPreferredSize(new Dimension(500,150));
		controls.put("foundNotes", foundNotes);

		JTextArea noteContent = new JTextArea();
		noteContent.setLineWrap(true);
		noteContent.setWrapStyleWord(true);
		JScrollPane noteContentScroller = new JScrollPane(noteContent);
		noteContentScroller.setPreferredSize(new Dimension(500,400));
		controls.put("noteContent", noteContent);

		Box vbox = Box.createVerticalBox();
		vbox.add(noteName);
		vbox.add(foundNotesScroller);
		vbox.add(noteContentScroller);

		JFrame ui = new JFrame("jNV");
		ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ui.setPreferredSize(new Dimension(520,600));

		ui.add(vbox);

		ui.pack();
		controls.put("window", ui);
		return controls;
	}

	private boolean SEARCHING = false;

	public void addBehaviors(HashMap<String,Component> ui, final Models models){
		final JTextField noteName = (JTextField)ui.get("noteName");
		final JList foundNotes = (JList)ui.get("foundNotes");
		final JTextArea noteContent = (JTextArea)ui.get("noteContent");
		final JFrame window = (JFrame)ui.get("window");

		final Notes notes = (Notes) models.get("notes");

		noteName.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e){
				SEARCHING = true;
			}
		}
		);

		window.addWindowListener( new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				saveIncremental();
			}
		}
		);
	}

	private void saveIncremental(){
		System.out.println("saved!");
	}
}