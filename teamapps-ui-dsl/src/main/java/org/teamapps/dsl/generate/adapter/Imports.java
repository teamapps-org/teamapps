package org.teamapps.dsl.generate.adapter;

import org.teamapps.dsl.generate.TeamAppsGeneratorException;

import java.util.*;

public class Imports {

	private final Map<String, Import> imports = new LinkedHashMap<>();

	public Imports() {
	}

	public void add(String name, String jsModuleName, String javaPackageName) {
		add(new Import(name, jsModuleName, javaPackageName));
	}

	public void add(Import newImport) {
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
