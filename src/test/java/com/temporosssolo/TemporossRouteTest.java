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

		updateAndAssert(route, snapshot(26, 100, false, false), RouteStage.LOAD_26);
		updateAndAssert(route, snapshot(0, 100, false, false), RouteStage.CATCH_27_FIRST);
		updateAndAssert(route, snapshot(27, 100, false, false), RouteStage.LOAD_27_FIRST);
		updateAndAssert(route, snapshot(0, 100, false, false), RouteStage.ATTACK_FIRST);
		updateAndAssert(route, snapshot(0, 75, true, false), RouteStage.ATTACK_FIRST);
		updateAndAssert(route, snapshot(0, 70, false, false), RouteStage.CATCH_27_SECOND);
		updateAndAssert(route, snapshot(27, 70, false, false), RouteStage.LOAD_THREE);
		updateAndAssert(route, snapshot(24, 70, true, false), RouteStage.ATTACK_TO_TEN);
		updateAndAssert(route, snapshot(24, 10, true, false), RouteStage.LOAD_REMAINDER);
		updateAndAssert(route, snapshot(0, 10, false, false), RouteStage.CATCH_27_FINAL);
		updateAndAssert(route, snapshot(27, 10, false, false), RouteStage.LOAD_FINAL);
		updateAndAssert(route, snapshot(0, 10, true, false), RouteStage.KILL_TEMPOROSS);
		updateAndAssert(route, snapshot(0, 0, false, true), RouteStage.COMPLETE);
	}

	@Test
	public void progressesThroughFishingAndFirstAttack()
	{
		TemporossRoute route = new TemporossRoute();
		assertEquals(RouteStage.CATCH_26, route.getStage());

		route.update(snapshot(26, 100, false, false));
		assertEquals(RouteStage.LOAD_26, route.getStage());

		route.update(snapshot(0, 100, false, false));
		assertEquals(RouteStage.CATCH_27_FIRST, route.getStage());

		route.update(snapshot(27, 100, false, false));
		assertEquals(RouteStage.LOAD_27_FIRST, route.getStage());

		route.update(snapshot(0, 100, false, false));
		assertEquals(RouteStage.ATTACK_FIRST, route.getStage());

		route.update(snapshot(0, 80, true, false));
		assertEquals(RouteStage.ATTACK_FIRST, route.getStage());

		route.update(snapshot(0, 70, false, false));
		assertEquals(RouteStage.CATCH_27_SECOND, route.getStage());
	}

	@Test
	public void firstCatchUsesAvailableInventoryCapacity()
	{
		assertEquals(25, TemporossRoute.getFirstFishTarget(snapshotWithFreeSlots(0, 25)));
		assertEquals(26, TemporossRoute.getFirstFishTarget(snapshotWithFreeSlots(0, 26)));

		TemporossRoute twentySixRoute = new TemporossRoute();
		twentySixRoute.update(snapshotWithFreeSlots(25, 1));
		assertEquals(RouteStage.CATCH_26, twentySixRoute.getStage());
		twentySixRoute.update(snapshotWithFreeSlots(26, 0));
		assertEquals(RouteStage.LOAD_26, twentySixRoute.getStage());

		TemporossRoute twentyFiveRoute = new TemporossRoute();
		twentyFiveRoute.update(snapshotWithFreeSlots(24, 1));
		assertEquals(RouteStage.CATCH_26, twentyFiveRoute.getStage());
		twentyFiveRoute.update(snapshotWithFreeSlots(25, 0));
		assertEquals(RouteStage.LOAD_26, twentyFiveRoute.getStage());

		TemporossRoute fullAtTwentyFour = new TemporossRoute();
		fullAtTwentyFour.update(snapshotWithFreeSlots(24, 0));
		assertEquals(RouteStage.CATCH_26, fullAtTwentyFour.getStage());
	}

	@Test
	public void waitsForThreeFishAndTenPercentBreakpoints()
	{
		TemporossRoute route = new TemporossRoute();
		while (route.getStage() != RouteStage.LOAD_THREE)
		{
			route.next();
		}

		route.update(snapshot(25, 50, true, false));
		assertEquals(RouteStage.LOAD_THREE, route.getStage());
		route.update(snapshot(24, 50, true, false));
		assertEquals(RouteStage.ATTACK_TO_TEN, route.getStage());

		route.update(snapshot(24, 11, true, false));
		assertEquals(RouteStage.ATTACK_TO_TEN, route.getStage());
		route.update(snapshot(24, 10, true, false));
		assertEquals(RouteStage.LOAD_REMAINDER, route.getStage());
	}

	@Test
	public void recoversWhenMoreThanThreeFishAreLoaded()
	{
		TemporossRoute route = new TemporossRoute();
		while (route.getStage() != RouteStage.LOAD_THREE)
		{
			route.next();
		}

		route.update(snapshot(23, 50, true, false));
		assertEquals(RouteStage.ATTACK_TO_TEN, route.getStage());
	}

	@Test
	public void finishesAfterVictory()
	{
		TemporossRoute route = new TemporossRoute();
		while (route.getStage() != RouteStage.KILL_TEMPOROSS)
		{
			route.next();
		}

		route.update(snapshot(0, 0, false, false));
		assertEquals(RouteStage.KILL_TEMPOROSS, route.getStage());
		route.update(snapshot(0, 0, false, true));
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
		Integer essence,
		boolean poolActive,
		boolean defeated)
	{
		return new RouteSnapshot(
			fish,
			Math.max(0, TemporossRoute.FIRST_FISH_TARGET - fish),
			essence,
			poolActive,
			defeated);
	}

	private static RouteSnapshot snapshotWithFreeSlots(int fish, int freeInventorySlots)
	{
		return new RouteSnapshot(fish, freeInventorySlots, 100, false, false);
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
