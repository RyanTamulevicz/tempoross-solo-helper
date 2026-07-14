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

	MIX_FISH_17(1, "Fish 17 harpoonfish", "Setup: 5 buckets of water. Cook when you've caught the first 8 or so, until a double fish spot comes up.", RouteTarget.FISH),
	MIX_COOK_17(1, "Cook all the fish", "Cook all the fish.", RouteTarget.COOK),
	MIX_LOAD_17(1, "Load all 17 fish", "Load all 17 fish.", RouteTarget.LOAD),
	MIX_FIRES_FIRST(1, "Fires should come up now", "Usually 4 fires appear, but sometimes 1 spreads. Drop buckets after this.", RouteTarget.FISH),
	MIX_FISH_19_FIRST(2, "Fish 19 harpoonfish", "Same thing as before: do a few, then cook until a double fish spot comes up.", RouteTarget.FISH),
	MIX_COOK_19_FIRST(2, "Cook all", "Cook all the fish.", RouteTarget.COOK),
	MIX_LOAD_19_FIRST(2, "Load all 19 fish", "Tempoross will go down, but just keep loading it all.", RouteTarget.LOAD),
	MIX_ATTACK_FIRST(2, "Attack Tempoross", "Attack Tempoross.", RouteTarget.ATTACK),
	MIX_FISH_19_SECOND(3, "Fish 19 harpoonfish", "Same thing as before: do a few, then cook until a double fish spot comes up.", RouteTarget.FISH),
	MIX_COOK_19_SECOND(3, "Cook all", "Cook all the fish.", RouteTarget.COOK),
	MIX_LOAD_19_SECOND(3, "Load all 19 fish", "Tempoross will go down, but just keep loading it all.", RouteTarget.LOAD),
	MIX_ATTACK_SECOND(3, "Attack Tempoross", "Attack Tempoross.", RouteTarget.ATTACK),
	MIX_FISH_FINAL_28(4, "Fish 28 harpoonfish", "Same thing as before: do a few, then cook until a double fish spot comes up.", RouteTarget.FISH),
	MIX_COOK_FINAL_28(4, "Cook all", "Cook all the fish.", RouteTarget.COOK),
	MIX_LOAD_FINAL_28(4, "Load 14 in EACH cannon", "Tempoross will go down, but just keep loading it all.", RouteTarget.LOAD),
	MIX_ATTACK_FINAL(4, "Attack Tempoross", "If bad RNG prevents the kill, the two cannons will redown him. Wait, then finish him off.", RouteTarget.ATTACK),
	MIX_POST_KILL_BUCKETS(4, "Collect & fill 5 buckets", "Once he dies, collect 5 more buckets and fill them before getting teleported out.", RouteTarget.NONE);

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
