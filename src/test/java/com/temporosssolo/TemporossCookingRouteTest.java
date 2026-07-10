package com.temporosssolo;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TemporossCookingRouteTest
{
	@Test
	public void followsCompleteCookingRouteIncludingSplitFinalInventory()
	{
		TemporossRoute route = new TemporossRoute(TemporossMethod.COOKING_MIX_XP_PERMITS);
		assertEquals(RouteStage.MIX_CATCH_OPENING, route.getStage());

		updateAndAssert(route, snapshot(8, 0, 0, 19, 100, false, false, false),
			RouteStage.MIX_COOK_OPENING);
		updateAndAssert(route, snapshot(0, 8, 0, 19, 100, false, false, false),
			RouteStage.MIX_CATCH_TO_17);
		updateAndAssert(route, snapshot(9, 8, 0, 10, 100, false, false, false),
			RouteStage.MIX_COOK_TO_17);
		updateAndAssert(route, snapshot(0, 17, 0, 10, 100, false, false, false),
			RouteStage.MIX_LOAD_17);
		updateAndAssert(route, loadingEmpty(), RouteStage.MIX_CATCH_19_FIRST);

		updateAndAssert(route, snapshot(19, 0, 0, 8, 100, false, false, false),
			RouteStage.MIX_COOK_19_FIRST);
		updateAndAssert(route, snapshot(0, 19, 0, 8, 100, false, false, false),
			RouteStage.MIX_LOAD_19_FIRST);
		updateAndAssert(route, loadingEmpty(), RouteStage.MIX_ATTACK_FIRST);
		updateAndAssert(route, snapshot(0, 0, 0, 27, 70, false, true, false),
			RouteStage.MIX_ATTACK_FIRST);
		updateAndAssert(route, snapshot(0, 0, 0, 27, 70, false, false, false),
			RouteStage.MIX_CATCH_19_SECOND);

		updateAndAssert(route, snapshot(19, 0, 0, 8, 70, false, false, false),
			RouteStage.MIX_COOK_19_SECOND);
		updateAndAssert(route, snapshot(0, 19, 0, 8, 70, false, false, false),
			RouteStage.MIX_LOAD_19_SECOND);
		updateAndAssert(route, loadingEmpty(), RouteStage.MIX_ATTACK_SECOND);
		updateAndAssert(route, snapshot(0, 0, 0, 27, 60, false, true, false),
			RouteStage.MIX_ATTACK_SECOND);
		updateAndAssert(route, snapshot(0, 0, 0, 27, 60, false, false, false),
			RouteStage.MIX_CATCH_FINAL);

		updateAndAssert(route, snapshot(27, 0, 0, 0, 60, false, false, false),
			RouteStage.MIX_COOK_FINAL);
		updateAndAssert(route, snapshot(0, 27, 0, 0, 60, false, false, false),
			RouteStage.MIX_LOAD_FINAL);
		updateAndAssert(route, loadingEmpty(), RouteStage.MIX_CATCH_FINAL);
		assertEquals(27, route.getFinalBatchLoaded());

		updateAndAssert(route, snapshot(1, 0, 0, 26, 60, false, false, false),
			RouteStage.MIX_COOK_FINAL);
		updateAndAssert(route, snapshot(0, 1, 0, 26, 60, false, false, false),
			RouteStage.MIX_LOAD_FINAL);
		updateAndAssert(route, loadingEmpty(), RouteStage.MIX_KILL_TEMPOROSS);
		updateAndAssert(route, snapshot(0, 0, 0, 27, 0, false, false, true),
			RouteStage.MIX_COMPLETE);
	}

	@Test
	public void doubleSpotInterruptsOpeningCookToResumeFishing()
	{
		TemporossRoute route = routeAt(RouteStage.MIX_COOK_OPENING);

		route.update(snapshot(4, 4, 0, 19, 100, true, false, false));
		assertEquals(RouteStage.MIX_CATCH_TO_17, route.getStage());
	}

	@Test
	public void crystallisedFishDoesNotCountTowardMixedMethodTargets()
	{
		TemporossRoute route = new TemporossRoute(TemporossMethod.COOKING_MIX_XP_PERMITS);
		route.update(snapshot(7, 0, 1, 19, 100, false, false, false));
		assertEquals(RouteStage.MIX_CATCH_OPENING, route.getStage());

		route.update(snapshot(8, 0, 1, 18, 100, false, false, false));
		assertEquals(RouteStage.MIX_COOK_OPENING, route.getStage());
		route.update(snapshot(0, 7, 1, 19, 100, false, false, false));
		assertEquals(RouteStage.MIX_COOK_OPENING, route.getStage());
		route.update(snapshot(0, 8, 1, 18, 100, false, false, false));
		assertEquals(RouteStage.MIX_CATCH_TO_17, route.getStage());

		route.update(snapshot(13, 0, 4, 11, 100, false, false, false));
		assertEquals(RouteStage.MIX_CATCH_TO_17, route.getStage());
		route.update(snapshot(17, 0, 4, 7, 100, false, false, false));
		assertEquals(RouteStage.MIX_COOK_TO_17, route.getStage());
		route.update(snapshot(0, 17, 4, 7, 100, false, false, false));
		assertEquals(RouteStage.MIX_LOAD_17, route.getStage());

		route.update(snapshot(0, 10, 4, 14, 100, false, false, true, false));
		assertEquals(7, route.getPreparedLoadedThisStage());
		route.update(snapshot(0, 0, 4, 24, 100, false, false, true, false));
		assertEquals(RouteStage.MIX_CATCH_19_FIRST, route.getStage());

		TemporossRoute finalRoute = routeAt(RouteStage.MIX_CATCH_FINAL);
		assertEquals(
			26,
			finalRoute.getFinalCycleTarget(
				snapshot(0, 0, 1, 26, 60, false, false, false)));
	}

	@Test
	public void exactPreparedThresholdsDoNotAdvanceOneFishEarly()
	{
		TemporossRoute firstLoad = routeAt(RouteStage.MIX_COOK_TO_17);
		firstLoad.update(snapshot(0, 16, 0, 11, 100, false, false, false));
		assertEquals(RouteStage.MIX_COOK_TO_17, firstLoad.getStage());
		firstLoad.update(snapshot(0, 17, 0, 10, 100, false, false, false));
		assertEquals(RouteStage.MIX_LOAD_17, firstLoad.getStage());

		TemporossRoute standardLoad = routeAt(RouteStage.MIX_COOK_19_FIRST);
		standardLoad.update(snapshot(0, 18, 0, 9, 100, false, false, false));
		assertEquals(RouteStage.MIX_COOK_19_FIRST, standardLoad.getStage());
		standardLoad.update(snapshot(0, 19, 0, 8, 100, false, false, false));
		assertEquals(RouteStage.MIX_LOAD_19_FIRST, standardLoad.getStage());
	}

	@Test
	public void excessCatchPausesTargetUntilExtraFishIsRemoved()
	{
		TemporossRoute route = routeAt(RouteStage.MIX_CATCH_TO_17);
		RouteSnapshot eighteenFish = snapshot(18, 0, 5, 4, 100, false, false, false);

		route.update(eighteenFish);
		assertEquals(RouteStage.MIX_CATCH_TO_17, route.getStage());
		assertEquals(RouteTarget.NONE, route.getTarget(eighteenFish));
		assertEquals(1, route.getExcessFish(eighteenFish));

		RouteSnapshot exactFish = snapshot(17, 0, 4, 7, 100, false, false, false);
		route.update(exactFish);
		assertEquals(RouteStage.MIX_COOK_TO_17, route.getStage());
	}

	@Test
	public void onlyConfirmedHopperLoadingAdvancesPreparedLoad()
	{
		TemporossRoute route = routeAt(RouteStage.MIX_COOK_TO_17);
		route.update(snapshot(0, 17, 4, 7, 100, false, false, false));
		assertEquals(RouteStage.MIX_LOAD_17, route.getStage());

		route.update(snapshot(0, 0, 0, 27, 100, false, false, false));
		assertEquals(RouteStage.MIX_LOAD_17, route.getStage());
		assertEquals(0, route.getPreparedLoadedThisStage());

		route.update(snapshot(0, 0, 0, 27, 100, false, false, true, false));
		assertEquals(RouteStage.MIX_LOAD_17, route.getStage());

		TemporossRoute confirmedRoute = routeAt(RouteStage.MIX_COOK_TO_17);
		confirmedRoute.update(snapshot(0, 17, 4, 7, 100, false, false, false));
		confirmedRoute.update(snapshot(0, 10, 4, 14, 100, false, false, true, false));
		assertEquals(RouteStage.MIX_LOAD_17, confirmedRoute.getStage());
		assertEquals(7, confirmedRoute.getPreparedLoadedThisStage());
		confirmedRoute.update(snapshot(0, 0, 0, 27, 100, false, false, true, false));
		assertEquals(RouteStage.MIX_CATCH_19_FIRST, confirmedRoute.getStage());
	}

	@Test
	public void methodSwitchAndManualNavigationStayWithinSelectedRoute()
	{
		TemporossRoute route = new TemporossRoute();
		route.next();
		assertEquals(RouteStage.LOAD_26, route.getStage());

		route.setMethod(TemporossMethod.COOKING_MIX_XP_PERMITS);
		assertEquals(RouteStage.MIX_CATCH_OPENING, route.getStage());
		assertEquals(1, route.getStageNumber());
		assertEquals(18, route.getStageCount());

		route.previous();
		assertEquals(RouteStage.MIX_CATCH_OPENING, route.getStage());
		for (int i = 0; i < route.getStageCount() + 2; i++)
		{
			route.next();
		}
		assertEquals(RouteStage.MIX_COMPLETE, route.getStage());
		assertEquals("No cooking (max XP)", TemporossMethod.NO_COOKING_MAX_XP.toString());
		assertEquals(
			"Cooking Mix of XP and permits",
			TemporossMethod.COOKING_MIX_XP_PERMITS.toString());
	}

	private static TemporossRoute routeAt(RouteStage target)
	{
		TemporossRoute route = new TemporossRoute(TemporossMethod.COOKING_MIX_XP_PERMITS);
		while (route.getStage() != target)
		{
			route.next();
		}
		return route;
	}

	private static RouteSnapshot loadingEmpty()
	{
		return snapshot(0, 0, 0, 27, 100, false, false, true, false);
	}

	private static RouteSnapshot snapshot(
		int raw,
		int cooked,
		int crystallised,
		int freeSlots,
		Integer essence,
		boolean doubleSpot,
		boolean poolActive,
		boolean defeated)
	{
		return snapshot(
			raw,
			cooked,
			crystallised,
			freeSlots,
			essence,
			doubleSpot,
			poolActive,
			false,
			defeated);
	}

	private static RouteSnapshot snapshot(
		int raw,
		int cooked,
		int crystallised,
		int freeSlots,
		Integer essence,
		boolean doubleSpot,
		boolean poolActive,
		boolean loading,
		boolean defeated)
	{
		return new RouteSnapshot(
			raw,
			cooked,
			crystallised,
			freeSlots,
			essence,
			doubleSpot,
			poolActive,
			loading,
			defeated);
	}

	private static void updateAndAssert(
		TemporossRoute route,
		RouteSnapshot snapshot,
		RouteStage expected)
	{
		route.update(snapshot);
		assertEquals(expected, route.getStage());
	}
}
