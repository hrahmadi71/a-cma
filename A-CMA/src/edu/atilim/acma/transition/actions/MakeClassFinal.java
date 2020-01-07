package edu.atilim.acma.transition.actions;

import java.util.Set;

import edu.atilim.acma.design.Accessibility;
import edu.atilim.acma.design.Design;
import edu.atilim.acma.design.Type;
import edu.atilim.acma.util.Log;

public class MakeClassFinal {
	public static class Checker implements ActionChecker {
		@Override
		public void findPossibleActions(Design design, Set<Action> set) {
			for (Type t : design.getTypes()) {
				if (t.getAccess() == Accessibility.PUBLIC || t.getExtenders().size() != 0 || t.isCompilerGenerated() || t.isAnnotation() || t.isInterface() || t.isFinal()) 
					continue;
				
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
				
				set.add(new Performer(t.getName(), typeParams));
			}
		}
	}
	
	public static class Performer implements Action {
		private String typeName;
		private int[] params;
		
		public Performer(String typeName, int[] params) {
			this.typeName = typeName;
			this.params = params;
		}

		@Override
		public void perform(Design d) {
			Type t = d.getType(typeName);
			
			if (t == null) {
				Log.severe("[MakeClassFinal] Can not find type: %s.", typeName);
				return;
			}
			
			t.setFinal(true);
		}
		
		@Override
		public String toString() {
			return String.format("[Make Class Final] %s", typeName);
		}
		
		@Override
		public int getId() {
			return ActionId.MCF_t1;
		}
		
		@Override
		public int[] getParams() {
			return params;
		}
	}
}

