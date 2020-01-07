package edu.atilim.acma.transition.actions;

import java.util.Set;

import edu.atilim.acma.design.Accessibility;
import edu.atilim.acma.design.Design;
import edu.atilim.acma.design.Method;
import edu.atilim.acma.design.Type;
import edu.atilim.acma.util.Log;

public class DecreaseMethodSecurityPrivate2Package {
	public static class Checker implements ActionChecker {

		@Override
		public void findPossibleActions(Design design, Set<Action> set) {
			
			for (Type t : design.getTypes()) {
				for (Method m : t.getMethods()) {
					
					if (m.isCompilerGenerated() || m.isOverride() || m.isFinal() ||  m.getAccess() != Accessibility.PRIVATE || m.isConstructor() || m.isClassConstructor()) 
						continue;
					
					Accessibility newaccess = Accessibility.PACKAGE;
					
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
					
					set.add(new Performer(t.getName(), m.getSignature(), newaccess, methodParams));
				}
			}
		}
	}
	
	public static class Performer implements Action {
		private String typeName;
		private String methodName;
		private Accessibility newAccess;
		private int[] params;

		public Performer(String typeName, String methodName, Accessibility newAccess, int[] params) {
			this.typeName = typeName;
			this.methodName = methodName;
			this.newAccess = newAccess;
			this.params = params;
		}

		@Override
		public void perform(Design d) {
			Method m = d.getType(typeName).getMethod(methodName);
			
			if (m == null) {
				Log.severe("[DecreaseMethodSecurityPrivate2Package] Can not find type: %s.", methodName);
				return;
			}
			
			m.setAccess(newAccess);
		}
		
		@Override
		public String toString() {
			return String.format("[Decrease Method Security from Private to Package] '%s' of '%s' to '%s'", methodName, typeName, newAccess);
		}
		
		@Override
		public int getType() {
			return ActionType.METHOD_LEVEL;
		}
		
		@Override
		public int getId() {
			return ActionId.DMS_Private2Package_t1;
		}
		
		@Override
		public int[] getParams() {
			return params;
		}
	}
}
