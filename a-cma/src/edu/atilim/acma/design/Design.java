package edu.atilim.acma.design;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.atilim.acma.metrics.MetricCalculator;
import edu.atilim.acma.metrics.MetricTable;
import edu.atilim.acma.transition.TransitionManager;
import edu.atilim.acma.transition.actions.Action;

public class Design implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private ArrayList<Type> types;
	private ArrayList<String> modificationLog;
	
	public List<Type> getTypes() {
		return Collections.unmodifiableList(types);
	}
	
	public void logModification(String log) {
		modificationLog.add(log);
	}
	
	public MetricTable getMetrics() {
		return MetricCalculator.calculate(this);
	}
	
	public List<String> getModifications() {
		return Collections.unmodifiableList(modificationLog);
	}
	
	public Type getType(String name) {
		for (int i = 0; i < types.size(); i++)
		{
			Type t = types.get(i);
			
			if (t.getName().equals(name))
				return t;
		}
		return null;
	}
	
	public List<Package> getPackages() {
		HashSet<Package> set = new HashSet<Package>();
		
		for (Type t : types) {
			set.add(new Package(t.getPackage(), this));
		}
		
		ArrayList<Package> list = new ArrayList<Package>(set);
		Collections.sort(list);
		return list;
	}
	
	public <T extends Node> T create(String name, Class<T> cls) {
		try {
			Constructor<T> ctor = cls.getConstructor(String.class, Design.class);
			T item = ctor.newInstance(name, this);
			
			if (item instanceof Type)
				types.add((Type)item);
			
			return item;
		} catch (Exception e) { e.printStackTrace(); }
		return null;
	}
	
	Reference getReference(Node from, Node to, int tag) {
		return getReference(from, to, 1, tag);
	}
	
	Reference getReference(Node from, Node to, int dimension, int tag) {
		if (from == null || to == null) return null;
		
		Reference ref = Reference.get(from, to, tag);
		ref.setDimension(dimension);
		return ref;
	}
	
	public Reference getReference(Node from, String to, int tag) {
		if (from == null) return null;
		
		int dimension = 0;
		int arrindex = to.indexOf("[]");
		String realName = to.substring(0, arrindex < 0 ? to.length() : arrindex);
		while (arrindex >= 0)
		{
			dimension++;
			arrindex = to.indexOf("[]", arrindex + 2);
		}
		
		Type reft = getType(realName);
		if (reft != null)
			return getReference(from, reft, dimension, tag);
		else
		{
			Reference ref = Reference.get(from, realName, tag);
			ref.setDimension(dimension);
			return ref;
		}
	}
	
	public Design() {
		types = new ArrayList<Type>();
		modificationLog = new ArrayList<String>();
	}
	
	public Set<Action> getPossibleActions() {
		return TransitionManager.getPossibleActions(this);
	}
	
	public Design copy() {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream oout = new ObjectOutputStream(out);
			oout.writeObject(this);
			ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()));
			return (Design)oin.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}