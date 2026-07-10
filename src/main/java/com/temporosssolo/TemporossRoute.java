package com.temporosssolo;

final class TemporossRoute
{
	static final int FIRST_FISH_TARGET = 26;
	static final int MIN_FIRST_FISH_TARGET = 25;
	static final int FULL_FISH_TARGET = 27;
	static final int THREE_FISH_REMAINDER = 24;
	static final int ESSENCE_TARGET = 10;

	private RouteStage stage = RouteStage.CATCH_26;
	private boolean spiritPoolSeen;

	RouteStage getStage()
	{
		return stage;
	}

	void reset()
	{
		setStage(RouteStage.CATCH_26);
	}

	void next()
	{
		setStage(stage.next());
	}

	void previous()
	{
		setStage(stage.previous());
	}

	void update(RouteSnapshot snapshot)
	{
		switch (stage)
		{
			case CATCH_26:
				if (snapshot.getFish() >= getFirstFishTarget(snapshot))
				{
					setStage(RouteStage.LOAD_26);
				}
				break;
			case LOAD_26:
				if (snapshot.getFish() == 0)
				{
					setStage(RouteStage.CATCH_27_FIRST);
				}
				break;
			case CATCH_27_FIRST:
				if (snapshot.getFish() >= FULL_FISH_TARGET)
				{
					setStage(RouteStage.LOAD_27_FIRST);
				}
				break;
			case LOAD_27_FIRST:
				if (snapshot.getFish() == 0)
				{
					setStage(RouteStage.ATTACK_FIRST);
				}
				break;
			case ATTACK_FIRST:
				updatePoolSeen(snapshot);
				if (spiritPoolSeen && !snapshot.isSpiritPoolActive())
				{
					setStage(RouteStage.CATCH_27_SECOND);
				}
				break;
			case CATCH_27_SECOND:
				if (snapshot.getFish() >= FULL_FISH_TARGET)
				{
					setStage(RouteStage.LOAD_THREE);
				}
				break;
			case LOAD_THREE:
				if (snapshot.getFish() > 0
					&& snapshot.getFish() <= THREE_FISH_REMAINDER)
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
				if (snapshot.getFish() == 0)
				{
					setStage(RouteStage.CATCH_27_FINAL);
				}
				break;
			case CATCH_27_FINAL:
				if (snapshot.getFish() >= FULL_FISH_TARGET)
				{
					setStage(RouteStage.LOAD_FINAL);
				}
				break;
			case LOAD_FINAL:
				if (snapshot.getFish() == 0)
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
		}
	}

	static int getFirstFishTarget(RouteSnapshot snapshot)
	{
		return snapshot.getFishCapacity() >= FIRST_FISH_TARGET
			? FIRST_FISH_TARGET
			: MIN_FIRST_FISH_TARGET;
	}

	private void updatePoolSeen(RouteSnapshot snapshot)
	{
		if (snapshot.isSpiritPoolActive())
		{
			spiritPoolSeen = true;
		}
	}

	private void setStage(RouteStage stage)
	{
		this.stage = stage;
		spiritPoolSeen = false;
	}
}
