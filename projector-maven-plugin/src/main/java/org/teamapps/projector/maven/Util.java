package org.teamapps.projector.maven;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Util {

	public static void checkMavenVersion(String versionString, Log log) throws MojoExecutionException {
		String[] parts = versionString.split("\\.");
		int[] versionParts = new int[3];
		for (int i = 0; i < Math.min(parts.length, versionParts.length); i++) {
			versionParts[i] = Integer.parseInt(parts[i]);
		}
		if (versionParts[0] * 1_000_000 + versionParts[1] * 1000 + versionParts[2] < 3_003_009) {
			String message = "Maven version needs to be at least 3.3.9 for projector-maven-plugin to run! Your version is " + versionString;
			log.error(message);
			throw new MojoExecutionException(message);
		}
	}


	public static List<Path> extractDtoDependencies(MavenProject mavenProject, List<String> dtoDependencies) throws MojoExecutionException {
		ArrayList<Path> dtoTmpDirs = new ArrayList<>();

		for (String dtoDependencyString : dtoDependencies) {
			String[] parts = dtoDependencyString.split(":");
			if (parts.length != 3) {
				throw new MojoExecutionException("dtoDependencies must be written in the form group.id:artifact-id:version");
			}
			String groupId = parts[0];
			String artifactId = parts[1];
			String version = parts[2];

			Artifact artifact = mavenProject.getArtifacts().stream()
					.filter(a -> a.getGroupId().equals(groupId) && a.getArtifactId().equals(artifactId) && a.getVersion().equals(version))
					.findFirst()
					.orElseThrow(() -> new MojoExecutionException("Could not find artifact: " + dtoDependencyString));

			Path tmpDir = null;
			try {
				tmpDir = Files.createTempDirectory("projector-plugin-dto");
			} catch (IOException e) {
				throw new MojoExecutionException("Could not create temp dir.", e);
			}
			extractSubDir(artifact.getFile().toPath(), tmpDir);
			dtoTmpDirs.add(tmpDir);
		}
		return dtoTmpDirs;
	}

	private static void extractSubDir(Path jarFile, Path targetDir) throws MojoExecutionException {
		boolean exists = Files.exists(jarFile);
		if (!exists) {
			throw new MojoExecutionException("Could not find file: " + jarFile + ". Are you sure it is added to the dependencies?");
		}
		try (FileSystem zipFs = FileSystems.newFileSystem(URI.create("jar:file:" + jarFile.toAbsolutePath()), new HashMap<>());) {
			Path pathInZip = zipFs.getPath("/", "projector-dto");
			Files.walkFileTree(pathInZip, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
					Path relativePathInZip = pathInZip.relativize(filePath);
					Path targetPath = targetDir.resolve(relativePathInZip.toString());
					Files.createDirectories(targetPath.getParent());
					Files.copy(filePath, targetPath);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			throw new MojoExecutionException("Could not find dto files inside jar file " + jarFile, e);
		}
	}

}
