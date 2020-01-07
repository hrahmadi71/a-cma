package edu.atilim.acma.transition.actions;

import java.util.List;
import java.util.Set;

import edu.atilim.acma.design.Accessibility;
import edu.atilim.acma.design.Design;
import edu.atilim.acma.design.Method;
import edu.atilim.acma.design.Type;
import edu.atilim.acma.util.Log;

public class IncreaseMethodSecurityPackage2Private {
	public static class Checker implements ActionChecker {

		@Override
		public void findPossibleActions(Design design, Set<Action> set) {
			List<Type> types = design.getTypes();
			
			for (Type t : types) {
				method:
				for (Method m : t.getMethods()) {
					
					if (m.isCompilerGenerated() || m.isOverride() || m.isFinal() ||  m.getAccess() != Accessibility.PACKAGE || m.isConstructor() || m.isClassConstructor()) continue;
					
					Accessibility newaccess = Accessibility.PRIVATE;
					
					for (Method mt : m.getCallerMethods()) {
						if (!mt.canAccess(m, newaccess))
							break method;
					}
					
					int[] methodParams = {
							m.countNoTotalCallers(),
							m.countNoInClassCallers(),
							m.countInHierarchyCallers(),
							m.countInPckageCallers(),
							m.countNoOverrides(),
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
				Log.severe("[IncreaseMethodSecurityPackage2Private] Can not find type: %s.", methodName);
				return;
			}
			m.setAccess(newAccess);
		}
		
		@Override
		public String toString() {
			return String.format("[Increase Method Security from Package to Private] '%s' of '%s' to '%s'", methodName, typeName, newAccess);
		}
		
		@Override
		public int getId() {
			return ActionId.IMS_Package2Private_t1;
		}
		
		@Override
		public int[] getParams() {
			return params;
		}
	}
}
