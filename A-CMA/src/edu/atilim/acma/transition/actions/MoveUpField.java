package edu.atilim.acma.transition.actions;

import java.util.Set;

import edu.atilim.acma.design.Accessibility;
import edu.atilim.acma.design.Design;
import edu.atilim.acma.design.Field;
import edu.atilim.acma.design.Type;
import edu.atilim.acma.util.Log;

public class MoveUpField {
	public static class Checker implements ActionChecker {
		@Override
		public void findPossibleActions(Design design, Set<Action> set) {
			for (Type t : design.getTypes()) {
				Type superType = t.getSuperType();
				
				if(superType == null || t.isCompilerGenerated() || t.isAnnotation()) 
					continue;
				
				for (Field f : t.getFields()) {
					if(f.getAccess() == Accessibility.PRIVATE || f.isCompilerGenerated()) 
						continue;
					float criterion = 0;
					if(f.countNoTotalUse() != 0) {
						criterion = f.countNoInClassUse()/f.countNoTotalUse();
					}
					set.add(new Performer(t.getName(), f.getName(), superType.getName(), criterion, 1));
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
	
		public Performer(String typeName, String fieldName, String newOwnerTypeName, float criterion, float threshold) {
			this.typeName = typeName;
			this.fieldName = fieldName;
			this.newOwnerTypeName = newOwnerTypeName;
			this.criterion = criterion;
			this.threshold = threshold;
		}

		@Override
		public void perform(Design d) {
			Field f = d.getType(typeName).getField(fieldName);
			Type t = d.getType(newOwnerTypeName);
		
			if (f == null) {
				Log.severe("[MoveUpField] Can not find field: %s.", fieldName);
				return;
			}
			
			f.setOwnerType(t);
		}
	
		@Override
		public String toString() {	
			return String.format("[Move Up Field] '%s' of '%s' to its super class '%s'", fieldName,typeName,newOwnerTypeName);
		}
		
		@Override
		public int getId() {
			if(criterion<threshold) {
				return ActionId.MUF_t1;
			}else {
				return ActionId.MUF_t2;
			}
		}
	}
}
