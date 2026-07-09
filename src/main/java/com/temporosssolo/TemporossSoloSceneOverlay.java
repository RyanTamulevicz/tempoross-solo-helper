package com.temporosssolo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

class TemporossSoloSceneOverlay extends Overlay
{
	private static final BasicStroke TARGET_STROKE = new BasicStroke(3f);

	private final Client client;
	private final TemporossSoloHelperPlugin plugin;
	private final TemporossSoloHelperConfig config;

	@Inject
	private TemporossSoloSceneOverlay(
		Client client,
		TemporossSoloHelperPlugin plugin,
		TemporossSoloHelperConfig config)
	{
		super(plugin);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.highlightTargets() || !plugin.isInEncounter() || !plugin.isRouteActive())
		{
			return null;
		}

		if (plugin.isWaveIncoming() && !plugin.isTethered())
		{
			renderObject(graphics, nearestObject(TemporossSoloHelperPlugin.TETHER_IDS),
				"Tether", config.urgentColor());
		}

		switch (plugin.getStage())
		{
			case CATCH_26:
			case CATCH_27_FIRST:
			case CATCH_27_SECOND:
			case CATCH_27_FINAL:
				renderNpc(graphics, nearestNpc(TemporossSoloHelperPlugin.FISHING_SPOT_IDS),
					"Fish", config.highlightColor());
				break;
			case LOAD_26:
				renderObject(graphics, nearestObject(TemporossSoloHelperPlugin.CANNON_IDS),
					"Load", config.highlightColor());
				if (plugin.getSnapshot().getWaterBuckets() > 0)
				{
					renderFire(graphics, "Douse ship fire", config.urgentColor());
				}
				break;
			case LOAD_27_FIRST:
			case LOAD_FIVE:
			case LOAD_REMAINDER:
			case LOAD_FINAL:
				renderObject(graphics, nearestObject(TemporossSoloHelperPlugin.CANNON_IDS),
					"Load", config.highlightColor());
				break;
			case TAKE_FIVE_BUCKETS:
			case TAKE_ONE_BUCKET:
				renderObject(graphics, nearestObject(TemporossSoloHelperPlugin.BUCKET_CRATE_IDS),
					"Take bucket", config.highlightColor());
				break;
			case HUMIDIFY_BUCKETS:
				renderWidget(graphics, client.getWidget(InterfaceID.MagicSpellbook.HUMIDIFY),
					"Humidify", config.highlightColor());
				renderObject(graphics, nearestObject(TemporossSoloHelperPlugin.WATER_PUMP_IDS),
					"Pump fallback", config.highlightColor());
				break;
			case DOUSE_FIRES:
				renderFire(graphics, "Douse", config.highlightColor());
				break;
			case DROP_BUCKETS:
				renderBucketItems(graphics);
				break;
			case ATTACK_FIRST:
			case ATTACK_TO_TWELVE:
			case KILL_TEMPOROSS:
				renderNpc(graphics, nearestNpc(TemporossSoloHelperPlugin.SPIRIT_POOL_IDS),
					"Harpoon Tempoross", config.highlightColor());
				break;
			case COMPLETE:
				break;
		}
		return null;
	}

	private void renderFire(Graphics2D graphics, String label, Color color)
	{
		NPC fireNpc = nearestNpc(TemporossSoloHelperPlugin.FIRE_NPC_IDS);
		if (fireNpc != null)
		{
			renderNpc(graphics, fireNpc, label, color);
			return;
		}
		renderObject(graphics, nearestObject(TemporossSoloHelperPlugin.FIRE_OBJECT_IDS), label, color);
	}

	private GameObject nearestObject(Set<Integer> ids)
	{
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return null;
		}

		GameObject nearest = null;
		int nearestDistance = Integer.MAX_VALUE;
		for (GameObject object : plugin.getTrackedObjects())
		{
			if (!ids.contains(object.getId()) || object.getPlane() != player.getWorldLocation().getPlane())
			{
				continue;
			}
			int distance = object.getLocalLocation().distanceTo(player.getLocalLocation());
			if (distance < nearestDistance)
			{
				nearestDistance = distance;
				nearest = object;
			}
		}
		return nearest;
	}

	private NPC nearestNpc(Set<Integer> ids)
	{
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return null;
		}

		NPC nearest = null;
		int nearestDistance = Integer.MAX_VALUE;
		for (NPC npc : plugin.getTrackedNpcs())
		{
			if (!ids.contains(npc.getId()))
			{
				continue;
			}
			int distance = npc.getLocalLocation().distanceTo(player.getLocalLocation());
			if (distance < nearestDistance)
			{
				nearestDistance = distance;
				nearest = npc;
			}
		}
		return nearest;
	}

	private void renderObject(Graphics2D graphics, GameObject object, String label, Color color)
	{
		if (object == null)
		{
			return;
		}

		Shape shape = object.getClickbox();
		if (shape == null)
		{
			shape = object.getConvexHull();
		}
		if (shape == null)
		{
			shape = object.getCanvasTilePoly();
		}
		if (shape != null)
		{
			OverlayUtil.renderPolygon(graphics, shape, color, fill(color), TARGET_STROKE);
		}
		Point textLocation = object.getCanvasTextLocation(graphics, label, 40);
		if (textLocation != null)
		{
			OverlayUtil.renderTextLocation(graphics, textLocation, label, color);
		}
	}

	private void renderNpc(Graphics2D graphics, NPC npc, String label, Color color)
	{
		if (npc == null)
		{
			return;
		}

		Shape shape = npc.getConvexHull();
		if (shape == null)
		{
			shape = npc.getCanvasTilePoly();
		}
		if (shape != null)
		{
			OverlayUtil.renderPolygon(graphics, shape, color, fill(color), TARGET_STROKE);
		}
		Point textLocation = npc.getCanvasTextLocation(graphics, label, npc.getLogicalHeight() + 40);
		if (textLocation != null)
		{
			OverlayUtil.renderTextLocation(graphics, textLocation, label, color);
		}
	}

	private void renderBucketItems(Graphics2D graphics)
	{
		Widget inventory = client.getWidget(InterfaceID.Inventory.ITEMS);
		if (inventory == null || inventory.isHidden())
		{
			return;
		}

		Widget[] items = inventory.getDynamicChildren();
		for (Widget item : items)
		{
			if (item.getItemId() == ItemID.BUCKET_EMPTY || item.getItemId() == ItemID.BUCKET_WATER)
			{
				renderWidget(graphics, item, "Drop", config.highlightColor());
			}
		}
	}

	private static void renderWidget(Graphics2D graphics, Widget widget, String label, Color color)
	{
		if (widget == null || widget.isHidden())
		{
			return;
		}

		Rectangle bounds = widget.getBounds();
		graphics.setColor(fill(color));
		graphics.fill(bounds);
		graphics.setColor(color);
		graphics.setStroke(TARGET_STROKE);
		graphics.draw(bounds);

		FontMetrics metrics = graphics.getFontMetrics();
		int x = bounds.x + (bounds.width - metrics.stringWidth(label)) / 2;
		int y = bounds.y + Math.max(metrics.getAscent(), (bounds.height + metrics.getAscent()) / 2);
		graphics.drawString(label, x, y);
	}

	private static Color fill(Color color)
	{
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), 55);
	}
}
