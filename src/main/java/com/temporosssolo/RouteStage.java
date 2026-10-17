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

	MIX_FISH_17(1, "Fish 17 harpoonfish", "Cook when you've caught the first 8 or so, until a double fish spot comes up.", RouteTarget.FISH),
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
	MIX_FISH_FINAL_28(4, "Fish 28 harpoonfish", "Fish until a double fish spot comes up, cook after it expires, and fish the double spot again when it spawns.", RouteTarget.FISH),
	MIX_COOK_FINAL_28(4, "Cook all", "Cook all the fish.", RouteTarget.COOK),
	MIX_LOAD_FINAL_28(4, "Load 14 in EACH cannon", "Load 14 fish into first cannon.", RouteTarget.LOAD),
	MIX_ATTACK_FINAL(4, "Attack Tempoross", "If bad RNG prevents the kill, the two cannons will redown him. Wait, then finish him off.", RouteTarget.ATTACK),
	MIX_POST_KILL_BUCKETS(4, "Take & fill 5 buckets", "Take 5 buckets and fill them with water before getting kicked out.", RouteTarget.NONE),

	PERMITS_FISH_17(1, "Fish 17 harpoonfish", "Fish the nearest spot. Cook after 9, then fish the double spot as soon as it appears.", RouteTarget.FISH),
	PERMITS_COOK_17(1, "Cook all 17", "Cook all 17. If the colossal wave starts, tether to the totem pole.", RouteTarget.COOK),
	PERMITS_LOAD_17(1, "Load exactly 17", "Load exactly 17 into the closest crate. Energy should drop to 4%.", RouteTarget.LOAD),
	PERMITS_DOUSE_SHIP(1, "Douse the ship fire", "Douse the ship flame. Douse island fires too, or leave them to spread, but watch your tile.", RouteTarget.DOUSE),
	PERMITS_FISH_19_FIRST(1, "Fish 19 harpoonfish", "Catch 19 as quickly as possible. Leave for the ship by 93% storm (90% if fires block the path).", RouteTarget.FISH),
	PERMITS_COOK_19_FIRST(1, "Cook all 19", "Cook all 19. Leave for the ship by 93% storm (90% if fires block the path).", RouteTarget.COOK),
	PERMITS_LOAD_19_FIRST(1, "Load all 19", "Load all 19. If short on time, load what you have; the helper tracks the rest.", RouteTarget.LOAD),
	PERMITS_ATTACK_FIRST(1, "Attack Tempoross", "Fish the spirit pool until Tempoross regains its energy.", RouteTarget.ATTACK),
	PERMITS_FISH_19_SECOND(2, "Fish 19 harpoonfish", "Catch 19. Optionally douse spreading fires with 5-10 buckets while waiting for double spots; drop extras.", RouteTarget.FISH),
	PERMITS_COOK_19_SECOND(2, "Cook all 19", "Cook all 19 before the storm reaches 93%.", RouteTarget.COOK),
	PERMITS_LOAD_19_SECOND(2, "Load all 19", "Load all 19 into the cannon.", RouteTarget.LOAD),
	PERMITS_ATTACK_SECOND(2, "Attack Tempoross", "Damage Tempoross to below 60% essence, ideally below 50%.", RouteTarget.ATTACK),
	PERMITS_FIREFIGHT(3, "Fight the fires", "Fill your inventory with buckets from the dock crate, cast Humidify, and douse fires around the island. Watch the storm.", RouteTarget.DOUSE),
	PERMITS_FISH_2(3, "Catch 2 fish", "Catch 2 more fish before the storm gets too high. Cook them only if no fires remain.", RouteTarget.FISH),
	PERMITS_LOAD_2(3, "Load 2 fish", "Load both fish into the cannon. Do not run through any fires.", RouteTarget.LOAD),
	PERMITS_KILL(3, "Finish Tempoross", "Finish Tempoross at the spirit pool. Douse more fires only if there is plenty of time.", RouteTarget.ATTACK),
	PERMITS_COMPLETE(3, "Leave", "Refill buckets of water for the next game, then select Leave on a Spirit Angler NPC.", RouteTarget.LEAVE);

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
