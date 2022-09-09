/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.dsl.generate;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.AutoIndentWriter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;
import org.teamapps.dsl.TeamAppsDtoParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class TeamAppsJavaDtoGenerator {
	private final static Logger logger = LoggerFactory.getLogger(TeamAppsJavaDtoGenerator.class);

	private final STGroupFile stGroup;
	private final String packageName;
	private final TeamAppsIntermediateDtoModel model;

	public static void main(String[] args) throws IOException, ParseException {
		Options options = new Options();
		options.addOption(Option.builder()
				.option("p")
				.longOpt("package")
				.hasArg(true)
				.required(true)
				.desc("Package name for generated classes")
				.build());
		options.addOption(Option.builder()
						.option("i")
						.longOpt("import")
						.hasArg(true)
						.desc("Additional directory with model files to include")
						.build());
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);

		String packageName = cmd.getOptionValue('p');

		TeamAppsIntermediateDtoModel[] importedModels = Optional.ofNullable(cmd.getOptionValues('i')).stream().flatMap(Arrays::stream)
				.map(dir -> {
					try {
						return new TeamAppsIntermediateDtoModel(TeamAppsGeneratorUtil.parseClassCollections(new File(dir)));
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}).toArray(TeamAppsIntermediateDtoModel[]::new);


		if (cmd.getArgs().length < 2) {
			new HelpFormatter().printHelp("generator", options);
			System.exit(1);
		}

		File sourceDir = new File(cmd.getArgs()[0]);
		File targetDir = new File(cmd.getArgs()[1]);


		System.out.println("Generating Java from " + sourceDir.getAbsolutePath() + " to " + targetDir.getAbsolutePath() + " with package name " + packageName);


		TeamAppsIntermediateDtoModel model = new TeamAppsIntermediateDtoModel(TeamAppsGeneratorUtil.parseClassCollections(sourceDir), importedModels);
		new TeamAppsDtoModelValidator(model).validate();
		new TeamAppsJavaDtoGenerator(packageName, model)
				.generate(targetDir);
	}

	public TeamAppsJavaDtoGenerator(TeamAppsIntermediateDtoModel model) throws IOException {
		this("org.teamapps.dto", model);
	}

	public TeamAppsJavaDtoGenerator(String packageName, TeamAppsIntermediateDtoModel model) throws IOException {
		this.packageName = packageName;
		this.model = model;
		stGroup = StGroupFactory.createStGroup("/org/teamapps/dsl/TeamAppsJavaDtoGenerator.stg", this.model);
	}

	public void generate(File targetDir) throws IOException {
		FileUtils.deleteDirectory(targetDir);
		File parentDir = FileUtils.createDirectory(new File(targetDir, packageName.replace('.', '/')));

		for (TeamAppsDtoParser.ClassDeclarationContext clazzContext : model.getOwnClassDeclarations()) {
			System.out.println("Generating class: " + clazzContext.Identifier());
			generateClass(clazzContext, new FileWriter(new File(parentDir, clazzContext.Identifier() + ".java")));
			generateClassJsonWrapper(clazzContext, new FileWriter(new File(parentDir, clazzContext.Identifier() + "Wrapper.java")));
			if (model.isReferenceableBaseClass(clazzContext)) {
				generateClassReference(clazzContext, new FileWriter(new File(parentDir, clazzContext.Identifier() + "Reference.java")));
			}
		}
		for (TeamAppsDtoParser.InterfaceDeclarationContext interfaceContext : model.getOwnInterfaceDeclarations()) {
			logger.info("Generating interface: " + interfaceContext.Identifier());
			generateInterface(interfaceContext, new FileWriter(new File(parentDir, interfaceContext.Identifier() + ".java")));
			generateInterfaceJsonWrapper(interfaceContext, new FileWriter(new File(parentDir, interfaceContext.Identifier() + "Wrapper.java")));
		}
		for (TeamAppsDtoParser.EnumDeclarationContext enumContext : model.getOwnEnumDeclarations()) {
			generateEnum(enumContext, new FileWriter(new File(parentDir, enumContext.Identifier() + ".java")));
		}
		generateJacksonTypeIdMaps(new FileWriter(new File(parentDir, "UiObjectJacksonTypeIdMaps.java")));
	}

	private void generateClassJsonWrapper(TeamAppsDtoParser.ClassDeclarationContext clazzContext, Writer writer) throws IOException {
		ST template = stGroup.getInstanceOf("jsonWrapper")
				.add("package", packageName)
				.add("c", clazzContext);
		AutoIndentWriter out = new AutoIndentWriter(writer);
		template.write(out, new StringTemplatesErrorListener());
		writer.close();
	}

	private void generateInterfaceJsonWrapper(TeamAppsDtoParser.InterfaceDeclarationContext interfaceContext, Writer writer) throws IOException {
		ST template = stGroup.getInstanceOf("jsonWrapper")
				.add("package", packageName)
				.add("c", interfaceContext);
		AutoIndentWriter out = new AutoIndentWriter(writer);
		template.write(out, new StringTemplatesErrorListener());
		writer.close();
	}

	private void generateEventJsonWrapper(TeamAppsDtoParser.EventDeclarationContext interfaceContext, Writer writer) throws IOException {
		ST template = stGroup.getInstanceOf("jsonWrapper")
				.add("package", packageName)
				.add("c", interfaceContext);
		AutoIndentWriter out = new AutoIndentWriter(writer);
		template.write(out, new StringTemplatesErrorListener());
		writer.close();
	}

	void generateClass(TeamAppsDtoParser.ClassDeclarationContext clazzContext, Writer writer) throws IOException {
		ST template = stGroup.getInstanceOf("class")
				.add("package", packageName)
				.add("c", clazzContext);
		AutoIndentWriter out = new AutoIndentWriter(writer);
		template.write(out, new StringTemplatesErrorListener());
		writer.close();
	}

	void generateClassReference(TeamAppsDtoParser.ClassDeclarationContext clazzContext, Writer writer) throws IOException {
		ST template = stGroup.getInstanceOf("classReference")
				.add("package", packageName)
				.add("c", clazzContext);
		AutoIndentWriter out = new AutoIndentWriter(writer);
		template.write(out, new StringTemplatesErrorListener());
		writer.close();
	}

	void generateInterface(TeamAppsDtoParser.InterfaceDeclarationContext interfaceContext, Writer writer) throws IOException {
		ST template = stGroup.getInstanceOf("interface")
				.add("package", packageName)
				.add("i", interfaceContext);
		AutoIndentWriter out = new AutoIndentWriter(writer);
		template.write(out, new StringTemplatesErrorListener());
		writer.close();
	}

	void generateEnum(TeamAppsDtoParser.EnumDeclarationContext enumContext, Writer writer) throws IOException {
		ST template = stGroup.getInstanceOf("enumClass")
				.add("package", packageName)
				.add("e", enumContext);
		AutoIndentWriter out = new AutoIndentWriter(writer);
		template.write(out, new StringTemplatesErrorListener());
		writer.close();
	}

	void generateJacksonTypeIdMaps(Writer writer) throws IOException {
		ArrayList<Object> allJsonSerializableClasses = new ArrayList<>();
		allJsonSerializableClasses.addAll(model.getOwnClassDeclarations());
		allJsonSerializableClasses.addAll(model.getOwnInterfaceDeclarations());
		allJsonSerializableClasses.addAll(model.getOwnCommandDeclarations());
		allJsonSerializableClasses.addAll(model.getOwnEventDeclarations());
		allJsonSerializableClasses.addAll(model.getOwnQueryDeclarations());
		ST template = stGroup.getInstanceOf("jacksonTypeIdMaps")
				.add("package", packageName)
				.add("allJsonSerializableClasses", allJsonSerializableClasses);
		AutoIndentWriter out = new AutoIndentWriter(writer);
		template.write(out, new StringTemplatesErrorListener());
		writer.close();
	}

}
