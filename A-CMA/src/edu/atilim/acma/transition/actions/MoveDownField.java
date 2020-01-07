package edu.atilim.acma.transition.actions;

import java.util.List;
import java.util.Set;

import edu.atilim.acma.design.Accessibility;
import edu.atilim.acma.design.Design;
import edu.atilim.acma.design.Field;
import edu.atilim.acma.design.Method;
import edu.atilim.acma.design.Type;
import edu.atilim.acma.util.Log;

public class MoveDownField {
	public static class Checker implements ActionChecker {
		@Override
		public void findPossibleActions(Design design, Set<Action> set) {
			for (Type type : design.getTypes()) {
				List<Type> childTypeList = type.getExtenders();
				
				if(childTypeList == null || type.isAnnotation() || type.isCompilerGenerated())
					continue;
				
				for (Field f : type.getFields()) {
					if(f.getAccess() == Accessibility.PRIVATE || f.isCompilerGenerated()) 
						continue;
					
					type:
					for(Type t : childTypeList){
						if(t.isAnnotation() || t.isCompilerGenerated()) continue;
						
						for (Method m : f.getAccessors()) {
							if (!m.canAccess(t) || !m.canAccess(t, f.getAccess()) || !t.isAncestorOf(m.getOwnerType())) 
								break type;
						}
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
						
						set.add(new Performer(type.getName(), f.getName(), t.getName(), criterion, 1, fieldParams));
					}	
				}	
			}
		}
	}
	
	public static class Performer implements Action {
		private String typeName;
		private String fieldName;
		private String newOwnerTypeName;
		private float criterion;
		private float threshold;
		private int[] params;
	
		public Performer(String typeName, String fieldName, String newOwnerTypeName, float criterion, float threshold, int[] params) {
			this.typeName = typeName;
			this.fieldName = fieldName;
			this.newOwnerTypeName = newOwnerTypeName;
			this.criterion = criterion;
			this.threshold = threshold;
			this.params = params;
		}

		@Override
		public void perform(Design d) {
			Field f = d.getType(typeName).getField(fieldName);
			Type t = d.getType(newOwnerTypeName);
		
			if (f == null) {
				Log.severe("[MoveDownField] Can not find field: %s.", fieldName);
				return;
			}
			
			f.setOwnerType(t);	
		}
	
		@Override
		public String toString() {	
			return String.format("[Move Down Field] '%s' of '%s' to its child class '%s'", fieldName,typeName,newOwnerTypeName);
		}
		
		@Override
		public int getId() {
//			if(criterion<threshold) {
//				return ActionId.MDM_t1;
//			}else {
//				return ActionId.MDM_t2;
//			}
			return ActionId.MDM_t1;
		}
		
		@Override
		public int[] getParams() {
			return params;
		}
	}
}

