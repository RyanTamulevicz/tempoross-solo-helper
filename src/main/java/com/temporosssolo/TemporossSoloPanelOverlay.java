package com.temporosssolo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.MenuAction;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

class TemporossSoloPanelOverlay extends OverlayPanel
{
	private static final int PANEL_WIDTH = 260;

	private final TemporossSoloHelperPlugin plugin;
	private final TemporossSoloHelperConfig config;

	@Inject
	private TemporossSoloPanelOverlay(
		TemporossSoloHelperPlugin plugin,
		TemporossSoloHelperConfig config)
	{
		super(plugin);
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.BOTTOM_LEFT);
		panelComponent.setPreferredSize(new Dimension(PANEL_WIDTH, 0));
		addMenuEntry(MenuAction.RUNELITE_OVERLAY, "Start route", "Catch-26 route", entry -> plugin.startRoute());
		addMenuEntry(MenuAction.RUNELITE_OVERLAY, "Stop route", "Catch-26 route", entry -> plugin.stopRoute());
		addMenuEntry(MenuAction.RUNELITE_OVERLAY, "Next step", "Catch-26 route", entry -> plugin.nextStep());
		addMenuEntry(MenuAction.RUNELITE_OVERLAY, "Previous step", "Catch-26 route", entry -> plugin.previousStep());
		addMenuEntry(MenuAction.RUNELITE_OVERLAY, "Reset route", "Catch-26 route", entry -> plugin.resetRoute());
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showPanel() || !plugin.isTemporossContextVisible())
		{
			return null;
		}

		if (!plugin.isRouteActive())
		{
			panelComponent.getChildren().add(TitleComponent.builder()
				.text("Catch-26 helper inactive")
				.color(Color.ORANGE)
				.build());
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Right-click this panel.")
				.leftColor(Color.WHITE)
				.build());
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Choose Start route to arm the helper.")
				.leftColor(Color.WHITE)
				.build());
			return super.render(graphics);
		}

		if (!plugin.isInEncounter())
		{
			panelComponent.getChildren().add(TitleComponent.builder()
				.text("Catch-26 helper armed")
				.color(config.highlightColor())
				.build());
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Waiting for the next Tempoross encounter.")
				.leftColor(Color.WHITE)
				.build());
			return super.render(graphics);
		}

		RouteStage stage = plugin.getStage();
		panelComponent.getChildren().add(TitleComponent.builder()
			.text(stage.getTitle())
			.color(config.highlightColor())
			.build());
		panelComponent.getChildren().add(LineComponent.builder()
			.left("Step")
			.right((stage.ordinal() + 1) + " / " + RouteStage.values().length)
			.build());
		panelComponent.getChildren().add(LineComponent.builder()
			.left(stage.getInstruction())
			.leftColor(Color.WHITE)
			.build());
		panelComponent.getChildren().add(LineComponent.builder()
			.left("Progress")
			.right(plugin.getProgressText())
			.rightColor(config.highlightColor())
			.build());
		panelComponent.getChildren().add(LineComponent.builder()
			.left("Right-click panel: Previous / Next / Reset / Stop")
			.leftColor(Color.LIGHT_GRAY)
			.build());
		return super.render(graphics);
	}
}
