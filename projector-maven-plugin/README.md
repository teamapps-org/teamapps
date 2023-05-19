
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
                <version>1.2</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>clean</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

## License

The Projector maven plugin is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).
