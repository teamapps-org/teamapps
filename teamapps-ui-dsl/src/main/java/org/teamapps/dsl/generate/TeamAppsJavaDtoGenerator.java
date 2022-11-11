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


		System.out.println("Generating Java from " + sourceDir.getAbsolutePath() + " to " + targetDir.getAbsolutePath());


		TeamAppsIntermediateDtoModel model = new TeamAppsIntermediateDtoModel(TeamAppsGeneratorUtil.parseClassCollections(sourceDir), importedModels);
		new TeamAppsDtoModelValidator(model).validate();
		new TeamAppsJavaDtoGenerator(model).generate(targetDir);
	}

	public TeamAppsJavaDtoGenerator(TeamAppsIntermediateDtoModel model) throws IOException {
		this.model = model;
		stGroup = StGroupFactory.createStGroup("/org/teamapps/dsl/TeamAppsJavaDtoGenerator.stg", this.model);
	}

	public void generate(File targetDir) throws IOException {
		FileUtils.deleteDirectory(targetDir);
		for (TeamAppsDtoParser.ClassDeclarationContext clazzContext : model.getOwnClassDeclarations()) {
			String packageName = TeamAppsIntermediateDtoModel.getPackageName(clazzContext);
			File packageDir = FileUtils.createDirectory(new File(targetDir, packageName.replace('.', '/')));
			if (clazzContext.notGeneratedAnnotation() != null) {
				System.out.println("Skipping @NotGenerated class: " + clazzContext.Identifier());
				continue;
			}
			System.out.println("Generating class: " + clazzContext.Identifier());
			generateClass(clazzContext, new FileWriter(new File(packageDir, clazzContext.Identifier() + ".java")));
			generateClassJsonWrapper(clazzContext, new FileWriter(new File(packageDir, clazzContext.Identifier() + "Wrapper.java")));
			if (model.isReferenceableBaseClass(clazzContext)) {
				generateClassReference(clazzContext, new FileWriter(new File(packageDir, clazzContext.Identifier() + "Reference.java")));
			}
		}
		for (TeamAppsDtoParser.InterfaceDeclarationContext interfaceContext : model.getOwnInterfaceDeclarations()) {
			String packageName = TeamAppsIntermediateDtoModel.getPackageName(interfaceContext);
			File packageDir = FileUtils.createDirectory(new File(targetDir, packageName.replace('.', '/')));
			if (interfaceContext.notGeneratedAnnotation() != null) {
				System.out.println("Skipping @NotGenerated interface: " + interfaceContext.Identifier());
				continue;
			}
			logger.info("Generating interface: " + interfaceContext.Identifier());
			generateInterface(interfaceContext, new FileWriter(new File(packageDir, interfaceContext.Identifier() + ".java")));
			generateInterfaceJsonWrapper(interfaceContext, new FileWriter(new File(packageDir, interfaceContext.Identifier() + "Wrapper.java")));
		}
		for (TeamAppsDtoParser.EnumDeclarationContext enumContext : model.getOwnEnumDeclarations()) {
			String packageName = TeamAppsIntermediateDtoModel.getPackageName(enumContext);
			File packageDir = FileUtils.createDirectory(new File(targetDir, packageName.replace('.', '/')));
			if (enumContext.notGeneratedAnnotation() != null) {
				System.out.println("Skipping @NotGenerated enum: " + enumContext.Identifier());
				continue;
			}
			generateEnum(enumContext, new FileWriter(new File(packageDir, enumContext.Identifier() + ".java")));
		}
	}

	private void generateClassJsonWrapper(TeamAppsDtoParser.ClassDeclarationContext clazzContext, Writer writer) throws IOException {
		ST template = stGroup.getInstanceOf("jsonWrapper")
				.add("c", clazzContext);
		AutoIndentWriter out = new AutoIndentWriter(writer);
		template.write(out, new StringTemplatesErrorListener());
		writer.close();
	}

	private void generateInterfaceJsonWrapper(TeamAppsDtoParser.InterfaceDeclarationContext interfaceContext, Writer writer) throws IOException {
		ST template = stGroup.getInstanceOf("jsonWrapper")
				.add("c", interfaceContext);
		AutoIndentWriter out = new AutoIndentWriter(writer);
		template.write(out, new StringTemplatesErrorListener());
		writer.close();
	}

	private void generateEventJsonWrapper(TeamAppsDtoParser.EventDeclarationContext interfaceContext, Writer writer) throws IOException {
		ST template = stGroup.getInstanceOf("jsonWrapper")
				.add("c", interfaceContext);
		AutoIndentWriter out = new AutoIndentWriter(writer);
		template.write(out, new StringTemplatesErrorListener());
		writer.close();
	}

	void generateClass(TeamAppsDtoParser.ClassDeclarationContext clazzContext, Writer writer) throws IOException {
		ST template = stGroup.getInstanceOf("class")
				.add("c", clazzContext);
		AutoIndentWriter out = new AutoIndentWriter(writer);
		template.write(out, new StringTemplatesErrorListener());
		writer.close();
	}

	void generateClassReference(TeamAppsDtoParser.ClassDeclarationContext clazzContext, Writer writer) throws IOException {
		ST template = stGroup.getInstanceOf("classReference")
				.add("c", clazzContext);
		AutoIndentWriter out = new AutoIndentWriter(writer);
		template.write(out, new StringTemplatesErrorListener());
		writer.close();
	}

	void generateInterface(TeamAppsDtoParser.InterfaceDeclarationContext interfaceContext, Writer writer) throws IOException {
		ST template = stGroup.getInstanceOf("interface")
				.add("i", interfaceContext);
		AutoIndentWriter out = new AutoIndentWriter(writer);
		template.write(out, new StringTemplatesErrorListener());
		writer.close();
	}

	void generateEnum(TeamAppsDtoParser.EnumDeclarationContext enumContext, Writer writer) throws IOException {
		ST template = stGroup.getInstanceOf("enumClass")
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
				.add("allJsonSerializableClasses", allJsonSerializableClasses);
		AutoIndentWriter out = new AutoIndentWriter(writer);
		template.write(out, new StringTemplatesErrorListener());
		writer.close();
	}

}
