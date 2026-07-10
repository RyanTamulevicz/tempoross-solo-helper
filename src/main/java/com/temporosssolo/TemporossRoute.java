package com.temporosssolo;

final class TemporossRoute
{
	static final int FIRST_FISH_TARGET = 26;
	static final int MIN_FIRST_FISH_TARGET = 25;
	static final int FULL_FISH_TARGET = 27;
	static final int THREE_FISH_REMAINDER = 24;
	static final int ESSENCE_TARGET = 10;
	static final int MIX_OPENING_TARGET = 8;
	static final int MIX_FIRST_LOAD_TARGET = 17;
	static final int MIX_STANDARD_LOAD_TARGET = 19;
	static final int MIX_FINAL_LOAD_TARGET = 28;

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
		RouteStage.MIX_CATCH_OPENING,
		RouteStage.MIX_COOK_OPENING,
		RouteStage.MIX_CATCH_TO_17,
		RouteStage.MIX_COOK_TO_17,
		RouteStage.MIX_LOAD_17,
		RouteStage.MIX_CATCH_19_FIRST,
		RouteStage.MIX_COOK_19_FIRST,
		RouteStage.MIX_LOAD_19_FIRST,
		RouteStage.MIX_ATTACK_FIRST,
		RouteStage.MIX_CATCH_19_SECOND,
		RouteStage.MIX_COOK_19_SECOND,
		RouteStage.MIX_LOAD_19_SECOND,
		RouteStage.MIX_ATTACK_SECOND,
		RouteStage.MIX_CATCH_FINAL,
		RouteStage.MIX_COOK_FINAL,
		RouteStage.MIX_LOAD_FINAL,
		RouteStage.MIX_KILL_TEMPOROSS,
		RouteStage.MIX_COMPLETE
	};

	private TemporossMethod method;
	private RouteStage stage;
	private boolean spiritPoolSeen;
	private int finalBatchLoaded;
	private int finalCycleTarget;
	private int preparedLoadTarget;
	private int preparedLoadedThisStage;
	private int lastPreparedFish;

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
		return getExcessFish(snapshot) > 0 ? RouteTarget.NONE : stage.getTarget();
	}

	int getExcessFish(RouteSnapshot snapshot)
	{
		int target;
		switch (stage)
		{
			case MIX_CATCH_TO_17:
				target = MIX_FIRST_LOAD_TARGET;
				break;
			case MIX_CATCH_19_FIRST:
			case MIX_CATCH_19_SECOND:
				target = MIX_STANDARD_LOAD_TARGET;
				break;
			case MIX_CATCH_FINAL:
				target = getFinalCycleTarget(snapshot);
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

	int getFinalBatchLoaded()
	{
		return finalBatchLoaded;
	}

	int getPreparedLoadedThisStage()
	{
		return preparedLoadedThisStage;
	}

	int getFinalCycleTarget(RouteSnapshot snapshot)
	{
		if (finalCycleTarget > 0)
		{
			return finalCycleTarget;
		}
		return Math.min(
			MIX_FINAL_LOAD_TARGET - finalBatchLoaded,
			Math.max(1, snapshot.getCookableFishCapacity()));
	}

	void reset()
	{
		stage = stages()[0];
		spiritPoolSeen = false;
		finalBatchLoaded = 0;
		finalCycleTarget = 0;
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
			case MIX_CATCH_OPENING:
				if (snapshot.getCookableFish() >= MIX_OPENING_TARGET)
				{
					setStage(RouteStage.MIX_COOK_OPENING);
				}
				break;
			case MIX_COOK_OPENING:
				if (snapshot.isDoubleSpotActive()
					&& snapshot.getCookableFish() < MIX_FIRST_LOAD_TARGET)
				{
					setStage(RouteStage.MIX_CATCH_TO_17);
				}
				else if (allFishPrepared(snapshot, MIX_OPENING_TARGET))
				{
					setStage(RouteStage.MIX_CATCH_TO_17);
				}
				break;
			case MIX_CATCH_TO_17:
				if (snapshot.getCookableFish() == MIX_FIRST_LOAD_TARGET)
				{
					setStage(RouteStage.MIX_COOK_TO_17);
				}
				break;
			case MIX_COOK_TO_17:
				if (allFishPrepared(snapshot, MIX_FIRST_LOAD_TARGET))
				{
					beginPreparedLoad(snapshot, MIX_FIRST_LOAD_TARGET, RouteStage.MIX_LOAD_17);
				}
				break;
			case MIX_LOAD_17:
				if (preparedLoadComplete(snapshot, MIX_FIRST_LOAD_TARGET))
				{
					setStage(RouteStage.MIX_CATCH_19_FIRST);
				}
				break;
			case MIX_CATCH_19_FIRST:
				if (snapshot.getCookableFish() == MIX_STANDARD_LOAD_TARGET)
				{
					setStage(RouteStage.MIX_COOK_19_FIRST);
				}
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
					setStage(RouteStage.MIX_CATCH_19_SECOND);
				}
				break;
			case MIX_CATCH_19_SECOND:
				if (snapshot.getCookableFish() == MIX_STANDARD_LOAD_TARGET)
				{
					setStage(RouteStage.MIX_COOK_19_SECOND);
				}
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
					finalBatchLoaded = 0;
					finalCycleTarget = 0;
					setStage(RouteStage.MIX_CATCH_FINAL);
				}
				break;
			case MIX_CATCH_FINAL:
				int catchTarget = getFinalCycleTarget(snapshot);
				if (snapshot.getCookableFish() == catchTarget)
				{
					finalCycleTarget = Math.min(
						snapshot.getCookableFish(),
						MIX_FINAL_LOAD_TARGET - finalBatchLoaded);
					setStage(RouteStage.MIX_COOK_FINAL);
				}
				break;
			case MIX_COOK_FINAL:
				if (allFishPrepared(snapshot, finalCycleTarget))
				{
					beginPreparedLoad(snapshot, finalCycleTarget, RouteStage.MIX_LOAD_FINAL);
				}
				break;
			case MIX_LOAD_FINAL:
				if (preparedLoadComplete(snapshot, finalCycleTarget))
				{
					finalBatchLoaded += preparedLoadedThisStage;
					finalCycleTarget = 0;
					setStage(finalBatchLoaded >= MIX_FINAL_LOAD_TARGET
						? RouteStage.MIX_KILL_TEMPOROSS
						: RouteStage.MIX_CATCH_FINAL);
				}
				break;
			case MIX_KILL_TEMPOROSS:
				if (snapshot.isDefeated())
				{
					setStage(RouteStage.MIX_COMPLETE);
				}
				break;
			case MIX_COMPLETE:
				break;
			default:
				break;
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
			preparedLoadedThisStage += lastPreparedFish - snapshot.getPreparedFish();
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
		if (stage.getTarget() != RouteTarget.LOAD)
		{
			resetPreparedLoadTracking();
		}
	}
}
