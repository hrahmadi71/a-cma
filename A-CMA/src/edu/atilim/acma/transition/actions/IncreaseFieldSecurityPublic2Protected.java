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
import edu.atilim.acma.design.Field;
import edu.atilim.acma.design.Method;
import edu.atilim.acma.design.Type;


public class IncreaseFieldSecurityPublic2Protected {
	public static class Checker implements ActionChecker {

		@Override
		public void findPossibleActions(Design design, Set<Action> set) {
			List<Type> types = design.getTypes();
						
			for (Type t : types) {
				field:
				for (Field f : t.getFields()) {
					// Turns out, Java compiler binds accesses to constants (static final) in compile time
					// So, a constant does not reflect the access characteristics of a field in bytecode.
					if (f.isCompilerGenerated() || f.isConstant() ||  f.getAccess() != Accessibility.PUBLIC) continue;
					
					Accessibility newaccess = Accessibility.PROTECTED;
					
					for (Method m : f.getAccessors()) {
						if (!m.canAccess(f, newaccess))
							break field;
					}
					float criterion = 0;
					if(f.countNoTotalUse() != 0) {
						criterion = f.countNoInClassUse()/f.countNoTotalUse();
					}
					set.add(new Performer(t.getName(), f.getName(), newaccess, criterion, 1));
				}
			}
		}
		
	}
	
	public static class Performer implements Action {
		private String typeName;
		private String fieldName;
		private Accessibility newAccess;
		private float criterion;
		private float threshold;

		public Performer(String typeName, String fieldName, Accessibility newaccess, float criterion, float threshold) {
			this.typeName = typeName;
			this.fieldName = fieldName;
			this.newAccess = newaccess;
			this.criterion = criterion;
			this.threshold = threshold;
		}

		@Override
		public void perform(Design d) {
			Field f = d.getType(typeName).getField(fieldName);
			f.setAccess(newAccess);
		}
		
		@Override
		public String toString() {
			return String.format("[Increase Field Security from Public to Protected] '%s' of '%s'", fieldName, typeName);
		}
		
		@Override
		public int getId() {
			if(criterion<threshold) {
				return ActionId.IFS_Public2Protected_t1;
			}else {
				return ActionId.IFS_Public2Protected_t2;
			}
		}
	}
}
