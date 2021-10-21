package org.teamapps.ux.session;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.teamapps.uisession.QualifiedUiSessionId;
import org.teamapps.ux.resource.FileResource;

import java.io.File;

public class SessionContextResourceManagerTest {

	@Test
	public void shouldCache() {
		SessionContextResourceManager manager = new SessionContextResourceManager(new QualifiedUiSessionId("http1", "ui1"));

		String resourceLink1 = manager.createResourceLink(new FileResource(new File("asdf.b")), null);
		String resourceLink2 = manager.createResourceLink(new FileResource(new File("asdf.b")), null);
		String resourceLink3 = manager.createResourceLink(new FileResource(new File("222.b")), null);

		Assertions.assertThat(resourceLink1).isEqualTo(resourceLink2);
		Assertions.assertThat(resourceLink3).isNotEqualTo(resourceLink2);
	}

	@Test
	public void shouldCacheUsingUniqueIdentifier() {
		SessionContextResourceManager manager = new SessionContextResourceManager(new QualifiedUiSessionId("http1", "ui1"));

		String resourceLink1 = manager.createResourceLink(new FileResource(new File("asdf.b")), "uid1");
		String resourceLink2 = manager.createResourceLink(new FileResource(new File("999.b")), "uid1");
		String resourceLink3 = manager.createResourceLink(new FileResource(new File("asdf.b")), "uuid2");

		Assertions.assertThat(resourceLink1).isEqualTo(resourceLink2);
		Assertions.assertThat(resourceLink3).isNotEqualTo(resourceLink1);
	}

}