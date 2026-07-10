package com.temporosssolo;

final class RouteSnapshot
{
	private final int fish;
	private final int freeInventorySlots;
	private final Integer essencePercent;
	private final boolean spiritPoolActive;
	private final boolean defeated;

	RouteSnapshot(
		int fish,
		int freeInventorySlots,
		Integer essencePercent,
		boolean spiritPoolActive,
		boolean defeated)
	{
		this.fish = fish;
		this.freeInventorySlots = freeInventorySlots;
		this.essencePercent = essencePercent;
		this.spiritPoolActive = spiritPoolActive;
		this.defeated = defeated;
	}

	int getFish()
	{
		return fish;
	}

	int getFishCapacity()
	{
		return fish + freeInventorySlots;
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
