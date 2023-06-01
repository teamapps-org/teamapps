package org.teamapps.projector.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Mojo(name = "sync-pom-version",
		defaultPhase = LifecyclePhase.INITIALIZE)
public class SyncPomVersionMojo extends AbstractMojo {

	@Component
	private MavenProject mavenProject;

	@Parameter(defaultValue = "${project.basedir}/package.json")
	private File packageJsonFile;

	public void execute() throws MojoExecutionException {
		String version = mavenProject.getVersion();
		String json;
		try {
			json = Files.readString(packageJsonFile.toPath());
		} catch (IOException e) {
			throw new MojoExecutionException("Could not read package.json", e);
		}
		String newJson = json.replaceAll("(\"version\"\\s*:\\s*)\"[^\"]*\"", "$1\"" + version + "\"");
		try {
			Files.writeString(packageJsonFile.toPath(), newJson);
		} catch (IOException e) {
			throw new MojoExecutionException("Could not write package.json", e);
		}
	}

}
