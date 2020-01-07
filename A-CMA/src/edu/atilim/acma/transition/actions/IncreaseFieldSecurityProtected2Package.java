package edu.atilim.acma.transition.actions;

import java.nio.file.Files;
import java.util.List;
import java.util.Set;

import edu.atilim.acma.design.Accessibility;
import edu.atilim.acma.design.Design;
import edu.atilim.acma.design.Field;
import edu.atilim.acma.design.Method;
import edu.atilim.acma.design.Type;


public class IncreaseFieldSecurityProtected2Package {
	public static class Checker implements ActionChecker {

		@Override
		public void findPossibleActions(Design design, Set<Action> set) {
			List<Type> types = design.getTypes();
			
			for (Type t : types) {
				field:
				for (Field f : t.getFields()) {
					// Turns out, Java compiler binds accesses to constants (static final) in compile time
					// So, a constant does not reflect the access characteristics of a field in bytecode.
					if (f.isCompilerGenerated() || f.isConstant() ||  f.getAccess() != Accessibility.PROTECTED) continue;
					
					Accessibility newaccess = Accessibility.PACKAGE;
					
					for (Method m : f.getAccessors()) {
						if (!m.canAccess(f, newaccess))
							break field;
					}
					float criterion = 0;
					if(f.countNoInHierarchyUse() != 0) {
						criterion = f.countNoInClassUse()/f.countNoInHierarchyUse();
					}
					
					int[] fieldParams = {
							f.countNoTotalUse(),
							f.countNoInHierarchyUse(),
							f.countNoInPackageUse(),
							f.countNoInClassUse(),
							t.getNoFields(),
							t.getNoMethods()
					};
										
					set.add(new Performer(t.getName(), f.getName(), newaccess, criterion, 1, fieldParams));
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
		private int[] params;

		public Performer(String typeName, String fieldName, Accessibility newaccess, float criterion, float threshold, int[] params) {
			this.typeName = typeName;
			this.fieldName = fieldName;
			this.newAccess = newaccess;
			this.criterion = criterion;
			this.threshold = threshold;
			this.params = params;
		}

		@Override
		public void perform(Design d) {
			d.getType(typeName).getField(fieldName).setAccess(newAccess);
		}
		
		@Override
		public String toString() {
			return String.format("[Increase Field Security from Public to Protected] '%s' of '%s'", fieldName, typeName);
		}
		
		@Override
		public int getId() {
			if(criterion<threshold) {
				return ActionId.IFS_Protected2Package_t1;
			}else {
				return ActionId.IFS_Protected2Package_t2;
			}
		}
		
		@Override
		public int[] getParams() {
			return params;
		}
	}
}
