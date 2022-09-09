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
import java.util.Arrays;
import java.util.Optional;

public class TeamAppsTypeScriptGenerator {
	private final static Logger logger = LoggerFactory.getLogger(TeamAppsTypeScriptGenerator.class);

    private final STGroupFile stGroup;
    private final TeamAppsIntermediateDtoModel model;

    public static void main(String[] args) throws IOException, ParseException {
        Options options = new Options();
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

        System.out.println("Generating TypeScript from " + sourceDir.getAbsolutePath() + " to " + targetDir.getAbsolutePath());

        new TeamAppsTypeScriptGenerator(new TeamAppsIntermediateDtoModel(TeamAppsGeneratorUtil.parseClassCollections(sourceDir), importedModels))
                .generate(targetDir);
    }

    public TeamAppsTypeScriptGenerator(TeamAppsIntermediateDtoModel model) {
        this.model = model;
        stGroup = StGroupFactory.createStGroup("/org/teamapps/dsl/TeamAppsTypeScriptGenerator.stg", this.model);
    }

    public void generate(File targetDir) throws IOException {
        FileUtils.deleteDirectory(targetDir);
        File parentDir = FileUtils.createDirectory(targetDir);

        for (TeamAppsDtoParser.EnumDeclarationContext enumContext : model.getOwnEnumDeclarations()) {
            generateEnum(enumContext, new FileWriter(new File(parentDir, enumContext.Identifier() + ".ts")));
        }
        for (TeamAppsDtoParser.InterfaceDeclarationContext interfaceContext : model.getOwnInterfaceDeclarations()) {
            logger.info("Generating typescript definitions for interface: " + interfaceContext.Identifier());
            System.out.println("Generating typescript definitions for interface: " + interfaceContext.Identifier());
            generateInterfaceDefinition(interfaceContext, new FileWriter(new File(parentDir, interfaceContext.Identifier() + "Config.ts")));
        }
        for (TeamAppsDtoParser.ClassDeclarationContext clazzContext : model.getOwnClassDeclarations()) {
            logger.info("Generating typescript definitions for class: " + clazzContext.Identifier());
            generateClassDefinition(clazzContext, new FileWriter(new File(parentDir, clazzContext.Identifier() + "Config.ts")));
        }
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

}
