package edu.atilim.acma.transition.actions;

import java.util.Set;

import edu.atilim.acma.design.Accessibility;
import edu.atilim.acma.design.Design;
import edu.atilim.acma.design.Method;
import edu.atilim.acma.design.Type;
import edu.atilim.acma.util.Log;

public class DecreaseMethodSecurityProtected2Public {
	public static class Checker implements ActionChecker {

		@Override
		public void findPossibleActions(Design design, Set<Action> set) {
			
			for (Type t : design.getTypes()) {
				for (Method m : t.getMethods()) {
					
					if (m.isCompilerGenerated() || m.isOverride() || m.isFinal() ||  m.getAccess() != Accessibility.PROTECTED || m.isConstructor() || m.isClassConstructor()) 
						continue;
					
					Accessibility newaccess = Accessibility.PUBLIC;
					float criterion = 0;
					if(m.countNoTotalCallers() != 0) {
						criterion = m.countNoInClassCallers() / m.countNoTotalCallers();
					}
					set.add(new Performer(t.getName(), m.getSignature(), newaccess, criterion, 1));
				}
			}
		}
	}
	
	public static class Performer implements Action {
		private String typeName;
		private String methodName;
		private Accessibility newAccess;
		private float criterion;
		private float threshold;

		public Performer(String typeName, String methodName, Accessibility newAccess, float criterion, float threshold) {
			this.typeName = typeName;
			this.methodName = methodName;
			this.newAccess = newAccess;
			this.criterion = criterion; 
			this.threshold = threshold;
		}

		@Override
		public void perform(Design d) {
			Method m = d.getType(typeName).getMethod(methodName);
			
			if (m == null) {
				Log.severe("[DecreaseMethodSecurityProtected2Public] Can not find type: %s.", methodName);
				return;
			}
			
			m.setAccess(newAccess);
		}
		
		@Override
		public String toString() {
			return String.format("[Decrease Method Security from Protected to Public] '%s' of '%s' to '%s'", methodName, typeName, newAccess);
		}
		
		@Override
		public int getId() {
			if(criterion<threshold) {
				return ActionId.DMS_Protected2Public_t1;
			}else {
				return ActionId.DMS_Protected2Public_t2;
			}
		}
	}
}
