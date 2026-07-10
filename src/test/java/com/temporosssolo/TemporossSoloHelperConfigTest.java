package com.temporosssolo;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TemporossSoloHelperConfigTest
{
	@Test
	public void defaultsToNoCookingMaxXp()
	{
		TemporossSoloHelperConfig config = new TemporossSoloHelperConfig()
		{
		};

		assertEquals(TemporossMethod.NO_COOKING_MAX_XP, config.method());
	}
}
