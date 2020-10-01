/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.dto.generate;

import org.antlr.v4.runtime.ParserRuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.AutoIndentWriter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;
import org.teamapps.dto.TeamAppsDtoParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TeamAppsTypeScriptGenerator {
	private final static Logger logger = LoggerFactory.getLogger(TeamAppsTypeScriptGenerator.class);

    private final STGroupFile stGroup;
    private final TeamAppsDtoModel model;

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage: sourceDir targetDir");
            System.exit(1);
        }

        File sourceDir = new File(args[0]);
        File targetDir = new File(args[1]);

        List<TeamAppsDtoParser.ClassCollectionContext> classCollections = TeamAppsGeneratorUtil.getFilesInDirectory(sourceDir).stream()
                .map(dtoFile -> {
                    try {
                        InputStreamReader reader = new InputStreamReader(new FileInputStream(dtoFile), StandardCharsets.UTF_8);
                        return ParserFactory.createParser(reader).classCollection();
                    } catch (Exception e1) {
                        throw new IllegalArgumentException(e1);
                    }
                })
                .collect(Collectors.toList());
        new TeamAppsTypeScriptGenerator(new TeamAppsDtoModel(classCollections)).generate(targetDir);
    }

    public TeamAppsTypeScriptGenerator(TeamAppsDtoModel model) {
        this.model = model;
        stGroup = StGroupFactory.createStGroup("/org/teamapps/dto/TeamAppsTypeScriptGenerator.stg", this.model);
    }

    public void generate(File targetDir) throws IOException {
        FileUtils.deleteDirectory(targetDir);
        File parentDir = FileUtils.createDirectory(targetDir);

        for (TeamAppsDtoParser.EnumDeclarationContext enumContext : model.getEnumDeclarations()) {
            generateEnum(enumContext, new FileWriter(new File(parentDir, enumContext.Identifier() + ".ts")));
        }
        for (TeamAppsDtoParser.InterfaceDeclarationContext interfaceContext : model.getInterfaceDeclarations()) {
            logger.info("Generating typescript definitions for interface: " + interfaceContext.Identifier());
            generateInterfaceDefinition(interfaceContext, new FileWriter(new File(parentDir, interfaceContext.Identifier() + "Config.ts")));
        }
        for (TeamAppsDtoParser.ClassDeclarationContext clazzContext : model.getClassDeclarations()) {
            logger.info("Generating typescript definitions for class: " + clazzContext.Identifier());
            generateClassDefinition(clazzContext, new FileWriter(new File(parentDir, clazzContext.Identifier() + "Config.ts")));
        }

        generateCommandBaseDefinition(new FileWriter(new File(parentDir, "UiCommand.ts")));
        generateCommandExecutor(model.getCommandDeclarations(), new FileWriter(new File(parentDir, "CommandExecutor.ts")));
        generateEventRegistrator(model.getAllClassesAndInterfacesWithEvents(), new FileWriter(new File(parentDir, "EventRegistrator.ts")));

        generateEventBaseDefinition(model.getEventDeclarations(), new FileWriter(new File(parentDir, "UiEvent.ts")));

//	    generateSubCommandBaseDefinition(new FileWriter(new File(parentDir, "UiSubCommand.ts")));
//	    generateSubCommandExecutor(model.getSubCommandDeclarations(), new FileWriter(new File(parentDir, "SubCommandExecutor.ts")));

//        generateSubEventBaseDefinition(model.getSubEventDeclarations(), new FileWriter(new File(parentDir, "UiSubEvent.ts")));
//        generateSubEventRegistrator(model.getClassesAndInterfacesReferencedForSubEvents(), new FileWriter(new File(parentDir, "SubEventRegistrator.ts")));
    }

	public void generateEnum(TeamAppsDtoParser.EnumDeclarationContext enumContext, Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("enum")
                .add("e", enumContext);
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

    public void generateClassDefinition(TeamAppsDtoParser.ClassDeclarationContext clazzContext, Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("classConfigDefinition")
                .add("c", clazzContext);
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

    public void generateInterfaceDefinition(TeamAppsDtoParser.InterfaceDeclarationContext interfaceContext, Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("interfaceConfigDefinition")
                .add("c", interfaceContext);
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

    public void generateCommandExecutor(List<TeamAppsDtoParser.CommandDeclarationContext> commandDeclarationContexts, Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("commandExecutor")
                .add("name", "CommandExecutor")
                .add("nonStaticCommands", commandDeclarationContexts.stream().filter(c -> c.staticModifier() == null).collect(Collectors.toList()))
                .add("staticCommands", commandDeclarationContexts.stream().filter(c -> c.staticModifier() != null).collect(Collectors.toList()));
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

	public void generateSubCommandBaseDefinition(Writer writer) throws IOException {
		ST template = stGroup.getInstanceOf("uiSubCommandBaseDefinition");
		AutoIndentWriter out = new AutoIndentWriter(writer);
		template.write(out, new StringTemplatesErrorListener());
		writer.close();
	}

    public void generateSubCommandExecutor(List<TeamAppsDtoParser.SubCommandDeclarationContext> commandDeclarationContexts, Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("commandExecutor")
                .add("name", "SubCommandExecutor")
                .add("nonStaticCommands", commandDeclarationContexts)
                .add("staticCommands", Collections.emptyList());
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

    public void generateCommandBaseDefinition(Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("uiCommandBaseDefinition");
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

    public void generateEventBaseDefinition(List<TeamAppsDtoParser.EventDeclarationContext> eventDeclarations, Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("uiEventBaseDefinition")
                .add("eventDeclarations", eventDeclarations);
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

    public void generateSubEventBaseDefinition(List<TeamAppsDtoParser.SubEventDeclarationContext> subEventDeclarations, Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("uiSubEventBaseDefinition")
                .add("subEventDeclarations", subEventDeclarations);
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

    public void generateEventRegistrator(List<ParserRuleContext> classAndInterfaceContexts, Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("eventRegistrator")
		        .add("classesAndInterfacesWithEvents", classAndInterfaceContexts);
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

    public void generateSubEventRegistrator(List<ParserRuleContext> classAndInterfaceContexts, Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("subEventRegistrator")
		        .add("classesAndInterfacesReferencedForSubEvents", classAndInterfaceContexts);
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

}
