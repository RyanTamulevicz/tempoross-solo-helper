package com.temporosssolo;

final class RouteSnapshot
{
	private final int fish;
	private final int emptyBuckets;
	private final int waterBuckets;
	private final Integer essencePercent;
	private final boolean spiritPoolActive;
	private final boolean defeated;

	RouteSnapshot(
		int fish,
		int emptyBuckets,
		int waterBuckets,
		Integer essencePercent,
		boolean spiritPoolActive,
		boolean defeated)
	{
		this.fish = fish;
		this.emptyBuckets = emptyBuckets;
		this.waterBuckets = waterBuckets;
		this.essencePercent = essencePercent;
		this.spiritPoolActive = spiritPoolActive;
		this.defeated = defeated;
	}

	int getFish()
	{
		return fish;
	}

	int getEmptyBuckets()
	{
		return emptyBuckets;
	}

	int getWaterBuckets()
	{
		return waterBuckets;
	}

	int getBuckets()
	{
		return emptyBuckets + waterBuckets;
	}

	Integer getEssencePercent()
	{
		return essencePercent;
	}

	boolean isSpiritPoolActive()
	{
		return spiritPoolActive;
	}

	boolean isDefeated()
	{
		return defeated;
	}
}
