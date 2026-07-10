package com.temporosssolo;

import net.runelite.api.coords.LocalPoint;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TemporossWorkingSideTest
{
	private static final int WORLD_VIEW = 0;
	private static final LocalPoint WEST_SOUTH = point(512, 512);
	private static final LocalPoint WEST_NORTH = point(512, 768);
	private static final LocalPoint EAST_SOUTH = point(2816, 512);
	private static final LocalPoint EAST_NORTH = point(2816, 768);

	@Test
	public void splitsIslandShoreAcrossBothShips()
	{
		TemporossWorkingSide side = standardArena();

		side.observeCandidate(WEST_SOUTH);
		assertFalse(side.isKnown());
		assertFalse(side.contains(point(512, 0)));
		assertTrue(side.confirmCandidate());

		assertTrue(side.contains(point(512, 0)));
		assertTrue(side.contains(point(2816, 0)));
		assertFalse(side.contains(point(512, 1280)));
		assertFalse(side.contains(point(2816, 1280)));
		assertFalse(side.contains(point(1664, 640)));
	}

	@Test
	public void remainsUnknownUntilAllFourCratesAreKnown()
	{
		TemporossWorkingSide side = sideWithCrates(WEST_SOUTH, WEST_NORTH);

		side.observeCandidate(WEST_SOUTH);
		assertFalse(side.confirmCandidate());
		assertFalse(side.contains(point(512, 0)));

		side.rememberCrate(EAST_SOUTH);
		side.rememberCrate(EAST_NORTH);
		assertTrue(side.isKnown());
		assertTrue(side.contains(point(2816, 0)));
	}

	@Test
	public void successfulLoadLocksTheExactShoreUntilReset()
	{
		TemporossWorkingSide side = sideWithCrates(
			EAST_NORTH,
			WEST_SOUTH,
			EAST_SOUTH,
			WEST_NORTH);

		side.observeCandidate(EAST_NORTH);
		assertTrue(side.confirmCandidate());
		side.observeCandidate(WEST_SOUTH);
		assertTrue(side.confirmCandidate());
		assertTrue(side.contains(point(512, 1280)));
		assertTrue(side.contains(point(2816, 1280)));
		assertFalse(side.contains(point(512, 0)));
		assertFalse(side.contains(point(2816, 0)));

		side.reset();
		assertFalse(side.isKnown());
		assertFalse(side.contains(point(2816, 1280)));
	}

	@Test
	public void shoreSplitSurvivesRotationAndTranslation()
	{
		LocalPoint westSouth = point(2000, 1000);
		LocalPoint westNorth = point(1744, 1000);
		LocalPoint eastSouth = point(2000, 3304);
		LocalPoint eastNorth = point(1744, 3304);
		TemporossWorkingSide side = sideWithCrates(
			eastNorth,
			westSouth,
			westNorth,
			eastSouth);

		side.observeCandidate(westSouth);
		assertTrue(side.confirmCandidate());
		assertTrue(side.contains(point(2512, 1000)));
		assertTrue(side.contains(point(2512, 3304)));
		assertFalse(side.contains(point(1232, 1000)));
		assertFalse(side.contains(point(1232, 3304)));
		assertFalse(side.contains(point(1872, 2152)));
	}

	private static TemporossWorkingSide standardArena()
	{
		return sideWithCrates(WEST_SOUTH, WEST_NORTH, EAST_SOUTH, EAST_NORTH);
	}

	private static TemporossWorkingSide sideWithCrates(LocalPoint... crates)
	{
		TemporossWorkingSide side = new TemporossWorkingSide();
		for (LocalPoint crate : crates)
		{
			side.rememberCrate(crate);
		}
		return side;
	}

	private static LocalPoint point(int x, int y)
	{
		return new LocalPoint(x, y, WORLD_VIEW);
	}
}
