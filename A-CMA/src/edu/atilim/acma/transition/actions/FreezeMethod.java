package edu.atilim.acma.transition.actions;

import java.util.Set;

import edu.atilim.acma.design.Design;
import edu.atilim.acma.design.Field;
import edu.atilim.acma.design.Method;
import edu.atilim.acma.design.Type;
import edu.atilim.acma.util.Log;

public class FreezeMethod {

	public static class Checker implements ActionChecker {		
		@Override
		public void findPossibleActions(Design design, Set<Action> set) {
			for (Type t : design.getTypes()) {
				for (Method m : t.getMethods()) {
					boolean flag = false;
					
					if(m.isCompilerGenerated() || m.isOverride() || m.isStatic() || m.isConstructor() || m.isClassConstructor()) continue;
					
					for (Field f : m.getAccessedFields()) {			
						if (f.getOwnerType() != t || !f.isStatic()){
							flag = true;
							break;
						}
					}
					if(!flag) {
						for(Method mt : m.getCalledMethods()){
							if(mt.getOwnerType() != t ||  !mt.isStatic() ){
								flag = true;
								break;
							}
						}
					}
					
					float criterion = 0;
					if(m.countNoTotalCallers() > 0) {
						criterion = m.countNoInClassCallers() / m.countNoTotalCallers();
					}
					set.add(new Performer(t.getName(), m.getSignature(), flag, criterion, 1));
				}
			}		
		}
	}//end of checker
	
	public static class Performer implements Action {	
			private String typeName;
			private String methodName;
			private boolean parameterizeFlag;
			private float criterion;
			private float threshold;
		
		public Performer(String typeName, String methodName, boolean parameterizeFlag, float criterion, float threshold) {
			this.typeName = typeName;
			this.methodName = methodName;
			this.parameterizeFlag = parameterizeFlag;
			this.criterion = criterion;
			this.threshold = threshold;
		}

		@Override
		public void perform(Design d) {
			Type t = d.getType(typeName);
			Method m = t.getMethod(methodName);
			
			if (m == null) {
				Log.severe("[FreezeMethod] Can not find method: %s.", methodName);
				return;
			}
			
			m.setStatic(true);
			
			if(parameterizeFlag){
				m.addParameter(t);
			}
		}
		
		@Override
		public String toString() {
			
			return String.format("[Freeze Method] Convert '%s' of '%s' to static ", methodName,typeName);
		}
		
		@Override
		public int getId() {
			if(criterion<threshold) {
				if(parameterizeFlag) {
					return ActionId.FM_t1;
				}else return ActionId.FM_t2;
			}else {
				if(parameterizeFlag) {
					return ActionId.FM_t3;
				}else return ActionId.FM_t4;
			}
		}
	}//end of performer
}
