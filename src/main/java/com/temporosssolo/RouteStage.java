package com.temporosssolo;

enum RouteStage
{
	CATCH_26("Catch 26", "Fish the highlighted harpoonfish spot."),
	LOAD_26("Load + tether", "Load every fish; tether and douse the ship fire."),
	TAKE_FIVE_BUCKETS("Take 5 buckets", "Take five more buckets, for six total."),
	HUMIDIFY_BUCKETS("Humidify", "Fill the buckets with Humidify or the pump."),
	DOUSE_FIRES("Douse fires", "Use the water buckets on active fires."),
	DROP_BUCKETS("Drop all buckets", "Drop every empty or filled bucket."),
	CATCH_27_FIRST("Fish 27", "Catch a full 27-fish inventory."),
	LOAD_27_FIRST("Load all 27", "Load every fish into a cannon."),
	ATTACK_FIRST("Attack Tempoross", "Harpoon the spirit pool until it closes."),
	CATCH_27_SECOND("Fish 27", "Catch another full 27-fish inventory."),
	LOAD_FIVE("Load 5", "Load exactly five fish, leaving 22."),
	ATTACK_TO_TWELVE("Attack to 12%", "Harpoon the spirit pool to 12% essence."),
	LOAD_REMAINDER("Load the rest", "Load all 22 remaining fish."),
	CATCH_27_FINAL("Fish final 27", "Catch the final 27-fish inventory."),
	LOAD_FINAL("Load final 27", "Load every fish into a cannon."),
	KILL_TEMPOROSS("Finish Tempoross", "Harpoon the spirit pool for the kill."),
	TAKE_ONE_BUCKET("Take 1 bucket", "Take one bucket for the next game."),
	COMPLETE("Route complete", "Keep the bucket and leave with the crew.");

	private final String title;
	private final String instruction;

	RouteStage(String title, String instruction)
	{
		this.title = title;
		this.instruction = instruction;
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
