package org.teamapps.projector.maven;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.teamapps.projector.maven.Util.checkMavenVersion;
import static org.teamapps.projector.maven.Util.extractDtoDependencies;
import static org.twdata.maven.mojoexecutor.MojoExecutor.*;


@Mojo(name = "generate-typescript-dtos",
		defaultPhase = LifecyclePhase.GENERATE_SOURCES,
		requiresDependencyResolution = ResolutionScope.COMPILE)
public class GenerateTypeScriptDtosMojo extends AbstractMojo {

	@Component
	private MavenProject mavenProject;

	@Component
	private MavenSession mavenSession;

	@Component
	private BuildPluginManager pluginManager;

	@Parameter(defaultValue = "${maven.version}", readonly = true)
	private String mavenVersion;

	@Parameter(defaultValue = "${project.basedir}/src/main/dto")
	private String dtoDir;

	@Parameter(defaultValue = "${project.basedir}/src/main/ts/generated")
	private String typeScriptTargetDir;

	@Parameter(required = true, defaultValue = "${projector.version}")
	private String projectorVersion;

	@Parameter
	private List<String> dtoDependencies;

	public void execute() throws MojoExecutionException {
		checkMavenVersion(mavenVersion, getLog());
		executeModelGenerator();
	}

	private void executeModelGenerator() throws MojoExecutionException {
		getLog().info("Generating DTOs.");

		List<Path> dtoDependencyPaths = dtoDependencies != null ? extractDtoDependencies(mavenProject, dtoDependencies) : List.of();
		String commandlineArgs = dtoDependencyPaths.stream().map(p -> "-i \"" + p.toAbsolutePath() + "\"").collect(Collectors.joining(" "))
								 + " \"" + dtoDir + "\" \"" + typeScriptTargetDir + "\"";

		getLog().debug("commandlineArgs: " + commandlineArgs);
		
		executeMojo(
				plugin(
						groupId("org.codehaus.mojo"),
						artifactId("exec-maven-plugin"),
						version("3.1.0"),
						List.of(dependency("org.teamapps", "teamapps-ui-dsl", projectorVersion))
				),
				goal("java"),
				configuration(
						element(name("mainClass"), "org.teamapps.dsl.generate.TeamAppsTypeScriptGenerator"),
						element(name("commandlineArgs"), commandlineArgs)
				),
				executionEnvironment(
						mavenSession,
						pluginManager
				)
		);
	}

}
