package com.temporosssolo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.MenuAction;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.ProgressBarComponent;
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
		addMenuEntry(MenuAction.RUNELITE_OVERLAY, "Next step", "Tempoross route", entry -> plugin.nextStep());
		addMenuEntry(MenuAction.RUNELITE_OVERLAY, "Previous step", "Tempoross route", entry -> plugin.previousStep());
		addMenuEntry(MenuAction.RUNELITE_OVERLAY, "Reset route", "Tempoross route", entry -> plugin.resetRoute());
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showPanel() || !plugin.isInEncounter())
		{
			return null;
		}

		RouteStage stage = plugin.getStage();
		panelComponent.getChildren().add(TitleComponent.builder()
			.text(stage.getTitle())
			.color(config.highlightColor())
			.build());
		panelComponent.getChildren().add(LineComponent.builder()
			.left(plugin.getMethod().toString())
			.leftColor(config.priorityColor())
			.build());
		panelComponent.getChildren().add(LineComponent.builder()
			.left("TRIP " + stage.getTrip() + " OF " + plugin.getTripCount())
			.leftColor(config.highlightColor())
			.right(config.autoAdvance() ? "AUTO" : "MANUAL")
			.rightColor(config.autoAdvance() ? new Color(120, 220, 140) : Color.LIGHT_GRAY)
			.build());

		ProgressBarComponent routeProgress = new ProgressBarComponent();
		routeProgress.setMinimum(0);
		routeProgress.setMaximum(plugin.getStageCount());
		routeProgress.setValue(plugin.getStageNumber());
		routeProgress.setLabelDisplayMode(ProgressBarComponent.LabelDisplayMode.TEXT_ONLY);
		routeProgress.setCenterLabel(
			"Step " + plugin.getStageNumber() + " of " + plugin.getStageCount());
		routeProgress.setForegroundColor(config.highlightColor());
		routeProgress.setBackgroundColor(new Color(55, 55, 55, 210));
		panelComponent.getChildren().add(routeProgress);

		panelComponent.getChildren().add(LineComponent.builder()
			.left(plugin.getInstructionText())
			.leftColor(Color.WHITE)
			.build());
		panelComponent.getChildren().add(LineComponent.builder()
			.left("Progress")
			.right(plugin.getProgressText())
			.rightColor(config.highlightColor())
			.build());
		panelComponent.getChildren().add(LineComponent.builder()
			.left("Right-click for Previous / Next / Reset")
			.leftColor(Color.LIGHT_GRAY)
			.build());
		return super.render(graphics);
	}
}
