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


public class IncreaseFieldSecurityPackage2Private {
	public static class Checker implements ActionChecker {

		@Override
		public void findPossibleActions(Design design, Set<Action> set) {
			List<Type> types = design.getTypes();
			
			for (Type t : types) {
				field:
				for (Field f : t.getFields()) {
					// Turns out, Java compiler binds accesses to constants (static final) in compile time
					// So, a constant does not reflect the access characteristics of a field in bytecode.
					if (f.isCompilerGenerated() || f.isConstant() ||  f.getAccess() != Accessibility.PACKAGE) continue;
					
					Accessibility newaccess = Accessibility.PRIVATE;
					
					for (Method m : f.getAccessors()) {
						if (!m.canAccess(f, newaccess))
							break field;
					}
					
					int[] fieldParams = {
							f.countNoTotalUse(),
							f.countNoInHierarchyUse(),
							f.countNoInPackageUse(),
							f.countNoInClassUse(),
							t.getNoFields(),
							t.getNoMethods()
					};
					
					set.add(new Performer(t.getName(), f.getName(), newaccess, fieldParams));
				}
			}
		}
		
	}
	
	public static class Performer implements Action {
		private String typeName;
		private String fieldName;
		private Accessibility newAccess;
		private int[] params;

		public Performer(String typeName, String fieldName, Accessibility newaccess, int[] params) {
			this.typeName = typeName;
			this.fieldName = fieldName;
			this.newAccess = newaccess;
			this.params = params;
		}

		@Override
		public void perform(Design d) {
			Field f = d.getType(typeName).getField(fieldName);
			f.setAccess(newAccess);
			printToFile(String.format("[Increase Field Security from Package to Private] Field total_use: '%s'  in-class use: '%s'  in-hierarchy use: '%s'  in-package use: '%s'", f.countNoTotalUse(), f.countNoInClassUse(), f.countNoInHierarchyUse(), f.countNoInPackageUse()));
		}
		
		@Override
		public String toString() {
			return String.format("[Increase Field Security from Package to Private] '%s' of '%s'", fieldName, typeName);
		}
		
		@Override
		public int getType() {
			return ActionType.FIELD_LEVEL;
		}
		
		@Override
		public int getId() {
			return ActionId.IFS_Package2Private_t1;
		}
		
		private void printToFile(String output){
			String filePath = "./data/FieldSecurity.txt";

			List<String> lines = Arrays.asList(output);
			Path file = Paths.get(filePath);
			try {
				Files.write(file, lines, StandardOpenOption.APPEND);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public int[] getParams() {
			return params;
		}
	}
}
