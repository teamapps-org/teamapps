package org.teamapps.dsl.generate.wrapper;

import org.teamapps.dsl.generate.DtoGeneratorException;

import java.util.*;

public class Imports {

	private final Map<String, Import> imports = new LinkedHashMap<>();
	private final String baseJsModuleName;

	public Imports(String baseJsModuleName) {
		this.baseJsModuleName = baseJsModuleName;
	}

	public void addImport(String name, String jsModuleName, String javaPackageName) {
		addImport(name, jsModuleName, "./" + name, javaPackageName);
	}

	public void addImport(String name, String jsModuleName, String localRelativePath, String javaPackageName) {
		jsModuleName = this.baseJsModuleName.equals(jsModuleName) ? localRelativePath : jsModuleName;
		Import newImport = new Import(name, jsModuleName, javaPackageName);
		Import existingImport = imports.get(newImport.name());
		if (existingImport != null
				&& (!Objects.equals(newImport.jsModuleName(), existingImport.jsModuleName()) || !Objects.equals(newImport.javaPackageName(), existingImport.javaPackageName()))) {
			throw new DtoGeneratorException("Inconsistent concurring imports: " + existingImport + ", " + newImport);
		}
		imports.putIfAbsent(newImport.name(), newImport);
	}

	public boolean contains(String className) {
		return imports.containsKey(className);
	}

	public Collection<Import> getAll() {
		return imports.values();
	}

}
