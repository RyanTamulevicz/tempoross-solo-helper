package com.temporosssolo;

import java.util.ArrayList;
import java.util.List;
import net.runelite.api.coords.LocalPoint;

final class TemporossWorkingSide
{
	private final List<LocalPoint> ammunitionCrates = new ArrayList<>();
	private LocalPoint candidateCrate;
	private LocalPoint confirmedCrate;
	private LocalPoint oppositeShoreCrate;

	void reset()
	{
		ammunitionCrates.clear();
		candidateCrate = null;
		confirmedCrate = null;
		oppositeShoreCrate = null;
	}

	void rememberCrate(LocalPoint crate)
	{
		if (crate != null && !ammunitionCrates.contains(crate))
		{
			ammunitionCrates.add(crate);
			rebuildConfirmedPair();
		}
	}

	void observeCandidate(LocalPoint crate)
	{
		if (confirmedCrate == null && crate != null)
		{
			candidateCrate = crate;
		}
	}

	LocalPoint getCandidate()
	{
		return candidateCrate;
	}

	boolean confirmCandidate()
	{
		if (confirmedCrate == null)
		{
			confirmedCrate = candidateCrate;
			rebuildConfirmedPair();
		}
		return isKnown();
	}

	boolean isKnown()
	{
		return confirmedCrate != null
			&& oppositeShoreCrate != null
			&& ammunitionCrates.size() >= 4;
	}

	boolean contains(LocalPoint location)
	{
		if (location == null || !isKnown())
		{
			return false;
		}

		// The nearest other hopper is the opposite-shore endpoint on the same
		// ship. Projecting across their midpoint splits the entire arena by island
		// shoreline instead of incorrectly splitting it by left/right ship. A fire
		// exactly on the center line is ignored.
		long shoreAxisX = (long) confirmedCrate.getX() - oppositeShoreCrate.getX();
		long shoreAxisY = (long) confirmedCrate.getY() - oppositeShoreCrate.getY();
		long relativeXTwice = 2L * location.getX() - confirmedCrate.getX() - oppositeShoreCrate.getX();
		long relativeYTwice = 2L * location.getY() - confirmedCrate.getY() - oppositeShoreCrate.getY();
		return relativeXTwice * shoreAxisX + relativeYTwice * shoreAxisY > 0;
	}

	private void rebuildConfirmedPair()
	{
		if (confirmedCrate == null || ammunitionCrates.size() < 4)
		{
			oppositeShoreCrate = null;
			return;
		}

		oppositeShoreCrate = null;
		int oppositeShoreDistance = Integer.MAX_VALUE;
		for (LocalPoint crate : ammunitionCrates)
		{
			if (crate.equals(confirmedCrate))
			{
				continue;
			}

			int distance = confirmedCrate.distanceTo(crate);
			if (distance < oppositeShoreDistance)
			{
				oppositeShoreDistance = distance;
				oppositeShoreCrate = crate;
			}
		}
	}
}
