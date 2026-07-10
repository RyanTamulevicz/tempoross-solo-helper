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
		this.rawFish = rawFish;
		this.cookedFish = cookedFish;
		this.crystallisedFish = crystallisedFish;
		this.freeInventorySlots = freeInventorySlots;
		this.essencePercent = essencePercent;
		this.doubleSpotActive = doubleSpotActive;
		this.spiritPoolActive = spiritPoolActive;
		this.loadingFish = loadingFish;
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

	boolean isDefeated()
	{
		return defeated;
	}
}
