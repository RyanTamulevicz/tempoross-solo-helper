package com.temporosssolo;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup(TemporossSoloHelperConfig.GROUP)
public interface TemporossSoloHelperConfig extends Config
{
	String GROUP = "tempoross-solo-helper";

	@ConfigItem(
		keyName = "method",
		name = "Method",
		description = "Selects the solo Tempoross strategy to guide"
	)
	default TemporossMethod method()
	{
		return TemporossMethod.NO_COOKING_MAX_XP;
	}

	@ConfigItem(
		keyName = "showPanel",
		name = "Show route panel",
		description = "Shows the current solo route step and progress"
	)
	default boolean showPanel()
	{
		return true;
	}

	@ConfigItem(
		keyName = "highlightTargets",
		name = "Highlight current target",
		description = "Highlights the current route target and active fires on the working shore"
	)
	default boolean highlightTargets()
	{
		return true;
	}

	@ConfigItem(
		keyName = "autoAdvance",
		name = "Auto-advance steps",
		description = "Advances the route from inventory, spirit-pool, and HUD state"
	)
	default boolean autoAdvance()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "highlightColor",
		name = "Target color",
		description = "Color used for the current route target"
	)
	default Color highlightColor()
	{
		return new Color(0, 255, 255, 220);
	}

	@Alpha
	@ConfigItem(
		keyName = "priorityColor",
		name = "Double-spot color",
		description = "Color used for priority double-fish spots"
	)
	default Color priorityColor()
	{
		return new Color(255, 215, 0, 230);
	}

	@Alpha
	@ConfigItem(
		keyName = "urgentColor",
		name = "Fire color",
		description = "Color used to highlight active fires"
	)
	default Color urgentColor()
	{
		return new Color(255, 170, 0, 230);
	}

	@ConfigItem(
		keyName = "nextStep",
		name = "Next-step hotkey",
		description = "Moves to the next route step; the panel also has a right-click action"
	)
	default Keybind nextStep()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
		keyName = "previousStep",
		name = "Previous-step hotkey",
		description = "Moves to the previous route step; the panel also has a right-click action"
	)
	default Keybind previousStep()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
		keyName = "resetRoute",
		name = "Reset-route hotkey",
		description = "Returns the selected method to its first step; the panel also has a right-click action"
	)
	default Keybind resetRoute()
	{
		return Keybind.NOT_SET;
	}
}
