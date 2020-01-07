package edu.atilim.acma.transition.actions;

import java.util.Set;

import edu.atilim.acma.design.Accessibility;
import edu.atilim.acma.design.Design;
import edu.atilim.acma.design.Method;
import edu.atilim.acma.design.Method.Parameter;
import edu.atilim.acma.design.Type;

public class IntroduceFactory {
	public static class Checker implements ActionChecker {
		@Override
		public void findPossibleActions(Design design, Set<Action> set) {
			for (Type t : design.getTypes()) {
				if(t.isInterface() || t.isAbstract() || t.isCompilerGenerated() || t.isAnnotation()) 
					continue;
				
				for (Method m : t.getMethods()) {
					if (!m.isConstructor() || m.getAccess() == Accessibility.PRIVATE || m.isCompilerGenerated() || m.isAbstract() || m.isClassConstructor())
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
					
					set.add(new Performer(t.getName(), m.getSignature(), t.getExtenders().size(), 2, typeParams));
				}
			}
		}
		
	}
	
	public static class Performer implements Action {
		private String typeName;
		private String ctorName;
		private float criterion;
		private float threshold;
		private int[] params;
		
		public Performer(String typeName, String ctorName, float criterion, float threshold, int[] params) {
			this.typeName = typeName;
			this.ctorName = ctorName;
			this.criterion = criterion;
			this.threshold = threshold;
			this.params = params;
		}

		@Override
		public void perform(Design d) {
			Type t = d.getType(typeName);
			if (t == null) return;
			Method m = t.getMethod(ctorName);
			if (m == null) return;
			
			Method factory = t.createMethod("create" + t.getName());
			factory.setStatic(true);
			factory.setAccess(m.getAccess());
			factory.addInstantiatedType(t);
			factory.setReturnType(t);
			
			for (Parameter p : m.getParameters())
				factory.addParameter(p.getType(), p.getDimension());
			
			for (Method cm : m.getCallerMethods()) {
				cm.removeCalledMethod(m);
				cm.addCalledMethod(factory);
			}
			
			factory.addCalledMethod(m);
			m.setAccess(Accessibility.PRIVATE);
		}
		
		@Override
		public String toString() {
			return String.format("[Introduce Factory] for %s.%s", typeName, ctorName);
		}
		
		@Override
		public int getId() {
			if(criterion<threshold)
				return ActionId.Intr_Fac_t1;
			else
				return ActionId.Intr_Fac_t2;
		}
		
		@Override
		public int[] getParams() {
			return params;
		}
	}
}
