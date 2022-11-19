package org.teamapps.dsl.generate.adapter;

public record Import(String name, String jsModuleName, String javaPackageName) {

	public String dtoName() {
		return "Dto" + name;
	}

}
