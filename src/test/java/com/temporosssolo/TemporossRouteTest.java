package com.temporosssolo;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TemporossRouteTest
{
	@Test
	public void followsCompleteCatch26Route()
	{
		TemporossRoute route = new TemporossRoute();
		assertEquals(RouteStage.CATCH_26, route.getStage());

		updateAndAssert(route, snapshot(26, 0, 1, 100, false, false), RouteStage.LOAD_26);
		updateAndAssert(route, snapshot(0, 1, 0, 100, false, false), RouteStage.TAKE_FIVE_BUCKETS);
		updateAndAssert(route, snapshot(0, 6, 0, 100, false, false), RouteStage.HUMIDIFY_BUCKETS);
		updateAndAssert(route, snapshot(0, 0, 6, 100, false, false), RouteStage.DOUSE_FIRES);
		updateAndAssert(route, snapshot(0, 6, 0, 100, false, false), RouteStage.DROP_BUCKETS);
		updateAndAssert(route, snapshot(0, 0, 0, 100, false, false), RouteStage.CATCH_27_FIRST);
		updateAndAssert(route, snapshot(27, 0, 0, 100, false, false), RouteStage.LOAD_27_FIRST);
		updateAndAssert(route, snapshot(0, 0, 0, 100, false, false), RouteStage.ATTACK_FIRST);
		updateAndAssert(route, snapshot(0, 0, 0, 75, true, false), RouteStage.ATTACK_FIRST);
		updateAndAssert(route, snapshot(0, 0, 0, 70, false, false), RouteStage.CATCH_27_SECOND);
		updateAndAssert(route, snapshot(27, 0, 0, 70, false, false), RouteStage.LOAD_FIVE);
		updateAndAssert(route, snapshot(22, 0, 0, 70, true, false), RouteStage.ATTACK_TO_TWELVE);
		updateAndAssert(route, snapshot(22, 0, 0, 12, true, false), RouteStage.LOAD_REMAINDER);
		updateAndAssert(route, snapshot(0, 0, 0, 12, false, false), RouteStage.CATCH_27_FINAL);
		updateAndAssert(route, snapshot(27, 0, 0, 12, false, false), RouteStage.LOAD_FINAL);
		updateAndAssert(route, snapshot(0, 0, 0, 12, true, false), RouteStage.KILL_TEMPOROSS);
		updateAndAssert(route, snapshot(0, 0, 0, 0, false, true), RouteStage.TAKE_ONE_BUCKET);
		updateAndAssert(route, snapshot(0, 1, 0, 0, false, true), RouteStage.COMPLETE);
	}

	@Test
	public void progressesThroughFishingBucketsAndFirstAttack()
	{
		TemporossRoute route = new TemporossRoute();
		assertEquals(RouteStage.CATCH_26, route.getStage());

		route.update(snapshot(26, 0, 1, 100, false, false));
		assertEquals(RouteStage.LOAD_26, route.getStage());

		route.update(snapshot(0, 1, 0, 100, false, false));
		assertEquals(RouteStage.TAKE_FIVE_BUCKETS, route.getStage());

		route.update(snapshot(0, 6, 0, 100, false, false));
		assertEquals(RouteStage.HUMIDIFY_BUCKETS, route.getStage());

		route.update(snapshot(0, 0, 6, 100, false, false));
		assertEquals(RouteStage.DOUSE_FIRES, route.getStage());

		route.update(snapshot(0, 6, 0, 100, false, false));
		assertEquals(RouteStage.DROP_BUCKETS, route.getStage());

		route.update(snapshot(0, 0, 0, 100, false, false));
		assertEquals(RouteStage.CATCH_27_FIRST, route.getStage());

		route.update(snapshot(27, 0, 0, 100, false, false));
		assertEquals(RouteStage.LOAD_27_FIRST, route.getStage());

		route.update(snapshot(0, 0, 0, 100, false, false));
		assertEquals(RouteStage.ATTACK_FIRST, route.getStage());

		route.update(snapshot(0, 0, 0, 80, true, false));
		assertEquals(RouteStage.ATTACK_FIRST, route.getStage());

		route.update(snapshot(0, 0, 0, 70, false, false));
		assertEquals(RouteStage.CATCH_27_SECOND, route.getStage());
	}

	@Test
	public void waitsForFiveFishAndTwelvePercentBreakpoints()
	{
		TemporossRoute route = new TemporossRoute();
		while (route.getStage() != RouteStage.LOAD_FIVE)
		{
			route.next();
		}

		route.update(snapshot(23, 0, 0, 50, true, false));
		assertEquals(RouteStage.LOAD_FIVE, route.getStage());
		route.update(snapshot(21, 0, 0, 50, true, false));
		assertEquals(RouteStage.LOAD_FIVE, route.getStage());
		route.update(snapshot(22, 0, 0, 50, true, false));
		assertEquals(RouteStage.ATTACK_TO_TWELVE, route.getStage());

		route.update(snapshot(22, 0, 0, 13, true, false));
		assertEquals(RouteStage.ATTACK_TO_TWELVE, route.getStage());
		route.update(snapshot(22, 0, 0, 12, true, false));
		assertEquals(RouteStage.LOAD_REMAINDER, route.getStage());
	}

	@Test
	public void finishesAfterVictoryAndOneBucket()
	{
		TemporossRoute route = new TemporossRoute();
		while (route.getStage() != RouteStage.KILL_TEMPOROSS)
		{
			route.next();
		}

		route.update(snapshot(0, 0, 0, 0, false, false));
		assertEquals(RouteStage.KILL_TEMPOROSS, route.getStage());
		route.update(snapshot(0, 0, 0, 0, false, true));
		assertEquals(RouteStage.TAKE_ONE_BUCKET, route.getStage());
		route.update(snapshot(0, 1, 0, 0, false, true));
		assertEquals(RouteStage.COMPLETE, route.getStage());
	}

	@Test
	public void manualNavigationStopsAtBounds()
	{
		TemporossRoute route = new TemporossRoute();
		route.previous();
		assertEquals(RouteStage.CATCH_26, route.getStage());

		for (int i = 0; i < RouteStage.values().length + 2; i++)
		{
			route.next();
		}
		assertEquals(RouteStage.COMPLETE, route.getStage());
	}

	private static RouteSnapshot snapshot(
		int fish,
		int emptyBuckets,
		int waterBuckets,
		Integer essence,
		boolean poolActive,
		boolean defeated)
	{
		return new RouteSnapshot(fish, emptyBuckets, waterBuckets, essence, poolActive, defeated);
	}

	private static void updateAndAssert(
		TemporossRoute route,
		RouteSnapshot snapshot,
		RouteStage expectedStage)
	{
		route.update(snapshot);
		assertEquals(expectedStage, route.getStage());
	}
}
