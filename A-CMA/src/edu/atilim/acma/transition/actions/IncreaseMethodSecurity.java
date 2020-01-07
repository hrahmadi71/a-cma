package edu.atilim.acma.transition.actions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import edu.atilim.acma.design.Accessibility;
import edu.atilim.acma.design.Design;
import edu.atilim.acma.design.Method;
import edu.atilim.acma.design.Type;
import edu.atilim.acma.util.Log;

public class IncreaseMethodSecurity {
	public static class Checker implements ActionChecker {

		@Override
		public void findPossibleActions(Design design, Set<Action> set) {
			List<Type> types = design.getTypes();
			
			for (Type t : types) {
				method:
				for (Method m : t.getMethods()) {
					
					if (m.isCompilerGenerated() || m.isOverride() || m.isFinal() ||  m.getAccess() == Accessibility.PRIVATE || m.isConstructor() || m.isClassConstructor()) continue;
					
					Accessibility newaccess = Accessibility.PRIVATE;

					if (m.getAccess() == Accessibility.PUBLIC)
						newaccess = Accessibility.PROTECTED;
					if (m.getAccess() == Accessibility.PROTECTED)
						newaccess = Accessibility.PACKAGE;
					if (m.getAccess() == Accessibility.PACKAGE)
						newaccess = Accessibility.PRIVATE;
					
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
				Log.severe("[IncreaseMethodSecurity] Can not find type: %s.", methodName);
				return;
			}
			m.setAccess(newAccess);
		}
		
		@Override
		public String toString() {
			return String.format("[Increase Method Security] '%s' of '%s' to '%s'", methodName, typeName, newAccess);
		}
		
		@Override
		public int getId() {
			return -1; // not implemented!
		}
		
		@Override
		public int[] getParams() {
			return params;
		}
	}
}
