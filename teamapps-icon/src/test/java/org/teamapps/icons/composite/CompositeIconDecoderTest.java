package org.teamapps.icons.composite;

import org.junit.Assert;
import org.junit.Test;
import org.teamapps.icon.TestIcon;

public class CompositeIconDecoderTest {

	@Test
	public void testDecode() throws Exception {
		CompositeIcon compositeIcon = new CompositeIconDecoder().decodeIcon("0(x)3(x)", qualifiedEncodedIcon -> TestIcon.A);
		Assert.assertEquals(TestIcon.A, compositeIcon.getBaseIcon());
		Assert.assertEquals(TestIcon.A, compositeIcon.getTopLeftIcon());
		Assert.assertNull(compositeIcon.getBottomRightIcon());
		Assert.assertNull(compositeIcon.getBottomLeftIcon());
		Assert.assertNull(compositeIcon.getTopRightIcon());
	}

}