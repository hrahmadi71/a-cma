package edu.atilim.acma.transition.actions;

import java.util.Set;

import edu.atilim.acma.design.Design;
import edu.atilim.acma.design.Type;
import edu.atilim.acma.util.Log;

public final class MakeClassAbstract {
	public static class Checker implements ActionChecker {
		@Override
		public void findPossibleActions(Design design, Set<Action> set) {
			for (Type t : design.getTypes()) {
				if (t.isCompilerGenerated() || t.isAnnotation() || t.isInterface() || t.isAbstract()) continue;
				
				if (t.getDependentMethodsAsInstantiator().size() != 0)
					continue;
				
				if (t.getExtenders().size() == 0)
					continue;
				
				set.add(new Performer(t.getName(), t.getSuperType()!=null));
			}
		}
		
	}
	
	public static class Performer implements Action {
		private String typeName;
		private boolean typeHasSuperType;
		
		public Performer(String typeName, boolean typeHasSuperType) {
			this.typeName = typeName;
			this.typeHasSuperType = typeHasSuperType;
		}

		@Override
		public void perform(Design d) {
			Type t = d.getType(typeName);
			if (t == null) {
				Log.severe("[MakeClassAbstract] Can not find type: %s.", typeName);
				return;
			}
			t.setAbstract(true);
		}
		
		@Override
		public String toString() {
			return String.format("[Make Class Abstract] %s", typeName);
		}
		
		@Override
		public int getId() {
			if(typeHasSuperType)
				return ActionId.MCA_t1;
			else
				return ActionId.MCA_t2;
		}
	}
}
