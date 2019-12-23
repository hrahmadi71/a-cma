package edu.atilim.acma.transition.actions;

import java.util.List;
import java.util.Set;

import edu.atilim.acma.design.Accessibility;
import edu.atilim.acma.design.Design;
import edu.atilim.acma.design.Field;
import edu.atilim.acma.design.Method;
import edu.atilim.acma.design.Type;
import edu.atilim.acma.transition.actions.DecreaseFieldSecurityPackage2Protected.Performer;
import edu.atilim.acma.util.Log;
import edu.atilim.acma.transition.actions.ActionId;

public class DecreaseFieldSecurityPrivate2Package {
	public static class Checker implements ActionChecker {

		@Override
		public void findPossibleActions(Design design, Set<Action> set) {
			List<Type> types = design.getTypes();
			
			for (Type t : types) {
				for (Field f : t.getFields()) {
					
					if (f.isCompilerGenerated() || f.isConstant() ||  f.getAccess() != Accessibility.PRIVATE) continue;
										
					set.add(new Performer(t.getName(), f.getName()));
				}
			}
		}
	}
	
	public static class Performer implements Action {
		private String typeName;
		private String fieldName;
		private Accessibility newAccess;

		public Performer(String typeName, String fieldName) {
			this.typeName = typeName;
			this.fieldName = fieldName;
			this.newAccess = Accessibility.PACKAGE;
		}

		@Override
		public void perform(Design d) {
			d.getType(typeName).getField(fieldName).setAccess(newAccess);
		}
		
		@Override
		public String toString() {
			return String.format("[Decrease Method Security from Private to Package] '%s' of '%s'", fieldName, typeName);
		}
		
		@Override
		public int getId() {
			return ActionId.DFS_Private2Package_t1;
		}
	}
}
