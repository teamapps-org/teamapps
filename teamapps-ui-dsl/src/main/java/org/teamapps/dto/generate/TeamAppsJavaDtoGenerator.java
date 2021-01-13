/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
import java.util.ArrayList;
import java.util.List;

public class TeamAppsJavaDtoGenerator {
    private final static Logger logger = LoggerFactory.getLogger(TeamAppsJavaDtoGenerator.class);

    private final STGroupFile stGroup;
    private final String packageName;
    private final TeamAppsDtoModel model;

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Usage: sourceDir targetDir packageName");
            System.exit(1);
        }

        File sourceDir = new File(args[0]);
        File targetDir = new File(args[1]);
        String packageName = args[2];

        List<TeamAppsDtoParser.ClassCollectionContext> classCollections = TeamAppsGeneratorUtil.parseClassCollections(sourceDir);
        new TeamAppsJavaDtoGenerator(packageName, new TeamAppsDtoModel(classCollections)).generate(targetDir);
    }

    public TeamAppsJavaDtoGenerator(TeamAppsDtoModel model) throws IOException {
        this("org.teamapps.dto", model);
    }

    public TeamAppsJavaDtoGenerator(String packageName, TeamAppsDtoModel model) throws IOException {
        this.packageName = packageName;
        this.model = model;
        stGroup = StGroupFactory.createStGroup("/org/teamapps/dto/TeamAppsJavaDtoGenerator.stg", this.model);
    }

    public void generate(File targetDir) throws IOException {
        FileUtils.deleteDirectory(targetDir);
        File parentDir = FileUtils.createDirectory(new File(targetDir, packageName.replace('.', '/')));

        for (TeamAppsDtoParser.ClassDeclarationContext clazzContext : model.getClassDeclarations()) {
            logger.info("Generating class: " + clazzContext.Identifier());
            generateClass(clazzContext, new FileWriter(new File(parentDir, clazzContext.Identifier() + ".java")));
            if (model.isReferenceableBaseClass(clazzContext)) {
                generateClassReference(clazzContext, new FileWriter(new File(parentDir, clazzContext.Identifier() + "Reference.java")));
            }
        }
        for (TeamAppsDtoParser.InterfaceDeclarationContext interfaceContext : model.getInterfaceDeclarations()) {
            logger.info("Generating interface: " + interfaceContext.Identifier());
            generateInterface(interfaceContext, new FileWriter(new File(parentDir, interfaceContext.Identifier() + ".java")));
        }
        for (TeamAppsDtoParser.EnumDeclarationContext enumContext : model.getEnumDeclarations()) {
            generateEnum(enumContext, new FileWriter(new File(parentDir, enumContext.Identifier() + ".java")));
        }
        generateObjectTypeEnum(new FileWriter(new File(parentDir, "UiObjectType.java")));
        generateUiObjectBaseClass(new FileWriter(new File(parentDir, "UiObject.java")));

        generateComponentEventBaseClass(new FileWriter(new File(parentDir, "UiEvent.java")));
        generateSubEventBaseClass(new FileWriter(new File(parentDir, "UiSubEvent.java")));
        generateEventEnum(new FileWriter(new File(parentDir, "UiEventType.java")));
        generateSubEventEnum(new FileWriter(new File(parentDir, "UiSubEventType.java")));
        generateComponentCommandBaseClass(new FileWriter(new File(parentDir, "UiCommand.java")));
        generateSubCommandBaseClass(new FileWriter(new File(parentDir, "UiSubCommand.java")));
        generateJacksonTypeIdMaps(new FileWriter(new File(parentDir, "UiObjectJacksonTypeIdMaps.java")));
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

    void generateComponentEventBaseClass(Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("uiEventBaseClass")
                .add("package", packageName)
                .add("allEventDeclarations", model.getEventDeclarations());
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

    void generateSubEventBaseClass(Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("uiSubEventBaseClass")
                .add("package", packageName)
                .add("allEventDeclarations", model.getSubEventDeclarations());
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

    void generateComponentCommandBaseClass(Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("uiCommandBaseClass")
                .add("package", packageName)
                .add("allCommandDeclarations", model.getCommandDeclarations());
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

    void generateSubCommandBaseClass(Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("uiSubCommandBaseClass")
                .add("package", packageName)
                .add("allCommandDeclarations", model.getSubCommandDeclarations());
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

    void generateObjectTypeEnum(Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("uiObjectTypeEnum")
                .add("package", packageName)
                .add("allClasses", model.getClassDeclarations());
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

    void generateUiObjectBaseClass(Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("uiObjectBaseClass")
                .add("package", packageName)
                .add("allClasses", model.getClassDeclarations());
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

    void generateEventEnum(Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("uiEventEnum")
                .add("package", packageName)
                .add("eventKind", "Event")
                .add("allEventDeclarations", model.getEventDeclarations());
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

    void generateSubEventEnum(Writer writer) throws IOException {
        ST template = stGroup.getInstanceOf("uiEventEnum")
                .add("package", packageName)
                .add("eventKind", "SubEvent")
                .add("allEventDeclarations", model.getSubEventDeclarations());
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

    void generateJacksonTypeIdMaps(Writer writer) throws IOException {
        ArrayList<Object> allJsonSerializableClasses = new ArrayList<>();
        allJsonSerializableClasses.addAll(model.getClassDeclarations());
        allJsonSerializableClasses.addAll(model.getInterfaceDeclarations());
        allJsonSerializableClasses.addAll(model.getCommandDeclarations());
        allJsonSerializableClasses.addAll(model.getSubCommandDeclarations());
        allJsonSerializableClasses.addAll(model.getEventDeclarations());
        allJsonSerializableClasses.addAll(model.getSubEventDeclarations());
        ST template = stGroup.getInstanceOf("jacksonTypeIdMaps")
                .add("package", packageName)
                .add("allJsonSerializableClasses", allJsonSerializableClasses);
        AutoIndentWriter out = new AutoIndentWriter(writer);
        template.write(out, new StringTemplatesErrorListener());
        writer.close();
    }

}
