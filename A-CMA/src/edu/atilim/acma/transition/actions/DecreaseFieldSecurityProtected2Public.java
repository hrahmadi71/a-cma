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

public class DecreaseFieldSecurityProtected2Public {
	public static class Checker implements ActionChecker {

		@Override
		public void findPossibleActions(Design design, Set<Action> set) {
			List<Type> types = design.getTypes();
			
			for (Type t : types) {
				for (Field f : t.getFields()) {
					
					if (f.isCompilerGenerated() || f.isConstant() ||  f.getAccess() != Accessibility.PROTECTED) continue;
					float criterion = 0;
					if(f.countNoTotalUse() != 0) {
						criterion = f.countNoInClassUse()/f.countNoTotalUse();
					}
					
					int[] fieldParams = {
							f.countNoTotalUse(),
							f.countNoInHierarchyUse(),
							f.countNoInPackageUse(),
							f.countNoInClassUse(),
							t.getNoFields(),
							t.getNoMethods()
					};
					
					set.add(new Performer(t.getName(), f.getName(), criterion, 1, fieldParams));
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

		public Performer(String typeName, String fieldName, float criterion, float threshold, int[] params) {
			this.typeName = typeName;
			this.fieldName = fieldName;
			this.newAccess = Accessibility.PUBLIC;
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
			return String.format("[Decrease Method Security from Protected to Public] '%s' of '%s'", fieldName, typeName);
		}
		
		@Override
		public int getId() {
//			if(criterion<threshold) {
//				return ActionId.DFS_Protected2Public_t1;
//			}else {
//				return ActionId.DFS_Protected2Public_t2;
//			}
			return ActionId.DFS_Protected2Public_t1;
		}
		
		@Override
		public int[] getParams() {
			return params;
		}
	}
}
