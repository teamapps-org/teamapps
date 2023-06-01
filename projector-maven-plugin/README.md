
# Projector maven plugin

Maven plugin for projector custom component development.

## Build configuration

Add this to your pom.xml:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.teamapps</groupId>
            <artifactId>projector-maven-plugin</artifactId>
            <version>[VERSION]</version>
            <executions>
                <execution>
                    <goals>
                        <goal>clean</goal>
                        <goal>sync-pom-version</goal>
                        <goal>generate-java-dtos</goal>
                        <goal>generate-typescript-dtos</goal>
                        <goal>add-js-dist-resources</goal>
                        <goal>add-dto-resources</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <jsResourcesTargetClassPath>org/teamapps/projector/my/component/resources/js</jsResourcesTargetClassPath>
                <dtoDependencies>
                    <dependency>org.teamapps:teamapps-client-core:${project.version}</dependency>
                </dtoDependencies>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## License

The Projector maven plugin is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).
