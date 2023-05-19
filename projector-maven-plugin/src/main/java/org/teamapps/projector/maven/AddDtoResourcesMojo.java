package org.teamapps.projector.maven;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;


@Mojo(name = "add-dto-resources",
		defaultPhase = LifecyclePhase.INITIALIZE)
public class AddDtoResourcesMojo extends AbstractMojo {

	@Component
	private MavenProject mavenProject;

	@Parameter(defaultValue = "${project.basedir}/src/main/dto")
	private String dtoDir;

	public void execute() throws MojoExecutionException {
		Resource resource = new Resource();
		resource.setDirectory(dtoDir);
		resource.setTargetPath("projector-dto");
		resource.addInclude("**/*");
		mavenProject.addResource(resource);
	}

}
