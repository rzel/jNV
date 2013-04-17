package org.vinodkd.jnv;

public class JNV_Json extends JNVBase{
	
	public static void main(String[] args) {
		JNV_Json jnv = new JNV_Json();
	}


	public NotesStore getStore(){
		return new JsonStore();
	}
}