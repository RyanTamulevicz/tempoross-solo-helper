package com.temporosssolo;

final class TemporossRoute
{
	static final int FIRST_FISH_TARGET = 26;
	static final int FULL_FISH_TARGET = 27;
	static final int BUCKET_TARGET = 6;
	static final int FIVE_FISH_REMAINDER = 22;
	static final int ESSENCE_TARGET = 12;

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
				if (snapshot.getFish() >= FIRST_FISH_TARGET)
				{
					setStage(RouteStage.LOAD_26);
				}
				break;
			case LOAD_26:
				if (snapshot.getFish() == 0)
				{
					setStage(RouteStage.TAKE_FIVE_BUCKETS);
				}
				break;
			case TAKE_FIVE_BUCKETS:
				if (snapshot.getBuckets() >= BUCKET_TARGET)
				{
					setStage(RouteStage.HUMIDIFY_BUCKETS);
				}
				break;
			case HUMIDIFY_BUCKETS:
				if (snapshot.getBuckets() >= BUCKET_TARGET && snapshot.getEmptyBuckets() == 0)
				{
					setStage(RouteStage.DOUSE_FIRES);
				}
				break;
			case DOUSE_FIRES:
				if (snapshot.getBuckets() >= BUCKET_TARGET && snapshot.getWaterBuckets() == 0)
				{
					setStage(RouteStage.DROP_BUCKETS);
				}
				break;
			case DROP_BUCKETS:
				if (snapshot.getBuckets() == 0)
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
					setStage(RouteStage.LOAD_FIVE);
				}
				break;
			case LOAD_FIVE:
				if (snapshot.getFish() == FIVE_FISH_REMAINDER)
				{
					setStage(RouteStage.ATTACK_TO_TWELVE);
				}
				break;
			case ATTACK_TO_TWELVE:
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
					setStage(RouteStage.TAKE_ONE_BUCKET);
				}
				break;
			case TAKE_ONE_BUCKET:
				if (snapshot.getBuckets() >= 1)
				{
					setStage(RouteStage.COMPLETE);
				}
				break;
			case COMPLETE:
				break;
		}
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
