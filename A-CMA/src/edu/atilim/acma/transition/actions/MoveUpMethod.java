package edu.atilim.acma.transition.actions;

import java.util.Set;

import edu.atilim.acma.design.Accessibility;
import edu.atilim.acma.design.Design;
import edu.atilim.acma.design.Method;
import edu.atilim.acma.design.Type;
import edu.atilim.acma.util.Log;

public class MoveUpMethod {
	public static class Checker implements ActionChecker {
		@Override
		public void findPossibleActions(Design design, Set<Action> set) {
			for (Type t : design.getTypes()) {
				Type superType = t.getSuperType();
				
				if(superType == null || superType.isAbstract() || superType.isInterface() || t.isInterface() || t.isAbstract() || t.isCompilerGenerated() || t.isAnnotation()) 
					continue;
				
				for (Method m : t.getMethods()) {
					if(m.getAccess() == Accessibility.PRIVATE || m.getAccess() == Accessibility.PUBLIC ||  m.isCompilerGenerated() || m.isStatic() ||  m.isClassConstructor() || !m.canBeMovedTo(superType)) 
						continue;
					
					int[] methodParams = {
							m.countNoTotalCallers(),
							m.countNoInClassCallers(),
							m.countInHierarchyCallers(),
							m.countInPckageCallers(),
							m.countNoOverrides(),
							m.getNoParameters(),
							t.getNoFields(),
							t.getNoMethods()
					};
					
					set.add(new Performer(t.getName(), m.getSignature(), superType.getName(), superType.getExtenders().size(), 2, methodParams));
				}	
			}
		}
	}
	
	public static class Performer implements Action {
		private String typeName;
		private String methodName;
		private String newOwnerTypeName;
		private float criterion;
		private float threshold;
		private int[] params;
	
		public Performer(String typeName, String methodName, String newOwnerTypeName, float criterion, float threshold, int[] params) {
			this.typeName = typeName;
			this.methodName = methodName;
			this.newOwnerTypeName = newOwnerTypeName;
			this.criterion = criterion;
			this.threshold = threshold;
			this.params = params;
		}

		@Override
		public void perform(Design d) {
			Method m = d.getType(typeName).getMethod(methodName);
			Type t = d.getType(newOwnerTypeName);
		
			if (m == null) {
			Log.severe("[MoveUpMethod] Can not find method: %s.", methodName);
			return;
			}
			
			m.setOwnerType(t);
		}
	
		@Override
		public String toString() {	
			return String.format("[Move Up Method] '%s' of '%s' to its super class '%s'", methodName,typeName,newOwnerTypeName);
		}
		
		@Override
		public int getId() {
			if(criterion<threshold)
				return ActionId.MUM_t1;
			else
				return ActionId.MUM_t2;
		}
		
		@Override
		public int[] getParams() {
			return params;
		}
	}
}
