package org.vinodkd.jnv;

public class JNV extends JNVBase{
	
	public static void main(String[] args) {
		JNV jnv = new JNV();
	}


	public NotesStore getStore(){
		return new SerializedStore();
	}
}