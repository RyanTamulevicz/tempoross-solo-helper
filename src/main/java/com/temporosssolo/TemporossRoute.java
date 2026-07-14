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

	private TemporossMethod method;
	private RouteStage stage;
	private boolean spiritPoolSeen;
	private boolean earlyCookActive;
	private boolean earlyCookFinished;
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
		if (isMixedFishingStage(stage) && earlyCookActive && !earlyCookFinished)
		{
			return RouteTarget.COOK;
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
		return 4;
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

	void reset()
	{
		stage = stages()[0];
		spiritPoolSeen = false;
		resetEarlyCookTracking();
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
		if (method == TemporossMethod.NO_COOKING_MAX_XP)
		{
			updateNoCooking(snapshot);
		}
		else
		{
			updateCooking(snapshot);
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
				updateMixedFishing(snapshot, MIX_FIRST_LOAD_TARGET, RouteStage.MIX_COOK_17);
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
				updateMixedFishing(snapshot, MIX_STANDARD_LOAD_TARGET, RouteStage.MIX_COOK_19_FIRST);
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
				updateMixedFishing(snapshot, MIX_STANDARD_LOAD_TARGET, RouteStage.MIX_COOK_19_SECOND);
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
				updateMixedFishing(snapshot, MIX_FINAL_LOAD_TARGET, RouteStage.MIX_COOK_FINAL_28);
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

	private void updateMixedFishing(RouteSnapshot snapshot, int target, RouteStage cookStage)
	{
		if (snapshot.getCookableFish() == target)
		{
			setStage(cookStage);
			return;
		}

		if (!earlyCookFinished
			&& !earlyCookActive
			&& snapshot.getCookableFish() >= MIX_EARLY_COOK_TARGET
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
		preparedLoadTarget = target;
		preparedLoadedThisStage = 0;
		lastPreparedFish = snapshot.getPreparedFish();
		setStage(loadStage);
	}

	private boolean preparedLoadComplete(RouteSnapshot snapshot, int expectedTarget)
	{
		if (preparedLoadTarget <= 0)
		{
			preparedLoadTarget = expectedTarget;
			preparedLoadedThisStage = 0;
			lastPreparedFish = snapshot.getPreparedFish();
		}

		if (snapshot.isLoadingFish() && snapshot.getPreparedFish() < lastPreparedFish)
		{
			int loaded = lastPreparedFish - snapshot.getPreparedFish();
			preparedLoadedThisStage += loaded;
			if (stage == RouteStage.MIX_LOAD_FINAL_28)
			{
				recordFinalHopperLoad(snapshot.getLoadingHopperKey(), loaded);
			}
		}
		lastPreparedFish = snapshot.getPreparedFish();
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
		return method == TemporossMethod.NO_COOKING_MAX_XP
			? NO_COOKING_STAGES
			: COOKING_STAGES;
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
			|| candidate == RouteStage.MIX_FISH_FINAL_28;
	}
}
