package com.temporosssolo;

final class TemporossRoute
{
	static final int FIRST_FISH_TARGET = 26;
	static final int MIN_FIRST_FISH_TARGET = 25;
	static final int FULL_FISH_TARGET = 27;
	static final int THREE_FISH_REMAINDER = 24;
	static final int ESSENCE_TARGET = 10;
	static final int MIX_EARLY_COOK_TARGET = 8;
	static final int MIX_FIRST_LOAD_TARGET = 17;
	static final int MIX_STANDARD_LOAD_TARGET = 19;
	static final int MIX_FINAL_LOAD_TARGET = 28;
	static final int MIX_FINAL_HOPPER_TARGET = 14;
	static final int PERMITS_EARLY_COOK_TARGET = 9;
	static final int PERMITS_FIRST_LOAD_TARGET = 17;
	static final int PERMITS_STANDARD_LOAD_TARGET = 19;
	static final int PERMITS_FINAL_LOAD_TARGET = 2;
	static final int PERMITS_SECOND_ATTACK_ESSENCE_TARGET = 50;
	static final int PERMITS_LEAVE_STORM_PERCENT = 93;
	static final int PERMITS_FIRE_BLOCKED_STORM_PERCENT = 90;

	private static final RouteStage[] NO_COOKING_STAGES =
	{
		RouteStage.CATCH_26,
		RouteStage.LOAD_26,
		RouteStage.CATCH_27_FIRST,
		RouteStage.LOAD_27_FIRST,
		RouteStage.ATTACK_FIRST,
		RouteStage.CATCH_27_SECOND,
		RouteStage.LOAD_THREE,
		RouteStage.ATTACK_TO_TEN,
		RouteStage.LOAD_REMAINDER,
		RouteStage.CATCH_27_FINAL,
		RouteStage.LOAD_FINAL,
		RouteStage.KILL_TEMPOROSS,
		RouteStage.COMPLETE
	};

	private static final RouteStage[] COOKING_STAGES =
	{
		RouteStage.MIX_FISH_17,
		RouteStage.MIX_COOK_17,
		RouteStage.MIX_LOAD_17,
		RouteStage.MIX_FIRES_FIRST,
		RouteStage.MIX_FISH_19_FIRST,
		RouteStage.MIX_COOK_19_FIRST,
		RouteStage.MIX_LOAD_19_FIRST,
		RouteStage.MIX_ATTACK_FIRST,
		RouteStage.MIX_FISH_19_SECOND,
		RouteStage.MIX_COOK_19_SECOND,
		RouteStage.MIX_LOAD_19_SECOND,
		RouteStage.MIX_ATTACK_SECOND,
		RouteStage.MIX_FISH_FINAL_28,
		RouteStage.MIX_COOK_FINAL_28,
		RouteStage.MIX_LOAD_FINAL_28,
		RouteStage.MIX_ATTACK_FINAL,
		RouteStage.MIX_POST_KILL_BUCKETS
	};

	private static final RouteStage[] MAX_PERMITS_STAGES =
	{
		RouteStage.PERMITS_FISH_17,
		RouteStage.PERMITS_COOK_17,
		RouteStage.PERMITS_LOAD_17,
		RouteStage.PERMITS_DOUSE_SHIP,
		RouteStage.PERMITS_FISH_19_FIRST,
		RouteStage.PERMITS_COOK_19_FIRST,
		RouteStage.PERMITS_LOAD_19_FIRST,
		RouteStage.PERMITS_ATTACK_FIRST,
		RouteStage.PERMITS_FISH_19_SECOND,
		RouteStage.PERMITS_COOK_19_SECOND,
		RouteStage.PERMITS_LOAD_19_SECOND,
		RouteStage.PERMITS_ATTACK_SECOND,
		RouteStage.PERMITS_FIREFIGHT,
		RouteStage.PERMITS_FISH_2,
		RouteStage.PERMITS_LOAD_2,
		RouteStage.PERMITS_KILL,
		RouteStage.PERMITS_COMPLETE
	};

	private TemporossMethod method;
	private RouteStage stage;
	private boolean spiritPoolSeen;
	private boolean earlyCookActive;
	private boolean earlyCookFinished;
	private boolean finalDoubleSpotSeen;
	private boolean finalDoubleCookFinished;
	private int preparedLoadTarget;
	private int preparedLoadedThisStage;
	private int lastPreparedFish;
	private int firstFinalHopperKey;
	private int firstFinalHopperLoaded;
	private int secondFinalHopperKey;
	private int secondFinalHopperLoaded;

	TemporossRoute()
	{
		this(TemporossMethod.NO_COOKING_MAX_XP);
	}

	TemporossRoute(TemporossMethod method)
	{
		this.method = method;
		reset();
	}

	TemporossMethod getMethod()
	{
		return method;
	}

	void setMethod(TemporossMethod method)
	{
		if (this.method != method)
		{
			this.method = method;
			reset();
		}
	}

	RouteStage getStage()
	{
		return stage;
	}

	RouteTarget getTarget(RouteSnapshot snapshot)
	{
		if (getExcessFish(snapshot) > 0)
		{
			return RouteTarget.NONE;
		}
		if (isEarlyCookFishingStage(stage) && earlyCookActive && !earlyCookFinished)
		{
			return RouteTarget.COOK;
		}
		if (stage == RouteStage.MIX_FISH_FINAL_28
			&& finalDoubleSpotSeen
			&& !finalDoubleCookFinished
			&& !snapshot.isDoubleSpotActive()
			&& snapshot.getRawFish() > 0)
		{
			return RouteTarget.COOK;
		}
		if (isPermitsCookedLoadStage(stage)
			&& preparedLoadedThisStage > 0
			&& getRemainingLoad() > 0
			&& snapshot.getPreparedFish() == 0
			&& !snapshot.isLoadingFish())
		{
			// A partial load ran out of fish: catch and cook the shortfall, then return to the cannon.
			return snapshot.getRawFish() > 0 && snapshot.getCookableFish() >= getRemainingLoad()
				? RouteTarget.COOK
				: RouteTarget.FISH;
		}
		return stage.getTarget();
	}

	int getExcessFish(RouteSnapshot snapshot)
	{
		int target;
		switch (stage)
		{
			case MIX_FISH_17:
				target = MIX_FIRST_LOAD_TARGET;
				break;
			case MIX_FISH_19_FIRST:
			case MIX_FISH_19_SECOND:
				target = MIX_STANDARD_LOAD_TARGET;
				break;
			case MIX_FISH_FINAL_28:
				target = MIX_FINAL_LOAD_TARGET;
				break;
			case PERMITS_FISH_17:
				target = PERMITS_FIRST_LOAD_TARGET;
				break;
			case PERMITS_FISH_19_FIRST:
			case PERMITS_FISH_19_SECOND:
				target = PERMITS_STANDARD_LOAD_TARGET;
				break;
			default:
				return 0;
		}
		return Math.max(0, snapshot.getCookableFish() - target);
	}

	int getStageNumber()
	{
		return indexOf(stage) + 1;
	}

	int getStageCount()
	{
		return stages().length;
	}

	int getTripCount()
	{
		RouteStage[] stages = stages();
		return stages[stages.length - 1].getTrip();
	}

	int getFirstFinalHopperLoaded()
	{
		return firstFinalHopperLoaded;
	}

	int getSecondFinalHopperLoaded()
	{
		return secondFinalHopperLoaded;
	}

	boolean isPreferredLoadHopper(int hopperKey)
	{
		if (stage != RouteStage.MIX_LOAD_FINAL_28
			|| firstFinalHopperKey < 0
			|| firstFinalHopperLoaded < MIX_FINAL_HOPPER_TARGET)
		{
			return true;
		}
		return secondFinalHopperKey >= 0
			? hopperKey == secondFinalHopperKey
			: hopperKey != firstFinalHopperKey;
	}

	int getPreparedLoadedThisStage()
	{
		return preparedLoadedThisStage;
	}

	int getRemainingLoad()
	{
		return Math.max(0, preparedLoadTarget - preparedLoadedThisStage);
	}

	void reset()
	{
		stage = stages()[0];
		spiritPoolSeen = false;
		resetEarlyCookTracking();
		resetFinalDoubleTracking();
		resetFinalHopperTracking();
		resetPreparedLoadTracking();
	}

	void next()
	{
		RouteStage[] stages = stages();
		setStage(stages[Math.min(indexOf(stage) + 1, stages.length - 1)]);
	}

	void previous()
	{
		RouteStage[] stages = stages();
		setStage(stages[Math.max(indexOf(stage) - 1, 0)]);
	}

	void update(RouteSnapshot snapshot)
	{
		switch (method)
		{
			case NO_COOKING_MAX_XP:
				updateNoCooking(snapshot);
				break;
			case COOKING_FIREFIGHTING_MAX_PERMITS:
				updateMaxPermits(snapshot);
				break;
			default:
				updateCooking(snapshot);
				break;
		}
	}

	private void updateNoCooking(RouteSnapshot snapshot)
	{
		switch (stage)
		{
			case CATCH_26:
				if (snapshot.getTotalFish() >= getFirstFishTarget(snapshot))
				{
					setStage(RouteStage.LOAD_26);
				}
				break;
			case LOAD_26:
				if (snapshot.getTotalFish() == 0)
				{
					setStage(RouteStage.CATCH_27_FIRST);
				}
				break;
			case CATCH_27_FIRST:
				if (snapshot.getTotalFish() >= FULL_FISH_TARGET)
				{
					setStage(RouteStage.LOAD_27_FIRST);
				}
				break;
			case LOAD_27_FIRST:
				if (snapshot.getTotalFish() == 0)
				{
					setStage(RouteStage.ATTACK_FIRST);
				}
				break;
			case ATTACK_FIRST:
				if (poolClosedAfterBeingSeen(snapshot))
				{
					setStage(RouteStage.CATCH_27_SECOND);
				}
				break;
			case CATCH_27_SECOND:
				if (snapshot.getTotalFish() >= FULL_FISH_TARGET)
				{
					setStage(RouteStage.LOAD_THREE);
				}
				break;
			case LOAD_THREE:
				if (snapshot.getTotalFish() > 0
					&& snapshot.getTotalFish() <= THREE_FISH_REMAINDER)
				{
					setStage(RouteStage.ATTACK_TO_TEN);
				}
				break;
			case ATTACK_TO_TEN:
				if (snapshot.getEssencePercent() != null
					&& snapshot.getEssencePercent() <= ESSENCE_TARGET)
				{
					setStage(RouteStage.LOAD_REMAINDER);
				}
				break;
			case LOAD_REMAINDER:
				if (snapshot.getTotalFish() == 0)
				{
					setStage(RouteStage.CATCH_27_FINAL);
				}
				break;
			case CATCH_27_FINAL:
				if (snapshot.getTotalFish() >= FULL_FISH_TARGET)
				{
					setStage(RouteStage.LOAD_FINAL);
				}
				break;
			case LOAD_FINAL:
				if (snapshot.getTotalFish() == 0)
				{
					setStage(RouteStage.KILL_TEMPOROSS);
				}
				break;
			case KILL_TEMPOROSS:
				if (snapshot.isDefeated())
				{
					setStage(RouteStage.COMPLETE);
				}
				break;
			case COMPLETE:
				break;
			default:
				break;
		}
	}

	private void updateCooking(RouteSnapshot snapshot)
	{
		switch (stage)
		{
			case MIX_FISH_17:
				updateMixedFishing(snapshot, MIX_FIRST_LOAD_TARGET, MIX_EARLY_COOK_TARGET, RouteStage.MIX_COOK_17);
				break;
			case MIX_COOK_17:
				if (allFishPrepared(snapshot, MIX_FIRST_LOAD_TARGET))
				{
					beginPreparedLoad(snapshot, MIX_FIRST_LOAD_TARGET, RouteStage.MIX_LOAD_17);
				}
				break;
			case MIX_LOAD_17:
				if (preparedLoadComplete(snapshot, MIX_FIRST_LOAD_TARGET))
				{
					setStage(RouteStage.MIX_FIRES_FIRST);
				}
				break;
			case MIX_FIRES_FIRST:
				if (snapshot.getCookableFish() > 0)
				{
					setStage(RouteStage.MIX_FISH_19_FIRST);
				}
				break;
			case MIX_FISH_19_FIRST:
				updateMixedFishing(snapshot, MIX_STANDARD_LOAD_TARGET, MIX_EARLY_COOK_TARGET, RouteStage.MIX_COOK_19_FIRST);
				break;
			case MIX_COOK_19_FIRST:
				if (allFishPrepared(snapshot, MIX_STANDARD_LOAD_TARGET))
				{
					beginPreparedLoad(snapshot, MIX_STANDARD_LOAD_TARGET, RouteStage.MIX_LOAD_19_FIRST);
				}
				break;
			case MIX_LOAD_19_FIRST:
				if (preparedLoadComplete(snapshot, MIX_STANDARD_LOAD_TARGET))
				{
					setStage(RouteStage.MIX_ATTACK_FIRST);
				}
				break;
			case MIX_ATTACK_FIRST:
				if (poolClosedAfterBeingSeen(snapshot))
				{
					setStage(RouteStage.MIX_FISH_19_SECOND);
				}
				break;
			case MIX_FISH_19_SECOND:
				updateMixedFishing(snapshot, MIX_STANDARD_LOAD_TARGET, MIX_EARLY_COOK_TARGET, RouteStage.MIX_COOK_19_SECOND);
				break;
			case MIX_COOK_19_SECOND:
				if (allFishPrepared(snapshot, MIX_STANDARD_LOAD_TARGET))
				{
					beginPreparedLoad(snapshot, MIX_STANDARD_LOAD_TARGET, RouteStage.MIX_LOAD_19_SECOND);
				}
				break;
			case MIX_LOAD_19_SECOND:
				if (preparedLoadComplete(snapshot, MIX_STANDARD_LOAD_TARGET))
				{
					setStage(RouteStage.MIX_ATTACK_SECOND);
				}
				break;
			case MIX_ATTACK_SECOND:
				if (poolClosedAfterBeingSeen(snapshot))
				{
					setStage(RouteStage.MIX_FISH_FINAL_28);
				}
				break;
			case MIX_FISH_FINAL_28:
				updateFinalFishing(snapshot);
				break;
			case MIX_COOK_FINAL_28:
				if (allFishPrepared(snapshot, MIX_FINAL_LOAD_TARGET))
				{
					resetFinalHopperTracking();
					beginPreparedLoad(snapshot, MIX_FINAL_LOAD_TARGET, RouteStage.MIX_LOAD_FINAL_28);
				}
				break;
			case MIX_LOAD_FINAL_28:
				if (preparedLoadComplete(snapshot, MIX_FINAL_LOAD_TARGET))
				{
					setStage(RouteStage.MIX_ATTACK_FINAL);
				}
				break;
			case MIX_ATTACK_FINAL:
				if (snapshot.isDefeated())
				{
					setStage(RouteStage.MIX_POST_KILL_BUCKETS);
				}
				break;
			case MIX_POST_KILL_BUCKETS:
				break;
			default:
				break;
		}
	}

	private void updateMaxPermits(RouteSnapshot snapshot)
	{
		switch (stage)
		{
			case PERMITS_FISH_17:
				updateMixedFishing(snapshot, PERMITS_FIRST_LOAD_TARGET, PERMITS_EARLY_COOK_TARGET,
					RouteStage.PERMITS_COOK_17);
				break;
			case PERMITS_COOK_17:
				if (allFishPrepared(snapshot, PERMITS_FIRST_LOAD_TARGET))
				{
					beginPreparedLoad(snapshot, PERMITS_FIRST_LOAD_TARGET, RouteStage.PERMITS_LOAD_17);
				}
				break;
			case PERMITS_LOAD_17:
				if (preparedLoadComplete(snapshot, PERMITS_FIRST_LOAD_TARGET))
				{
					setStage(RouteStage.PERMITS_DOUSE_SHIP);
				}
				break;
			case PERMITS_DOUSE_SHIP:
				if (snapshot.getCookableFish() > 0)
				{
					setStage(RouteStage.PERMITS_FISH_19_FIRST);
				}
				break;
			case PERMITS_FISH_19_FIRST:
				if (startedEarlyLoad(snapshot, RouteStage.PERMITS_LOAD_19_FIRST))
				{
					break;
				}
				if (snapshot.getCookableFish() == PERMITS_STANDARD_LOAD_TARGET)
				{
					setStage(RouteStage.PERMITS_COOK_19_FIRST);
				}
				break;
			case PERMITS_COOK_19_FIRST:
				if (startedEarlyLoad(snapshot, RouteStage.PERMITS_LOAD_19_FIRST))
				{
					break;
				}
				if (allFishPrepared(snapshot, PERMITS_STANDARD_LOAD_TARGET))
				{
					beginPreparedLoad(snapshot, PERMITS_STANDARD_LOAD_TARGET, RouteStage.PERMITS_LOAD_19_FIRST);
				}
				break;
			case PERMITS_LOAD_19_FIRST:
				if (preparedLoadComplete(snapshot, PERMITS_STANDARD_LOAD_TARGET))
				{
					setStage(RouteStage.PERMITS_ATTACK_FIRST);
				}
				break;
			case PERMITS_ATTACK_FIRST:
				if (poolClosedAfterBeingSeen(snapshot))
				{
					setStage(RouteStage.PERMITS_FISH_19_SECOND);
				}
				break;
			case PERMITS_FISH_19_SECOND:
				if (startedEarlyLoad(snapshot, RouteStage.PERMITS_LOAD_19_SECOND))
				{
					break;
				}
				if (snapshot.getCookableFish() == PERMITS_STANDARD_LOAD_TARGET)
				{
					setStage(RouteStage.PERMITS_COOK_19_SECOND);
				}
				break;
			case PERMITS_COOK_19_SECOND:
				if (startedEarlyLoad(snapshot, RouteStage.PERMITS_LOAD_19_SECOND))
				{
					break;
				}
				if (allFishPrepared(snapshot, PERMITS_STANDARD_LOAD_TARGET))
				{
					beginPreparedLoad(snapshot, PERMITS_STANDARD_LOAD_TARGET, RouteStage.PERMITS_LOAD_19_SECOND);
				}
				break;
			case PERMITS_LOAD_19_SECOND:
				if (preparedLoadComplete(snapshot, PERMITS_STANDARD_LOAD_TARGET))
				{
					setStage(RouteStage.PERMITS_ATTACK_SECOND);
				}
				break;
			case PERMITS_ATTACK_SECOND:
				if (poolClosedAfterBeingSeen(snapshot)
					|| (snapshot.getEssencePercent() != null
						&& snapshot.getEssencePercent() < PERMITS_SECOND_ATTACK_ESSENCE_TARGET))
				{
					setStage(RouteStage.PERMITS_FIREFIGHT);
				}
				break;
			case PERMITS_FIREFIGHT:
				if (snapshot.getCookableFish() > 0)
				{
					setStage(RouteStage.PERMITS_FISH_2);
				}
				break;
			case PERMITS_FISH_2:
				if (snapshot.getCookableFish() >= PERMITS_FINAL_LOAD_TARGET)
				{
					beginPreparedLoad(snapshot, PERMITS_FINAL_LOAD_TARGET, RouteStage.PERMITS_LOAD_2);
				}
				break;
			case PERMITS_LOAD_2:
				if (preparedLoadComplete(snapshot, PERMITS_FINAL_LOAD_TARGET))
				{
					setStage(RouteStage.PERMITS_KILL);
				}
				break;
			case PERMITS_KILL:
				if (snapshot.isDefeated())
				{
					setStage(RouteStage.PERMITS_COMPLETE);
				}
				break;
			default:
				break;
		}
	}

	/**
	 * When the storm forces a run to the ship before all 19 fish are cooked, the player loads
	 * what they have. Count that partial load so the shortfall can be finished afterwards.
	 */
	private boolean startedEarlyLoad(RouteSnapshot snapshot, RouteStage loadStage)
	{
		int prepared = snapshot.getPreparedFish();
		int previous = lastPreparedFish;
		lastPreparedFish = prepared;
		if (!snapshot.isLoadingFish() || prepared >= previous)
		{
			return false;
		}

		setStage(loadStage);
		preparedLoadTarget = PERMITS_STANDARD_LOAD_TARGET;
		preparedLoadedThisStage = previous - prepared;
		lastPreparedFish = prepared;
		return true;
	}

	private void updateMixedFishing(RouteSnapshot snapshot, int target, int earlyCookTarget, RouteStage cookStage)
	{
		if (snapshot.getCookableFish() == target)
		{
			setStage(cookStage);
			return;
		}

		if (!earlyCookFinished
			&& !earlyCookActive
			&& snapshot.getCookableFish() >= earlyCookTarget
			&& snapshot.getRawFish() > 0)
		{
			earlyCookActive = true;
		}

		if (earlyCookActive
			&& (snapshot.isDoubleSpotActive() || snapshot.getRawFish() == 0))
		{
			earlyCookActive = false;
			earlyCookFinished = true;
		}
	}

	private void updateFinalFishing(RouteSnapshot snapshot)
	{
		if (snapshot.getCookableFish() == MIX_FINAL_LOAD_TARGET)
		{
			setStage(RouteStage.MIX_COOK_FINAL_28);
			return;
		}

		if (snapshot.isDoubleSpotActive())
		{
			finalDoubleSpotSeen = true;
			finalDoubleCookFinished = false;
		}
		else if (finalDoubleSpotSeen
			&& !finalDoubleCookFinished
			&& snapshot.getRawFish() == 0)
		{
			finalDoubleCookFinished = true;
		}
	}

	static int getFirstFishTarget(RouteSnapshot snapshot)
	{
		return snapshot.getFishCapacity() >= FIRST_FISH_TARGET
			? FIRST_FISH_TARGET
			: MIN_FIRST_FISH_TARGET;
	}

	private boolean allFishPrepared(RouteSnapshot snapshot, int target)
	{
		return snapshot.getRawFish() == 0 && snapshot.getPreparedFish() == target;
	}

	private void beginPreparedLoad(RouteSnapshot snapshot, int target, RouteStage loadStage)
	{
		setStage(loadStage);
		preparedLoadTarget = target;
		preparedLoadedThisStage = 0;
		lastPreparedFish = loadableFish(snapshot);
	}

	private int loadableFish(RouteSnapshot snapshot)
	{
		// The final two fish may be loaded raw; every other exact load counts cooked fish only.
		return stage == RouteStage.PERMITS_LOAD_2
			? snapshot.getCookableFish()
			: snapshot.getPreparedFish();
	}

	private boolean preparedLoadComplete(RouteSnapshot snapshot, int expectedTarget)
	{
		if (preparedLoadTarget <= 0)
		{
			preparedLoadTarget = expectedTarget;
			preparedLoadedThisStage = 0;
			lastPreparedFish = loadableFish(snapshot);
		}

		int loadable = loadableFish(snapshot);
		if (snapshot.isLoadingFish() && loadable < lastPreparedFish)
		{
			int loaded = lastPreparedFish - loadable;
			preparedLoadedThisStage += loaded;
			if (stage == RouteStage.MIX_LOAD_FINAL_28)
			{
				recordFinalHopperLoad(snapshot.getLoadingHopperKey(), loaded);
			}
		}
		lastPreparedFish = loadable;
		return preparedLoadedThisStage >= preparedLoadTarget;
	}

	private void resetPreparedLoadTracking()
	{
		preparedLoadTarget = 0;
		preparedLoadedThisStage = 0;
		lastPreparedFish = 0;
	}

	private void recordFinalHopperLoad(int hopperKey, int loaded)
	{
		if (hopperKey < 0 || loaded <= 0)
		{
			return;
		}
		if (firstFinalHopperKey < 0)
		{
			firstFinalHopperKey = hopperKey;
		}
		if (hopperKey == firstFinalHopperKey)
		{
			firstFinalHopperLoaded += loaded;
			return;
		}
		if (secondFinalHopperKey < 0)
		{
			secondFinalHopperKey = hopperKey;
		}
		if (hopperKey == secondFinalHopperKey)
		{
			secondFinalHopperLoaded += loaded;
		}
	}

	private void resetEarlyCookTracking()
	{
		earlyCookActive = false;
		earlyCookFinished = false;
	}

	private void resetFinalDoubleTracking()
	{
		finalDoubleSpotSeen = false;
		finalDoubleCookFinished = false;
	}

	private void resetFinalHopperTracking()
	{
		firstFinalHopperKey = -1;
		firstFinalHopperLoaded = 0;
		secondFinalHopperKey = -1;
		secondFinalHopperLoaded = 0;
	}

	private boolean poolClosedAfterBeingSeen(RouteSnapshot snapshot)
	{
		if (snapshot.isSpiritPoolActive())
		{
			spiritPoolSeen = true;
		}
		return spiritPoolSeen && !snapshot.isSpiritPoolActive();
	}

	private RouteStage[] stages()
	{
		switch (method)
		{
			case NO_COOKING_MAX_XP:
				return NO_COOKING_STAGES;
			case COOKING_FIREFIGHTING_MAX_PERMITS:
				return MAX_PERMITS_STAGES;
			default:
				return COOKING_STAGES;
		}
	}

	private int indexOf(RouteStage candidate)
	{
		RouteStage[] stages = stages();
		for (int i = 0; i < stages.length; i++)
		{
			if (stages[i] == candidate)
			{
				return i;
			}
		}
		return 0;
	}

	private void setStage(RouteStage stage)
	{
		this.stage = stage;
		spiritPoolSeen = false;
		if (isMixedFishingStage(stage))
		{
			resetEarlyCookTracking();
		}
		if (stage == RouteStage.MIX_FISH_FINAL_28)
		{
			resetFinalDoubleTracking();
		}
		if (stage.getTarget() != RouteTarget.LOAD)
		{
			resetPreparedLoadTracking();
		}
	}

	private static boolean isMixedFishingStage(RouteStage candidate)
	{
		return candidate == RouteStage.MIX_FISH_17
			|| candidate == RouteStage.MIX_FISH_19_FIRST
			|| candidate == RouteStage.MIX_FISH_19_SECOND
			|| candidate == RouteStage.MIX_FISH_FINAL_28
			|| candidate == RouteStage.PERMITS_FISH_17;
	}

	private static boolean isEarlyCookFishingStage(RouteStage candidate)
	{
		return candidate == RouteStage.MIX_FISH_17
			|| candidate == RouteStage.MIX_FISH_19_FIRST
			|| candidate == RouteStage.MIX_FISH_19_SECOND
			|| candidate == RouteStage.PERMITS_FISH_17;
	}

	/**
	 * Stages where cannon loading must be observed: every load step, plus the max permits 19-fish
	 * steps where the storm can force an early partial load.
	 */
	static boolean tracksLoading(RouteStage candidate)
	{
		return candidate.getTarget() == RouteTarget.LOAD
			|| candidate == RouteStage.PERMITS_FISH_19_FIRST
			|| candidate == RouteStage.PERMITS_COOK_19_FIRST
			|| candidate == RouteStage.PERMITS_FISH_19_SECOND
			|| candidate == RouteStage.PERMITS_COOK_19_SECOND;
	}

	static boolean isPermitsCookedLoadStage(RouteStage candidate)
	{
		return candidate == RouteStage.PERMITS_LOAD_17
			|| candidate == RouteStage.PERMITS_LOAD_19_FIRST
			|| candidate == RouteStage.PERMITS_LOAD_19_SECOND;
	}
}
