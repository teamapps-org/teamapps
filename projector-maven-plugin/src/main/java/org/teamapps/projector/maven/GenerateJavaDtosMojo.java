package org.teamapps.projector.maven;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

import java.util.List;

import static org.teamapps.projector.maven.Util.checkMavenVersion;
import static org.twdata.maven.mojoexecutor.MojoExecutor.*;


@Mojo(name = "generate-java-dtos",
		defaultPhase = LifecyclePhase.GENERATE_SOURCES,
		requiresDependencyResolution = ResolutionScope.COMPILE)
public class GenerateJavaDtosMojo extends AbstractMojo {

	@Component
	private MavenProject mavenProject;

	@Component
	private MavenSession mavenSession;

	@Component
	private BuildPluginManager pluginManager;

	@Parameter(property = "maven.version", readonly = true)
	private String mavenVersion;

	@Parameter(defaultValue = "${project.basedir}/src/main/dto")
	private String dtoDir;

	@Parameter(defaultValue = "${project.build.directory}/generated-sources/teamapps-dto")
	private String javaTargetDir;

	@Parameter(property = "projector.version")
	private String projectorVersion;

	public void execute() throws MojoExecutionException {
		checkMavenVersion(mavenVersion, getLog());
		executeModelGenerator();

		mavenProject.addCompileSourceRoot(javaTargetDir);
	}

	private void executeModelGenerator() throws MojoExecutionException {
		getLog().info("Generating DTOs");

		if (projectorVersion == null || projectorVersion.isEmpty()) {
			throw new MojoExecutionException("Please specify projectorVersion in the plugin configuration!");
		}

		String commandlineArgs = "-i \"" + mavenProject.getBasedir() + "/../teamapps-client-core/src/main/dto\" "
								 + "-i \"" + mavenProject.getBasedir() + "/../teamapps-client-core-components/src/main/dto\" "
								 + "\"" + dtoDir + "\" \"" + javaTargetDir + "\"";

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
						element(name("mainClass"), "org.teamapps.dsl.generate.TeamAppsJavaDtoGenerator"),
						element(name("commandlineArgs"), commandlineArgs)
				),
				executionEnvironment(
						mavenSession,
						pluginManager
				)
		);
	}

}
