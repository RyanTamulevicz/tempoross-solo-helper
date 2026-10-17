package com.temporosssolo;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TemporossMaxPermitsRouteTest
{
	private static final int HOPPER = 101;

	@Test
	public void followsCompleteMaxPermitsRoute()
	{
		TemporossRoute route = new TemporossRoute(TemporossMethod.COOKING_FIREFIGHTING_MAX_PERMITS);
		assertEquals(RouteStage.PERMITS_FISH_17, route.getStage());
		assertEquals(17, route.getStageCount());
		assertEquals(3, route.getTripCount());

		updateAndAssert(route, snapshot(17, 0, 11, 100, false, false, false), RouteStage.PERMITS_COOK_17);
		updateAndAssert(route, snapshot(0, 17, 11, 100, false, false, false), RouteStage.PERMITS_LOAD_17);
		updateAndAssert(route, loading(0, 0), RouteStage.PERMITS_DOUSE_SHIP);
		assertEquals(RouteTarget.DOUSE, route.getTarget(snapshot(0, 0, 22, 4, false, false, false)));
		updateAndAssert(route, snapshot(1, 0, 21, 4, false, false, false), RouteStage.PERMITS_FISH_19_FIRST);

		updateAndAssert(route, snapshot(19, 0, 3, 4, false, false, false), RouteStage.PERMITS_COOK_19_FIRST);
		updateAndAssert(route, snapshot(0, 19, 3, 4, false, false, false), RouteStage.PERMITS_LOAD_19_FIRST);
		updateAndAssert(route, loading(0, 0), RouteStage.PERMITS_ATTACK_FIRST);
		route.update(snapshot(0, 0, 22, 80, false, true, false));
		updateAndAssert(route, snapshot(0, 0, 22, 70, false, false, false), RouteStage.PERMITS_FISH_19_SECOND);

		updateAndAssert(route, snapshot(19, 0, 3, 70, false, false, false), RouteStage.PERMITS_COOK_19_SECOND);
		updateAndAssert(route, snapshot(0, 19, 3, 70, false, false, false), RouteStage.PERMITS_LOAD_19_SECOND);
		updateAndAssert(route, loading(0, 0), RouteStage.PERMITS_ATTACK_SECOND);
		updateAndAssert(route, snapshot(0, 0, 22, 55, false, true, false), RouteStage.PERMITS_ATTACK_SECOND);
		updateAndAssert(route, snapshot(0, 0, 22, 49, false, true, false), RouteStage.PERMITS_FIREFIGHT);
		assertEquals(RouteTarget.DOUSE, route.getTarget(snapshot(0, 0, 0, 49, false, false, false)));

		updateAndAssert(route, snapshot(1, 0, 21, 49, false, false, false), RouteStage.PERMITS_FISH_2);
		updateAndAssert(route, snapshot(2, 0, 20, 49, false, false, false), RouteStage.PERMITS_LOAD_2);
		updateAndAssert(route, loading(0, 1), RouteStage.PERMITS_LOAD_2);
		updateAndAssert(route, loading(0, 0), RouteStage.PERMITS_KILL);
		updateAndAssert(route, snapshot(0, 0, 22, 0, false, false, true), RouteStage.PERMITS_COMPLETE);
		assertEquals(RouteTarget.LEAVE, route.getStage().getTarget());
	}

	@Test
	public void cooksAfterNineAndReturnsToTheDoubleSpot()
	{
		TemporossRoute route = new TemporossRoute(TemporossMethod.COOKING_FIREFIGHTING_MAX_PERMITS);
		RouteSnapshot eightFish = snapshot(8, 0, 15, 100, false, false, false);
		route.update(eightFish);
		assertEquals(RouteTarget.FISH, route.getTarget(eightFish));

		RouteSnapshot nineFish = snapshot(9, 0, 14, 100, false, false, false);
		route.update(nineFish);
		assertEquals(RouteTarget.COOK, route.getTarget(nineFish));

		RouteSnapshot doubleSpot = snapshot(5, 4, 14, 100, true, false, false);
		route.update(doubleSpot);
		assertEquals(RouteTarget.FISH, route.getTarget(doubleSpot));
	}

	@Test
	public void nineteenFishTripsDoNotTakeAnEarlyCookBreak()
	{
		TemporossRoute route = routeAt(RouteStage.PERMITS_FISH_19_FIRST);
		RouteSnapshot tenFish = snapshot(10, 0, 12, 50, false, false, false);
		route.update(tenFish);
		assertEquals(RouteTarget.FISH, route.getTarget(tenFish));

		RouteSnapshot twentyFish = snapshot(20, 0, 2, 50, false, false, false);
		route.update(twentyFish);
		assertEquals(RouteStage.PERMITS_FISH_19_FIRST, route.getStage());
		assertEquals(1, route.getExcessFish(twentyFish));
		assertEquals(RouteTarget.NONE, route.getTarget(twentyFish));
	}

	@Test
	public void stormForcedPartialLoadTracksShortfallUntilTheBatchIsComplete()
	{
		TemporossRoute route = routeAt(RouteStage.PERMITS_COOK_19_FIRST);
		route.update(snapshot(1, 15, 6, 4, false, false, false));
		route.update(snapshot(0, 15, 7, 4, false, false, false));
		assertEquals(RouteStage.PERMITS_COOK_19_FIRST, route.getStage());

		route.update(loading(0, 0));
		assertEquals(RouteStage.PERMITS_LOAD_19_FIRST, route.getStage());
		assertEquals(15, route.getPreparedLoadedThisStage());
		assertEquals(4, route.getRemainingLoad());

		RouteSnapshot empty = snapshot(0, 0, 22, 4, false, false, false);
		route.update(empty);
		assertEquals(RouteTarget.FISH, route.getTarget(empty));

		RouteSnapshot fourRaw = snapshot(4, 0, 18, 4, false, false, false);
		route.update(fourRaw);
		assertEquals(RouteTarget.COOK, route.getTarget(fourRaw));

		RouteSnapshot fourCooked = snapshot(0, 4, 18, 4, false, false, false);
		route.update(fourCooked);
		assertEquals(RouteTarget.LOAD, route.getTarget(fourCooked));
		route.update(loading(0, 0));
		assertEquals(RouteStage.PERMITS_ATTACK_FIRST, route.getStage());
	}

	@Test
	public void earlyLoadFromTheFishingStepIsCounted()
	{
		TemporossRoute route = routeAt(RouteStage.PERMITS_FISH_19_SECOND);
		route.update(snapshot(3, 12, 13, 50, false, false, false));
		route.update(new RouteSnapshot(3, 2, 0, 23, 50, false, false, true, HOPPER, 0, 0, false));
		assertEquals(RouteStage.PERMITS_LOAD_19_SECOND, route.getStage());
		assertEquals(10, route.getPreparedLoadedThisStage());
		assertEquals(9, route.getRemainingLoad());
	}

	@Test
	public void droppingCookedFishOutsideTheCannonIsNotALoad()
	{
		TemporossRoute route = routeAt(RouteStage.PERMITS_COOK_19_FIRST);
		route.update(snapshot(0, 10, 12, 50, false, false, false));
		route.update(snapshot(0, 9, 13, 50, false, false, false));
		assertEquals(RouteStage.PERMITS_COOK_19_FIRST, route.getStage());
	}

	@Test
	public void secondAttackAlsoEndsWhenThePoolCloses()
	{
		TemporossRoute route = routeAt(RouteStage.PERMITS_ATTACK_SECOND);
		route.update(snapshot(0, 0, 22, 70, false, true, false));
		updateAndAssert(route, snapshot(0, 0, 22, 62, false, false, false), RouteStage.PERMITS_FIREFIGHT);
	}

	@Test
	public void finalLoadCountsCookedFishToo()
	{
		TemporossRoute route = routeAt(RouteStage.PERMITS_FISH_2);
		route.update(snapshot(1, 1, 20, 30, false, false, false));
		assertEquals(RouteStage.PERMITS_LOAD_2, route.getStage());
		route.update(new RouteSnapshot(0, 1, 0, 21, 30, false, false, true, HOPPER, 0, 0, false));
		route.update(new RouteSnapshot(0, 0, 0, 22, 30, false, false, true, HOPPER, 0, 0, false));
		assertEquals(RouteStage.PERMITS_KILL, route.getStage());
	}

	@Test
	public void otherMethodsKeepFourTrips()
	{
		assertEquals(4, new TemporossRoute(TemporossMethod.NO_COOKING_MAX_XP).getTripCount());
		assertEquals(4, new TemporossRoute(TemporossMethod.COOKING_MIX_XP_PERMITS).getTripCount());
		assertEquals("Cooking + firefighting (max permits)",
			TemporossMethod.COOKING_FIREFIGHTING_MAX_PERMITS.toString());
	}

	private static TemporossRoute routeAt(RouteStage target)
	{
		TemporossRoute route = new TemporossRoute(TemporossMethod.COOKING_FIREFIGHTING_MAX_PERMITS);
		while (route.getStage() != target)
		{
			route.next();
		}
		return route;
	}

	private static RouteSnapshot loading(int cooked, int raw)
	{
		return new RouteSnapshot(
			raw, cooked, 0, 28 - cooked - raw, 50, false, false, true, HOPPER, 0, 0, false);
	}

	private static RouteSnapshot snapshot(
		int raw,
		int cooked,
		int freeSlots,
		Integer essence,
		boolean doubleSpot,
		boolean poolActive,
		boolean defeated)
	{
		return new RouteSnapshot(raw, cooked, 0, freeSlots, essence, doubleSpot, poolActive, false, defeated);
	}

	private static void updateAndAssert(TemporossRoute route, RouteSnapshot snapshot, RouteStage expected)
	{
		route.update(snapshot);
		assertEquals(expected, route.getStage());
	}
}
