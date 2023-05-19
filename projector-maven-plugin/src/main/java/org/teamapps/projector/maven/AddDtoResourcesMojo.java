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
		defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class AddDtoResourcesMojo extends AbstractMojo {

	@Component
	private MavenProject mavenProject;

	@Parameter(defaultValue = "${project.build.directory}/js-dist")
	private String javaScriptDistDir;

	@Parameter
	private String jsResourcesTargetClassPath;

	public void execute() throws MojoExecutionException {
		if (jsResourcesTargetClassPath == null || jsResourcesTargetClassPath.isEmpty()) {
			throw new MojoExecutionException("Please specify jsResourcesTargetClassPath");
		}

		Resource resource = new Resource();
		resource.setDirectory(javaScriptDistDir);
		resource.setTargetPath(jsResourcesTargetClassPath);
		resource.addInclude("**/*");
		mavenProject.addResource(resource);
	}

}
