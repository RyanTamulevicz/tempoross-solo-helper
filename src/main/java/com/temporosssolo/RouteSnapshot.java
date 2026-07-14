package com.temporosssolo;

final class RouteSnapshot
{
	private final int rawFish;
	private final int cookedFish;
	private final int crystallisedFish;
	private final int freeInventorySlots;
	private final Integer essencePercent;
	private final boolean doubleSpotActive;
	private final boolean spiritPoolActive;
	private final boolean loadingFish;
	private final int loadingHopperKey;
	private final int waterBuckets;
	private final int emptyBuckets;
	private final boolean defeated;

	RouteSnapshot(
		int rawFish,
		int cookedFish,
		int crystallisedFish,
		int freeInventorySlots,
		Integer essencePercent,
		boolean doubleSpotActive,
		boolean spiritPoolActive,
		boolean loadingFish,
		boolean defeated)
	{
		this(
			rawFish,
			cookedFish,
			crystallisedFish,
			freeInventorySlots,
			essencePercent,
			doubleSpotActive,
			spiritPoolActive,
			loadingFish,
			-1,
			0,
			0,
			defeated);
	}

	RouteSnapshot(
		int rawFish,
		int cookedFish,
		int crystallisedFish,
		int freeInventorySlots,
		Integer essencePercent,
		boolean doubleSpotActive,
		boolean spiritPoolActive,
		boolean loadingFish,
		int loadingHopperKey,
		int waterBuckets,
		int emptyBuckets,
		boolean defeated)
	{
		this.rawFish = rawFish;
		this.cookedFish = cookedFish;
		this.crystallisedFish = crystallisedFish;
		this.freeInventorySlots = freeInventorySlots;
		this.essencePercent = essencePercent;
		this.doubleSpotActive = doubleSpotActive;
		this.spiritPoolActive = spiritPoolActive;
		this.loadingFish = loadingFish;
		this.loadingHopperKey = loadingHopperKey;
		this.waterBuckets = waterBuckets;
		this.emptyBuckets = emptyBuckets;
		this.defeated = defeated;
	}

	int getRawFish()
	{
		return rawFish;
	}

	int getCookedFish()
	{
		return cookedFish;
	}

	int getPreparedFish()
	{
		return cookedFish;
	}

	int getCookableFish()
	{
		return rawFish + cookedFish;
	}

	int getTotalFish()
	{
		return rawFish + cookedFish + crystallisedFish;
	}

	int getFishCapacity()
	{
		return getTotalFish() + freeInventorySlots;
	}

	int getCookableFishCapacity()
	{
		return getCookableFish() + freeInventorySlots;
	}

	Integer getEssencePercent()
	{
		return essencePercent;
	}

	boolean isDoubleSpotActive()
	{
		return doubleSpotActive;
	}

	boolean isSpiritPoolActive()
	{
		return spiritPoolActive;
	}

	boolean isLoadingFish()
	{
		return loadingFish;
	}

	int getLoadingHopperKey()
	{
		return loadingHopperKey;
	}

	int getWaterBuckets()
	{
		return waterBuckets;
	}

	int getTotalBuckets()
	{
		return waterBuckets + emptyBuckets;
	}

	boolean isDefeated()
	{
		return defeated;
	}
}
