package com.temporosssolo;

enum RouteStage
{
	CATCH_26(1, "Catch 25 or 26", "Run to the island and fill the available fish slots."),
	LOAD_26(1, "Load first trip", "Load every fish at the hopper; tether if needed."),
	CATCH_27_FIRST(2, "Fish 27", "Return to the island and catch 27 fish."),
	LOAD_27_FIRST(2, "Load all 27", "Load every fish into the highlighted hopper."),
	ATTACK_FIRST(2, "Attack Tempoross", "Fish from the spirit pool until it closes."),
	CATCH_27_SECOND(3, "Fish 27", "Return to the island and catch another 27 fish."),
	LOAD_THREE(3, "Load 3", "Load exactly three fish, then click off."),
	ATTACK_TO_TEN(3, "Attack to 10%", "Fish from the spirit pool to about 10% essence."),
	LOAD_REMAINDER(3, "Load the rest", "Load the remaining fish into the hopper."),
	CATCH_27_FINAL(4, "Fish final 27", "Catch the final 27-fish inventory."),
	LOAD_FINAL(4, "Load final 27", "Load every fish into the highlighted hopper."),
	KILL_TEMPOROSS(4, "Finish Tempoross", "Fish from the spirit pool for the kill."),
	COMPLETE(4, "Leave", "Select Leave on a Spirit Angler NPC.");

	private final int trip;
	private final String title;
	private final String instruction;

	RouteStage(int trip, String title, String instruction)
	{
		this.trip = trip;
		this.title = title;
		this.instruction = instruction;
	}

	int getTrip()
	{
		return trip;
	}

	String getTitle()
	{
		return title;
	}

	String getInstruction()
	{
		return instruction;
	}

	RouteStage next()
	{
		RouteStage[] stages = values();
		return stages[Math.min(ordinal() + 1, stages.length - 1)];
	}

	RouteStage previous()
	{
		RouteStage[] stages = values();
		return stages[Math.max(ordinal() - 1, 0)];
	}
}
