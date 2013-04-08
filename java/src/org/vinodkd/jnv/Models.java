package org.vinodkd.jnv;

import java.util.HashMap;

public class Models{
	private HashMap<String,Model> models = new HashMap<String,Model>();

	public Models() {}

	public void add(String name, Model model){ models.put(name, model);}
	public Model get(String name) { return models.get(name);}
}