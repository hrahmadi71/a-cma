package edu.atilim.acma.transition.actions;

import java.util.Set;

import edu.atilim.acma.design.Design;
import edu.atilim.acma.design.Type;
import edu.atilim.acma.util.Log;

public class MakeClassConcrete {
	public static class Checker implements ActionChecker {
		@Override
		public void findPossibleActions(Design design, Set<Action> set) {
			for (Type t : design.getTypes()) {
				if (t.isCompilerGenerated() || t.isAnnotation()) continue;
				
				if(t.isAbstract()) {
					int[] typeParams = {
							t.getNoFields(),
							t.getNoMethods(),
							t.getDependentFields().size(),
							t.getDependentMethodsAsInstantiator().size(),
							t.getDependentMethodsAsParameter().size(),
							t.getDependentMethodsAsReturnType().size(),
							t.getExtenders().size(),
							t.getImplementers().size(),
							t.getNoSiblings(),
							t.getNoTotalMethodsOfSiblings()
					};
					set.add(new Performer(t.getName(), t.getSuperType()!=null, typeParams));
				}
			}
		}
	}
	
	public static class Performer implements Action {
		private String typeName;
		private boolean typeHasSuperType;
		private int[] params;

		public Performer(String typeName, boolean typeHasSuperType, int[] params) {
			this.typeName = typeName;
			this.typeHasSuperType = typeHasSuperType;
			this.params = params;
		}

		@Override
		public void perform(Design d) {
			Type t = d.getType(typeName);
			
			if (t == null) {
				Log.severe("[MakeClassConcrete] Can not find type: %s.", typeName);
				return;
			}
			
			t.setAbstract(false);
		}
		
		@Override
		public String toString() {
			return String.format("[Make Class Concrete] %s", typeName);
		}
		
		@Override
		public int getType() {
			return ActionType.CLASS_LEVEL;
		}
		
		@Override
		public int getId() {
//			if(typeHasSuperType)
//				return ActionId.MCC_t1;
//			else
//				return ActionId.MCC_t2;
			return ActionId.MCC_t1;
		}
		
		@Override
		public int[] getParams() {
			return params;
		}
	}
}
