package org.teamapps.dsl.generate.adapter;

import org.teamapps.dsl.generate.TeamAppsGeneratorException;

import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Imports {

	private final Map<String, Import> imports = new HashMap<>();

	public Imports() {
	}

	public void add(String name, String jsModuleName, String javaPackageName) {
		add(new Import(name, jsModuleName, javaPackageName));
	}

	public void add(Import newImport) {
		Import existingImport = imports.get(newImport.name());
		if (existingImport != null
				&& (!Objects.equals(newImport.jsModuleName(), existingImport.jsModuleName()) || !Objects.equals(newImport.javaPackageName(), existingImport.javaPackageName()))) {
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
