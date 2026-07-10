package com.temporosssolo;

public enum TemporossMethod
{
	NO_COOKING_MAX_XP("No cooking (max XP)"),
	COOKING_MIX_XP_PERMITS("Cooking Mix of XP and permits");

	private final String displayName;

	TemporossMethod(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
