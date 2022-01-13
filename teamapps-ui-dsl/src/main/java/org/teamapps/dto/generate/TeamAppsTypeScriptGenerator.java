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
package org.teamapps.dto.generate;

import org.antlr.v4.runtime.ParserRuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.AutoIndentWriter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;
import org.teamapps.dto.TeamAppsDtoParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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

        System.out.println("Generating TypeScript from " + sourceDir.getAbsolutePath() + " to " + targetDir.getAbsolutePath());

        new TeamAppsTypeScriptGenerator(new TeamAppsDtoModel(TeamAppsGeneratorUtil.parseClassCollections(sourceDir)))
                .generate(targetDir);
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

        generateEventBaseDefinition(model.getEventDeclarations(), new FileWriter(new File(parentDir, "UiEvent.ts")));
        generateEventRegistrator(model.getAllClassesAndInterfacesWithEvents(), new FileWriter(new File(parentDir, "ComponentEventDescriptors.ts")));

        generateQueryBaseDefinition(model.getEventDeclarations(), new FileWriter(new File(parentDir, "UiQuery.ts")));
        generateQueryFunctionAdder(model.getAllClassesAndInterfacesWithQueries(), new FileWriter(new File(parentDir, "QueryFunctionAdder.ts")));
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

    public void generateCommandBaseDefinition(Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("uiCommandBaseDefinition");
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

    public void generateEventBaseDefinition(List<TeamAppsDtoParser.EventDeclarationContext> eventDeclarations, Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("uiEventBaseDefinition");
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

    public void generateQueryBaseDefinition(List<TeamAppsDtoParser.EventDeclarationContext> eventDeclarations, Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("uiQueryBaseDefinition");
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

    public void generateEventRegistrator(List<ParserRuleContext> classAndInterfaceContexts, Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("componentEventDescriptors")
		        .add("classesAndInterfacesWithEvents", classAndInterfaceContexts);
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

    public void generateQueryFunctionAdder(List<ParserRuleContext> classAndInterfaceContexts, Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("queryFunctionAdder")
		        .add("classesAndInterfacesWithQueries", classAndInterfaceContexts);
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

}
