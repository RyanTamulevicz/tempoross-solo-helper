package com.temporosssolo;

enum RouteStage
{
	CATCH_26(1, "Catch 25 or 26", "Run to the island and fill the available fish slots.", RouteTarget.FISH),
	LOAD_26(1, "Load first trip", "Load every fish at the hopper; tether if needed.", RouteTarget.LOAD),
	CATCH_27_FIRST(2, "Fish 27", "Return to the island and catch 27 fish.", RouteTarget.FISH),
	LOAD_27_FIRST(2, "Load all 27", "Load every fish into the highlighted hopper.", RouteTarget.LOAD),
	ATTACK_FIRST(2, "Attack Tempoross", "Fish from the spirit pool until it closes.", RouteTarget.ATTACK),
	CATCH_27_SECOND(3, "Fish 27", "Return to the island and catch another 27 fish.", RouteTarget.FISH),
	LOAD_THREE(3, "Load 3", "Load exactly three fish, then click off.", RouteTarget.LOAD),
	ATTACK_TO_TEN(3, "Attack to 10%", "Fish from the spirit pool to about 10% essence.", RouteTarget.ATTACK),
	LOAD_REMAINDER(3, "Load the rest", "Load the remaining fish into the hopper.", RouteTarget.LOAD),
	CATCH_27_FINAL(4, "Fish final 27", "Catch the final 27-fish inventory.", RouteTarget.FISH),
	LOAD_FINAL(4, "Load final 27", "Load every fish into the highlighted hopper.", RouteTarget.LOAD),
	KILL_TEMPOROSS(4, "Finish Tempoross", "Fish from the spirit pool for the kill.", RouteTarget.ATTACK),
	COMPLETE(4, "Leave", "Select Leave on a Spirit Angler NPC.", RouteTarget.LEAVE),

	MIX_CATCH_OPENING(1, "Fish opening batch", "Catch about 8 raw fish; tether if the game's wave appears.", RouteTarget.FISH),
	MIX_COOK_OPENING(1, "Cook opening batch", "Cook the opening fish at the highlighted shrine.", RouteTarget.COOK),
	MIX_CATCH_TO_17(1, "Fish to 17", "Return to fishing and reach exactly 17 total fish.", RouteTarget.FISH),
	MIX_COOK_TO_17(1, "Cook all 17", "Cook every raw fish; crystallised fish do not count.", RouteTarget.COOK),
	MIX_LOAD_17(1, "Load exactly 17", "Load 17 cooked fish; 1 optional raw may follow.", RouteTarget.LOAD),
	MIX_CATCH_19_FIRST(2, "Fish 19", "Target 19. If the HUD reaches 93%, use Next to cook/load early.", RouteTarget.FISH),
	MIX_COOK_19_FIRST(2, "Cook 19", "Cook all raw fish. At 93%, use Next and recover manually.", RouteTarget.COOK),
	MIX_LOAD_19_FIRST(2, "Load 19", "Load exactly 19 cooked fish; 1 optional raw may follow.", RouteTarget.LOAD),
	MIX_ATTACK_FIRST(2, "Attack Tempoross", "Normal: pool until closed. 93% recovery: ~40%, then return.", RouteTarget.ATTACK),
	MIX_CATCH_19_SECOND(3, "Fish 19", "Catch exactly 19 fish for the third trip.", RouteTarget.FISH),
	MIX_COOK_19_SECOND(3, "Cook 19", "Cook every raw fish; crystallised fish do not count.", RouteTarget.COOK),
	MIX_LOAD_19_SECOND(3, "Load 19", "Load exactly 19 cooked fish; 1 optional raw may follow.", RouteTarget.LOAD),
	MIX_ATTACK_SECOND(3, "Attack Tempoross", "Fish from the spirit pool until it closes again.", RouteTarget.ATTACK),
	MIX_CATCH_FINAL(4, "Fish final 28", "Catch the highlighted share of the final 28 fish.", RouteTarget.FISH),
	MIX_COOK_FINAL(4, "Cook final 28", "Cook every raw fish; crystallised fish do not count.", RouteTarget.COOK),
	MIX_LOAD_FINAL(4, "Load final 28", "Load this batch; if time is tight, put 6 in the other hopper.", RouteTarget.LOAD),
	MIX_KILL_TEMPOROSS(4, "Finish Tempoross", "Fish from the spirit pool for the kill.", RouteTarget.ATTACK),
	MIX_COMPLETE(4, "Leave", "Select Leave on a Spirit Angler NPC.", RouteTarget.LEAVE);

	private final int trip;
	private final String title;
	private final String instruction;
	private final RouteTarget target;

	RouteStage(int trip, String title, String instruction, RouteTarget target)
	{
		this.trip = trip;
		this.title = title;
		this.instruction = instruction;
		this.target = target;
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

	RouteTarget getTarget()
	{
		return target;
	}
}
