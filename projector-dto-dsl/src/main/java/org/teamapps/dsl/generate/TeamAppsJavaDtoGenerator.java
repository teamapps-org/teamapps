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
import org.teamapps.dsl.generate.wrapper.ClassWrapper;
import org.teamapps.dsl.generate.wrapper.EnumWrapper;
import org.teamapps.dsl.generate.wrapper.InterfaceWrapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;

import static org.teamapps.dsl.generate.ErrorMessageUtil.runWithExceptionMessagePrefix;

public class TeamAppsJavaDtoGenerator {
	private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final STGroupFile stGroup;
	private final TeamAppsIntermediateDtoModel model;

	public static void main(String[] args) throws Exception {
		Options options = new Options();
		options.addOption(Option.builder()
				.option("i")
				.longOpt("import")
				.hasArg(true)
				.desc("Additional directory with model files to include")
				.build());
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);

		String[] importedModelDirs = cmd.getOptionValues('i');
		TeamAppsIntermediateDtoModel importedModel = null;
		if (importedModelDirs != null) {
			for (String importedModelDir : importedModelDirs) {
				try {
					if (importedModel != null) {
						importedModel = new TeamAppsIntermediateDtoModel(TeamAppsGeneratorUtil.parseClassCollections(new File(importedModelDir)), importedModelDir.toString(), importedModel);
					} else {
						importedModel = new TeamAppsIntermediateDtoModel(TeamAppsGeneratorUtil.parseClassCollections(new File(importedModelDir)), importedModelDir.toString());
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}

		if (cmd.getArgs().length < 2) {
			new HelpFormatter().printHelp("generator", options);
			System.exit(1);
		}

		File sourceDir = new File(cmd.getArgs()[0]);
		File targetDir = new File(cmd.getArgs()[1]);


		System.out.println("Generating Java from " + sourceDir.getAbsolutePath() + " to " + targetDir.getAbsolutePath() + " with imported models from: " + Arrays.toString(importedModelDirs));


		TeamAppsIntermediateDtoModel model;
		if (importedModel != null) {
			model = new TeamAppsIntermediateDtoModel(TeamAppsGeneratorUtil.parseClassCollections(sourceDir), sourceDir.toString(), importedModel);
		} else {
			model = new TeamAppsIntermediateDtoModel(TeamAppsGeneratorUtil.parseClassCollections(sourceDir), sourceDir.toString());
		}
		new TeamAppsDtoModelValidator(model).validate();
		new TeamAppsJavaDtoGenerator(model).generate(targetDir);
	}

	public TeamAppsJavaDtoGenerator(TeamAppsIntermediateDtoModel model) throws IOException {
		this.model = model;
		stGroup = StGroupFactory.createStGroup("/org/teamapps/dsl/TeamAppsJavaDtoGenerator.stg", this.model);
	}

	public void generate(File targetDir) throws IOException {
		FileUtils.deleteDirectory(targetDir);
		for (ClassWrapper classWrapper : model.getOwnClassDeclarations()) {
			File packageDir = FileUtils.createDirectory(new File(targetDir, classWrapper.getPackageName().replace('.', '/')));
			if (classWrapper.getParserRuleContext().notGeneratedAnnotation() != null) {
				System.out.println("Skipping @NotGenerated class: " + classWrapper.getName());
				continue;
			}
			System.out.println("Generating class: " + classWrapper.getName());
			generateClass(classWrapper, new FileWriter(new File(packageDir, classWrapper.getName() + ".java")));
			generateClassJsonWrapper(classWrapper, new FileWriter(new File(packageDir, classWrapper.getName() + "Wrapper.java")));
			generateClientObjectChannel(classWrapper, new FileWriter(new File(packageDir, classWrapper.getName() + "ClientObjectChannel.java")));
			if (classWrapper.getAllSuperTypes(false).stream().anyMatch(t -> t.getQualifiedName().equals("org.teamapps.projector.dto.DtoClientObject"))) {
				generateClientObjectEventMethodInvoker(classWrapper, new FileWriter(new File(packageDir, classWrapper.getName() + "EventMethodInvoker.java")));
				generateClientObjectEventHandlerInterface(classWrapper, new FileWriter(new File(packageDir, classWrapper.getName() + "EventHandler.java")));
			}
		}
		for (InterfaceWrapper interfaceWrapper : model.getOwnInterfaceDeclarations()) {
			if (interfaceWrapper.isExternal()) {
				continue;
			}
			File packageDir = FileUtils.createDirectory(new File(targetDir, interfaceWrapper.getPackageName().replace('.', '/')));
			if (interfaceWrapper.getParserRuleContext().notGeneratedAnnotation() != null) {
				System.out.println("Skipping @NotGenerated interface: " + interfaceWrapper.getName());
				continue;
			}
			logger.info("Generating interface: " + interfaceWrapper.getName());
			generateInterface(interfaceWrapper, new FileWriter(new File(packageDir, interfaceWrapper.getName() + ".java")));
			generateInterfaceJsonWrapper(interfaceWrapper, new FileWriter(new File(packageDir, interfaceWrapper.getName() + "Wrapper.java")));
		}
		for (EnumWrapper enumWrapper : model.getOwnEnumDeclarations()) {
			File packageDir = FileUtils.createDirectory(new File(targetDir, enumWrapper.getPackageName().replace('.', '/')));
			if (enumWrapper.getParserRuleContext().notGeneratedAnnotation() != null) {
				System.out.println("Skipping @NotGenerated enum: " + enumWrapper.getName());
				continue;
			}
			generateEnum(enumWrapper, new FileWriter(new File(packageDir, enumWrapper.getName() + ".java")));
		}
	}

	void generateClassJsonWrapper(ClassWrapper clazzContext, Writer writer) throws IOException {
		runWithExceptionMessagePrefix(() -> {
			ST template = stGroup.getInstanceOf("jsonWrapper")
					.add("c", clazzContext);
			AutoIndentWriter out = new AutoIndentWriter(writer);
			template.write(out, new StringTemplatesErrorListener());
			writer.close();
		}, "Error while generating JsonWrapper for " + clazzContext.getName());
	}

	void generateClientObjectChannel(ClassWrapper classContext, Writer writer) throws IOException {
		System.out.println("Generating ClientObjectChannel " + classContext.getName());
		runWithExceptionMessagePrefix(() -> {
			ST template = stGroup.getInstanceOf("clientObjectChannel")
					.add("c", classContext);
			AutoIndentWriter out = new AutoIndentWriter(writer);
			template.write(out, new StringTemplatesErrorListener());
			writer.close();
		}, "Error while generating ClientObjectChannel for " + classContext.getName());
	}

	void generateClientObjectEventMethodInvoker(ClassWrapper classContext, Writer writer) throws IOException {
		System.out.println("Generating ClientObjectEventMethodInvoker " + classContext.getName());
		runWithExceptionMessagePrefix(() -> {
			ST template = stGroup.getInstanceOf("eventMethodInvoker")
					.add("c", classContext);
			AutoIndentWriter out = new AutoIndentWriter(writer);
			template.write(out, new StringTemplatesErrorListener());
			writer.close();
		}, "Error while generating ClientObjectEventMethodInvoker for " + classContext.getName());
	}

	void generateClientObjectEventHandlerInterface(ClassWrapper classContext, Writer writer) throws IOException {
		System.out.println("Generating event handler interface " + classContext.getName());
		runWithExceptionMessagePrefix(() -> {
			ST template = stGroup.getInstanceOf("eventHandlerInterface")
					.add("c", classContext);
			AutoIndentWriter out = new AutoIndentWriter(writer);
			template.write(out, new StringTemplatesErrorListener());
			writer.close();
		}, "Error while generating event handler interface for " + classContext.getName());
	}

	void generateInterfaceJsonWrapper(InterfaceWrapper interfaceWrapper, Writer writer) throws IOException {
		runWithExceptionMessagePrefix(() -> {
			ST template = stGroup.getInstanceOf("jsonWrapper")
					.add("c", interfaceWrapper);
			AutoIndentWriter out = new AutoIndentWriter(writer);
			template.write(out, new StringTemplatesErrorListener());
			writer.close();
		}, "Error while generating JsonWrapper for " + interfaceWrapper.getName());
	}                                                                                                          

	void generateClass(ClassWrapper clazzContext, Writer writer) throws IOException {
		runWithExceptionMessagePrefix(() -> {
			ST template = stGroup.getInstanceOf("class")
					.add("c", clazzContext);
			AutoIndentWriter out = new AutoIndentWriter(writer);
			template.write(out, new StringTemplatesErrorListener());
			writer.close();
		}, "Error while generating class " + clazzContext.getName());
	}

	void generateInterface(InterfaceWrapper interfaceWrapper, Writer writer) throws IOException {
		runWithExceptionMessagePrefix(() -> {
			ST template = stGroup.getInstanceOf("interface")
					.add("c", interfaceWrapper);
			AutoIndentWriter out = new AutoIndentWriter(writer);
			template.write(out, new StringTemplatesErrorListener());
			writer.close();
		}, "Error while generating interface " + interfaceWrapper.getName());
	}

	void generateEnum(EnumWrapper enumWrapper, Writer writer) throws IOException {
		runWithExceptionMessagePrefix(() -> {
			ST template = stGroup.getInstanceOf("enumClass")
					.add("e", enumWrapper);
			AutoIndentWriter out = new AutoIndentWriter(writer);
			template.write(out, new StringTemplatesErrorListener());
			writer.close();
		}, "Error while generating enum " + enumWrapper.getName());
	}

}