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
package org.teamapps.projector.dsl.generate;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.AutoIndentWriter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;
import org.teamapps.projector.dsl.generate.wrapper.ClassWrapper;
import org.teamapps.projector.dsl.generate.wrapper.EnumWrapper;
import org.teamapps.projector.dsl.generate.wrapper.InterfaceWrapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TypeScriptDtoGenerator {
	private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final STGroupFile stGroup;
    private final IntermediateDtoModel model;

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
		IntermediateDtoModel importedModel = null;
		if (importedModelDirs != null) {
			for (String importedModelDir : importedModelDirs) {
                    try {
					if (importedModel != null) {
						importedModel = new IntermediateDtoModel(DtoGeneratorUtil.parseClassCollections(new File(importedModelDir)), importedModelDir.toString(), importedModel);
					} else {
						importedModel = new IntermediateDtoModel(DtoGeneratorUtil.parseClassCollections(new File(importedModelDir)), importedModelDir.toString());
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

        System.out.println("Generating TypeScript from " + sourceDir.getAbsolutePath() + " to " + targetDir.getAbsolutePath() + " with imported models from: " + Arrays.toString(importedModelDirs));


		IntermediateDtoModel model;
		if (importedModel != null) {
			model = new IntermediateDtoModel(DtoGeneratorUtil.parseClassCollections(sourceDir), sourceDir.toString(), importedModel);
		} else {
			model = new IntermediateDtoModel(DtoGeneratorUtil.parseClassCollections(sourceDir), sourceDir.toString());
		}
		new DtoModelValidator(model).validate();
		new TypeScriptDtoGenerator(model).generate(targetDir);
    }

    public TypeScriptDtoGenerator(IntermediateDtoModel model) {
        this.model = model;
        stGroup = StGroupFactory.createStGroup("/org/teamapps/dsl/TeamAppsTypeScriptGenerator.stg", this.model);
    }

    public void generate(File targetDir) throws IOException {
        FileUtils.deleteDirectory(targetDir);
        File parentDir = FileUtils.createDirectory(targetDir);

        for (EnumWrapper enumContext : model.getOwnEnumDeclarations()) {
            if (enumContext.getParserRuleContext().notGeneratedAnnotation() != null) {
                System.out.println("Skipping @NotGenerated enum: " + enumContext.getName());
                continue;
            }
            generateEnum(enumContext, new FileWriter(new File(parentDir, enumContext.getName() + ".ts")));
        }
        for (InterfaceWrapper interfaceContext : model.getOwnInterfaceDeclarations()) {
            if (interfaceContext.isExternal()
				|| interfaceContext.getParserRuleContext().notGeneratedAnnotation() != null) {
                System.out.println("Skipping @NotGenerated interface: " + interfaceContext.getName());
                continue;
            }
            logger.info("Generating typescript definitions for interface: " + interfaceContext.getName());
            System.out.println("Generating typescript definitions for interface: " + interfaceContext.getName());
            generateInterfaceDefinition(interfaceContext, new FileWriter(new File(parentDir, interfaceContext.getName() + ".ts")));
        }
        for (ClassWrapper classContext : model.getOwnClassDeclarations()) {
            if (classContext.getParserRuleContext().notGeneratedAnnotation() != null) {
                System.out.println("Skipping @NotGenerated class: " + classContext.getName());
                continue;
            }
            logger.info("Generating typescript definitions for class: " + classContext.getName());
            generateClassDefinition(classContext, new FileWriter(new File(parentDir, classContext.getName() + ".ts")));
        }

        generateIndexTs(new FileWriter(new File(parentDir, "index.ts")));
    }

    private void generateIndexTs(Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("indexTs")
                .add("classes", model.getOwnClassDeclarations().stream().filter(c -> c.getParserRuleContext().notGeneratedAnnotation() == null && !c.isExternal()).collect(Collectors.toList()))
                .add("interfaces", model.getOwnInterfaceDeclarations().stream().filter(c -> c.getParserRuleContext().notGeneratedAnnotation() == null && !c.isExternal()).collect(Collectors.toList()))
                .add("enums", model.getOwnEnumDeclarations().stream().filter(e -> e.getParserRuleContext().notGeneratedAnnotation() == null && !e.isExternal()).collect(Collectors.toList()));
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

    public void generateEnum(EnumWrapper enumContext, Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("enum")
                .add("e", enumContext);
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

    public void generateClassDefinition(ClassWrapper clazzContext, Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("classConfigDefinition")
                .add("c", clazzContext);
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

    public void generateInterfaceDefinition(InterfaceWrapper interfaceContext, Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("interfaceConfigDefinition")
                .add("c", interfaceContext);
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

}
