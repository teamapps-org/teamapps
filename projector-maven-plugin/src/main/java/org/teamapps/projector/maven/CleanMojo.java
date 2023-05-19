package org.teamapps.projector.maven;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;


@Mojo(name = "clean",
		defaultPhase = LifecyclePhase.CLEAN)
public class CleanMojo extends AbstractMojo {

	@Component
	private MavenProject mavenProject;

	@Component
	private MavenSession mavenSession;

	@Component
	private BuildPluginManager pluginManager;

	@Parameter(defaultValue = "${project.basedir}/src/main/ts/generated")
	private String typeScriptGeneratorTargetDir;

	@Parameter(defaultValue = "${project.basedir}/target/dist")
	private String javaScriptDistDir;

	public void execute() throws MojoExecutionException {
		compileModel();
	}

	private void compileModel() throws MojoExecutionException {
		getLog().info("Cleaning: "
					  + "\n\t" + typeScriptGeneratorTargetDir
					  + "\n\t" + javaScriptDistDir
		);

		executeMojo(
				plugin(
						groupId("org.apache.maven.plugins"),
						artifactId("maven-clean-plugin"),
						version("3.2.0")
				),
				goal("clean"),
				configuration(
						element(name("filesets"),
								element("fileset",
										element("directory", typeScriptGeneratorTargetDir),
										element("directory", javaScriptDistDir)
								)
						)
				),
				executionEnvironment(
						mavenSession.clone(),
						pluginManager
				)
		);
	}
}
