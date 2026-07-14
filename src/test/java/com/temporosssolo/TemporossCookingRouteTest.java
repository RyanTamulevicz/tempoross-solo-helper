package com.temporosssolo;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TemporossCookingRouteTest
{
	private static final int FIRST_HOPPER = 101;
	private static final int SECOND_HOPPER = 202;

	@Test
	public void followsCompleteSeventeenStepCookingRoute()
	{
		TemporossRoute route = new TemporossRoute(TemporossMethod.COOKING_MIX_XP_PERMITS);
		assertEquals(RouteStage.MIX_FISH_17, route.getStage());
		assertEquals(17, route.getStageCount());

		updateAndAssert(route, snapshot(8, 0, 0, 15, 100, false, false, false),
			RouteStage.MIX_FISH_17);
		assertEquals(RouteTarget.COOK, route.getTarget(snapshot(8, 0, 0, 15, 100, false, false, false)));
		updateAndAssert(route, snapshot(0, 8, 0, 15, 100, false, false, false),
			RouteStage.MIX_FISH_17);
		assertEquals(RouteTarget.FISH, route.getTarget(snapshot(0, 8, 0, 15, 100, false, false, false)));
		updateAndAssert(route, snapshot(9, 8, 0, 6, 100, false, false, false),
			RouteStage.MIX_COOK_17);
		updateAndAssert(route, snapshot(0, 17, 0, 6, 100, false, false, false),
			RouteStage.MIX_LOAD_17);
		updateAndAssert(route, loading(0, FIRST_HOPPER), RouteStage.MIX_FIRES_FIRST);
		assertEquals(RouteTarget.FISH, route.getTarget(snapshot(0, 0, 0, 23, 100, false, false, false)));
		updateAndAssert(route, snapshot(1, 0, 0, 22, 100, false, false, false),
			RouteStage.MIX_FISH_19_FIRST);

		completeFishCookLoad(route, 19, RouteStage.MIX_COOK_19_FIRST,
			RouteStage.MIX_LOAD_19_FIRST, RouteStage.MIX_ATTACK_FIRST, FIRST_HOPPER);
		closePoolAndAssert(route, RouteStage.MIX_FISH_19_SECOND);

		completeFishCookLoad(route, 19, RouteStage.MIX_COOK_19_SECOND,
			RouteStage.MIX_LOAD_19_SECOND, RouteStage.MIX_ATTACK_SECOND, FIRST_HOPPER);
		closePoolAndAssert(route, RouteStage.MIX_FISH_FINAL_28);

		updateAndAssert(route, snapshot(28, 0, 0, 0, 60, false, false, false),
			RouteStage.MIX_COOK_FINAL_28);
		updateAndAssert(route, snapshot(0, 28, 0, 0, 60, false, false, false),
			RouteStage.MIX_LOAD_FINAL_28);
		updateAndAssert(route, loading(14, FIRST_HOPPER), RouteStage.MIX_LOAD_FINAL_28);
		assertEquals(14, route.getFirstFinalHopperLoaded());
		assertFalse(route.isPreferredLoadHopper(FIRST_HOPPER));
		assertTrue(route.isPreferredLoadHopper(SECOND_HOPPER));
		updateAndAssert(route, loading(0, SECOND_HOPPER), RouteStage.MIX_ATTACK_FINAL);
		assertEquals(14, route.getSecondFinalHopperLoaded());

		updateAndAssert(route, snapshot(0, 0, 0, 28, 0, false, false, true),
			RouteStage.MIX_POST_KILL_BUCKETS);
		assertEquals(17, route.getStageNumber());
	}

	@Test
	public void doubleSpotEndsEarlyCookAndKeepsFishingPriority()
	{
		TemporossRoute route = new TemporossRoute(TemporossMethod.COOKING_MIX_XP_PERMITS);
		RouteSnapshot eightFish = snapshot(8, 0, 0, 15, 100, false, false, false);
		route.update(eightFish);
		assertEquals(RouteTarget.COOK, route.getTarget(eightFish));

		RouteSnapshot doubleSpot = snapshot(4, 4, 0, 15, 100, true, false, false);
		route.update(doubleSpot);
		assertEquals(RouteStage.MIX_FISH_17, route.getStage());
		assertEquals(RouteTarget.FISH, route.getTarget(doubleSpot));

		RouteSnapshot doubleGone = snapshot(9, 4, 0, 10, 100, false, false, false);
		route.update(doubleGone);
		assertEquals(RouteTarget.FISH, route.getTarget(doubleGone));
	}

	@Test
	public void everyFishingTripUsesTheEarlyCookBreak()
	{
		RouteStage[] fishingStages =
		{
			RouteStage.MIX_FISH_17,
			RouteStage.MIX_FISH_19_FIRST,
			RouteStage.MIX_FISH_19_SECOND,
			RouteStage.MIX_FISH_FINAL_28
		};
		for (RouteStage fishingStage : fishingStages)
		{
			TemporossRoute route = routeAt(fishingStage);
			RouteSnapshot eightFish = snapshot(8, 0, 0, 20, 100, false, false, false);
			route.update(eightFish);
			assertEquals(RouteTarget.COOK, route.getTarget(eightFish));

			RouteSnapshot doubleSpot = snapshot(4, 4, 0, 20, 100, true, false, false);
			route.update(doubleSpot);
			assertEquals(RouteTarget.FISH, route.getTarget(doubleSpot));
		}
	}

	@Test
	public void crystallisedFishDoesNotCountTowardCookingTargets()
	{
		TemporossRoute route = new TemporossRoute(TemporossMethod.COOKING_MIX_XP_PERMITS);
		route.update(snapshot(7, 0, 1, 15, 100, false, false, false));
		assertEquals(RouteTarget.FISH, route.getTarget(snapshot(7, 0, 1, 15, 100, false, false, false)));

		route.update(snapshot(8, 0, 1, 14, 100, false, false, false));
		assertEquals(RouteTarget.COOK, route.getTarget(snapshot(8, 0, 1, 14, 100, false, false, false)));
		route.update(snapshot(0, 8, 1, 14, 100, false, false, false));
		route.update(snapshot(13, 0, 4, 6, 100, false, false, false));
		assertEquals(RouteStage.MIX_FISH_17, route.getStage());
		route.update(snapshot(17, 0, 4, 2, 100, false, false, false));
		assertEquals(RouteStage.MIX_COOK_17, route.getStage());
	}

	@Test
	public void exactCookedThresholdsDoNotAdvanceEarly()
	{
		TemporossRoute route = routeAt(RouteStage.MIX_COOK_17);
		route.update(snapshot(0, 16, 0, 12, 100, false, false, false));
		assertEquals(RouteStage.MIX_COOK_17, route.getStage());
		route.update(snapshot(0, 17, 0, 11, 100, false, false, false));
		assertEquals(RouteStage.MIX_LOAD_17, route.getStage());

		TemporossRoute standard = routeAt(RouteStage.MIX_COOK_19_FIRST);
		standard.update(snapshot(0, 18, 0, 10, 100, false, false, false));
		assertEquals(RouteStage.MIX_COOK_19_FIRST, standard.getStage());
		standard.update(snapshot(0, 19, 0, 9, 100, false, false, false));
		assertEquals(RouteStage.MIX_LOAD_19_FIRST, standard.getStage());
	}

	@Test
	public void excessCatchPausesTargetUntilExtraFishIsRemoved()
	{
		TemporossRoute route = routeAt(RouteStage.MIX_FISH_17);
		RouteSnapshot eighteenFish = snapshot(18, 0, 0, 10, 100, false, false, false);

		route.update(eighteenFish);
		assertEquals(RouteStage.MIX_FISH_17, route.getStage());
		assertEquals(RouteTarget.NONE, route.getTarget(eighteenFish));
		assertEquals(1, route.getExcessFish(eighteenFish));

		route.update(snapshot(17, 0, 0, 11, 100, false, false, false));
		assertEquals(RouteStage.MIX_COOK_17, route.getStage());
	}

	@Test
	public void onlyConfirmedHopperLoadingAdvancesCookedLoad()
	{
		TemporossRoute route = routeAt(RouteStage.MIX_COOK_17);
		route.update(snapshot(0, 17, 0, 11, 100, false, false, false));
		assertEquals(RouteStage.MIX_LOAD_17, route.getStage());

		route.update(snapshot(0, 0, 0, 28, 100, false, false, false));
		assertEquals(RouteStage.MIX_LOAD_17, route.getStage());
		assertEquals(0, route.getPreparedLoadedThisStage());

		TemporossRoute confirmed = routeAt(RouteStage.MIX_COOK_17);
		confirmed.update(snapshot(0, 17, 0, 11, 100, false, false, false));
		confirmed.update(loading(10, FIRST_HOPPER));
		assertEquals(7, confirmed.getPreparedLoadedThisStage());
		confirmed.update(loading(0, FIRST_HOPPER));
		assertEquals(RouteStage.MIX_FIRES_FIRST, confirmed.getStage());
	}

	@Test
	public void postKillBucketSnapshotReportsCollectedAndFilledSeparately()
	{
		RouteSnapshot snapshot = new RouteSnapshot(
			0, 0, 0, 23, 0, false, false, false, -1, 3, 2, true);

		assertEquals(3, snapshot.getWaterBuckets());
		assertEquals(5, snapshot.getTotalBuckets());
	}

	@Test
	public void methodSwitchAndNavigationUseTheSeventeenStepRoute()
	{
		TemporossRoute route = new TemporossRoute();
		route.next();
		assertEquals(RouteStage.LOAD_26, route.getStage());

		route.setMethod(TemporossMethod.COOKING_MIX_XP_PERMITS);
		assertEquals(RouteStage.MIX_FISH_17, route.getStage());
		assertEquals(1, route.getStageNumber());
		assertEquals(17, route.getStageCount());

		route.previous();
		assertEquals(RouteStage.MIX_FISH_17, route.getStage());
		for (int i = 0; i < route.getStageCount() + 2; i++)
		{
			route.next();
		}
		assertEquals(RouteStage.MIX_POST_KILL_BUCKETS, route.getStage());
		assertEquals("No cooking (max XP)", TemporossMethod.NO_COOKING_MAX_XP.toString());
		assertEquals("Cooking Mix of XP and permits", TemporossMethod.COOKING_MIX_XP_PERMITS.toString());
	}

	@Test
	public void usesRequestedVisibleStepWording()
	{
		String[] titles =
		{
			"Fish 17 harpoonfish",
			"Cook all the fish",
			"Load all 17 fish",
			"Fires should come up now",
			"Fish 19 harpoonfish",
			"Cook all",
			"Load all 19 fish",
			"Attack Tempoross",
			"Fish 19 harpoonfish",
			"Cook all",
			"Load all 19 fish",
			"Attack Tempoross",
			"Fish 28 harpoonfish",
			"Cook all",
			"Load 14 in EACH cannon",
			"Attack Tempoross",
			"Collect & fill 5 buckets"
		};

		TemporossRoute route = new TemporossRoute(TemporossMethod.COOKING_MIX_XP_PERMITS);
		for (int i = 0; i < titles.length; i++)
		{
			assertEquals(titles[i], route.getStage().getTitle());
			route.next();
		}
	}

	private static void completeFishCookLoad(
		TemporossRoute route,
		int target,
		RouteStage cookStage,
		RouteStage loadStage,
		RouteStage nextStage,
		int hopperKey)
	{
		updateAndAssert(route, snapshot(target, 0, 0, 28 - target, 70, false, false, false), cookStage);
		updateAndAssert(route, snapshot(0, target, 0, 28 - target, 70, false, false, false), loadStage);
		updateAndAssert(route, loading(0, hopperKey), nextStage);
	}

	private static void closePoolAndAssert(TemporossRoute route, RouteStage expected)
	{
		route.update(snapshot(0, 0, 0, 28, 70, false, true, false));
		updateAndAssert(route, snapshot(0, 0, 0, 28, 70, false, false, false), expected);
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

	private static RouteSnapshot loading(int cooked, int hopperKey)
	{
		return new RouteSnapshot(
			0, cooked, 0, 28 - cooked, 70, false, false, true, hopperKey, 0, 0, false);
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
		return new RouteSnapshot(
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

	private static void updateAndAssert(
		TemporossRoute route,
		RouteSnapshot snapshot,
		RouteStage expected)
	{
		route.update(snapshot);
		assertEquals(expected, route.getStage());
	}
}
