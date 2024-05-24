package org.teamapps.dsl.generate.wrapper;

import org.teamapps.dsl.generate.TeamAppsGeneratorException;

import java.util.*;

public class Imports {

	private final Map<String, Import> imports = new LinkedHashMap<>();

	public Imports() {
	}

	public void addImport(String name, String jsModuleName, String javaPackageName) {
		Import newImport = new Import(name, jsModuleName, javaPackageName);
		Import existingImport = imports.get(newImport.name());
		if (existingImport != null
				&& (!Objects.equals(newImport.jsPackageName(), existingImport.jsPackageName()) || !Objects.equals(newImport.javaPackageName(), existingImport.javaPackageName()))) {
			throw new TeamAppsGeneratorException("Inconsistent concurring imports: " + existingImport + ", " + newImport);
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
