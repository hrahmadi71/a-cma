package edu.atilim.acma.transition.actions;

import java.util.List;
import java.util.Set;

import edu.atilim.acma.design.Accessibility;
import edu.atilim.acma.design.Design;
import edu.atilim.acma.design.Method;
import edu.atilim.acma.design.Type;
import edu.atilim.acma.design.Method.Parameter;
import edu.atilim.acma.util.Log;

public class InstantiateMethod {
	public static class Checker implements ActionChecker {
		List<Parameter> parameterList;
	
		@Override
		public void findPossibleActions(Design design, Set<Action> set) {
			for (Type type : design.getTypes()) {
			
				if(type.isInterface() || type.isAbstract() || type.isCompilerGenerated() || type.isAnnotation()) 
					continue;
			
				for(Method m : type.getMethods() ){
					parameterList = m.getParameters();
						
					if(parameterList == null || m.getAccess() == Accessibility.PROTECTED || m.getAccess() == Accessibility.PUBLIC ||  m.isCompilerGenerated() || m.isConstructor() ||  m.isClassConstructor()) 
							continue;
				
					if(m.isStatic()){
						for(Parameter p : parameterList){
							if(!m.canBeMovedTo(p.getType()))
								continue;
							else {
								float criterion = 0;
								if(m.countNoInClassCallers() != 0)
									criterion = (float) m.countNoCallersInType(p.getType()) / m.countNoInClassCallers();
								else
									criterion = m.countNoCallersInType(p.getType());

								
								int[] methodParams = {
										m.countNoTotalCallers(),
										m.countNoInClassCallers(),
										m.countInHierarchyCallers(),
										m.countInPckageCallers(),
										m.countNoOverrides(),
										m.getNoParameters(),
										type.getNoFields(),
										type.getNoMethods()
								};
								
								set.add(new Performer(type.getName(), m.getSignature(), p.getType().getName(), criterion, 1, methodParams));
							}
						}
					}
				}
			}
		}
	}

	public static class Performer implements Action {
		private String typeName;
		private String methodName;
		private String newOwnerTypeName;
		private float criterion;
		private float threshold;
		private int[] params;

		public Performer(String typeName, String methodName, String newOwnerTypeName, float criterion, float threshold, int[] params) {
			this.typeName = typeName;
			this.methodName = methodName;
			this.newOwnerTypeName = newOwnerTypeName;
			this.criterion = criterion;
			this.threshold = threshold;
			this.params = params;
		}

		@Override
		public void perform(Design d) {
			Method m = d.getType(typeName).getMethod(methodName);
			Type t = d.getType(newOwnerTypeName);
			
			if (m == null) {
				Log.severe("[InstantiateMethod] Can not find method: %s.", methodName);
				return;
			}
		
			m.setOwnerType(t);
			
			for(Parameter p : m.getParameters()){
				if(p.getType() == t){
					m.removeParameter(p);
					break;
				}
			}
		}

		@Override
		public String toString() {
			return String.format("[Instantiate Method] Move static method '%s' of '%s' to its parameter type '%s'", methodName,typeName,newOwnerTypeName);
		}
		
		@Override
		public int getType() {
			return ActionType.METHOD_LEVEL;
		}
		
		@Override
		public int getId() {
//			if(criterion<threshold)
//				return ActionId.InsM_t1;
//			else
//				return ActionId.InsM_t2;
			return ActionId.InsM_t1;
		}
		
		@Override
		public int[] getParams() {
			return params;
		}
	}
}
