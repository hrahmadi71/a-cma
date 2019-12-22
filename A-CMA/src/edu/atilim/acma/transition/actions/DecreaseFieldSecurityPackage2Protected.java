package edu.atilim.acma.transition.actions;

import java.util.List;
import java.util.Set;

import edu.atilim.acma.design.Accessibility;
import edu.atilim.acma.design.Design;
import edu.atilim.acma.design.Field;
import edu.atilim.acma.design.Method;
import edu.atilim.acma.design.Type;
import edu.atilim.acma.util.Log;

public class DecreaseFieldSecurityPackage2Protected {
	public static class Checker implements ActionChecker {

		@Override
		public void findPossibleActions(Design design, Set<Action> set) {
			List<Type> types = design.getTypes();
			
			for (Type t : types) {
				for (Field f : t.getFields()) {
					
					if (f.isCompilerGenerated() || f.isConstant() ||  f.getAccess() != Accessibility.PACKAGE) continue;
										
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
			this.newAccess = Accessibility.PROTECTED;
		}

		@Override
		public void perform(Design d) {
			d.getType(typeName).getField(fieldName).setAccess(newAccess);
		}
		
		@Override
		public String toString() {
			return String.format("[Decrease Method Security from Package to Protected] '%s' of '%s'", fieldName, typeName);
		}
		
		@Override
		public int getId() {
			return 0;
		}
	}
}
