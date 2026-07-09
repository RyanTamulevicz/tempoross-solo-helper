package com.temporosssolo;

import com.google.inject.Provides;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.NPC;
import net.runelite.api.Tile;
import net.runelite.api.WorldView;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

@Slf4j
@PluginDescriptor(
	name = "Tempoross Solo Helper",
	description = "Quest-style guidance for the catch-26 solo Tempoross route",
	tags = {"tempoross", "solo", "fishing", "helper"}
)
public class TemporossSoloHelperPlugin extends Plugin
{
	private static final String WAVE_INCOMING_MESSAGE = "a colossal wave closes in";
	private static final String WAVE_SAFE_MESSAGE = "as the wave washes over you";
	private static final String WAVE_HIT_MESSAGE = "the wave slams into you";
	private static final Pattern PERCENT_PATTERN = Pattern.compile("(\\d{1,3})%");

	static final Set<Integer> FISHING_SPOT_IDS = Set.of(
		NpcID.TEMPOROSS_HARPOONFISH_FISHINGSPOT_NORTH,
		NpcID.TEMPOROSS_HARPOONFISH_FISHINGSPOT_SOUTH,
		NpcID.TEMPOROSS_HARPOONFISH_FISHINGSPOT_SPECIAL);
	static final Set<Integer> SPIRIT_POOL_IDS = Set.of(NpcID.TEMPOROSS_P2_FISHINGSPOT);
	static final Set<Integer> FIRE_NPC_IDS = Set.of(NpcID.TEMPOROSS_FIRE_HITBOX);
	private static final Set<Integer> VICTORY_NPC_IDS = Set.of(
		NpcID.TEMPOROSS_INSTANCE_HOST_E_VICTORY,
		NpcID.TEMPOROSS_INSTANCE_HOST_W_VICTORY,
		NpcID.TEMPOROSS_INSTANCE_HOST_N_VICTORY,
		NpcID.TEMPOROSS_INSTANCE_HOST_S_VICTORY);
	private static final Set<Integer> TRACKED_NPC_IDS = Set.of(
		NpcID.TEMPOROSS_HARPOONFISH_FISHINGSPOT_NORTH,
		NpcID.TEMPOROSS_HARPOONFISH_FISHINGSPOT_SOUTH,
		NpcID.TEMPOROSS_HARPOONFISH_FISHINGSPOT_SPECIAL,
		NpcID.TEMPOROSS_P2_FISHINGSPOT_INACTIVE,
		NpcID.TEMPOROSS_P2_FISHINGSPOT,
		NpcID.TEMPOROSS_FIRE_HITBOX,
		NpcID.TEMPOROSS_INSTANCE_HOST_E_VICTORY,
		NpcID.TEMPOROSS_INSTANCE_HOST_W_VICTORY,
		NpcID.TEMPOROSS_INSTANCE_HOST_N_VICTORY,
		NpcID.TEMPOROSS_INSTANCE_HOST_S_VICTORY);

	static final Set<Integer> CANNON_IDS = Set.of(
		ObjectID.TEMPOROSS_CANNON_1,
		ObjectID.TEMPOROSS_CANNON_2,
		ObjectID.TEMPOROSS_CANNON_3,
		ObjectID.TEMPOROSS_CANNON_4);
	static final Set<Integer> BUCKET_CRATE_IDS = Set.of(ObjectID.TEMPOROSS_CRATE_BUCKET);
	static final Set<Integer> WATER_PUMP_IDS = Set.of(
		ObjectID.TEMPOROSS_WATER_PUMP,
		ObjectID.TEMPOROSS_WATER_PUMP_2,
		ObjectID.TEMPOROSS_WATER_PUMP_BOTTOM_1,
		ObjectID.TEMPOROSS_WATER_PUMP_BOTTOM_2,
		ObjectID.TEMPOROSS_WATER_PUMP_DOCK);
	static final Set<Integer> TETHER_IDS = Set.of(
		ObjectID.TEMPOROSS_MAST_BOTTOM_UNTETHERED,
		ObjectID.TEMPOROSS_TOTEM_UNTETHERED,
		ObjectID.TEMPOROSS_MAST_BOTTOM_WEST,
		ObjectID.TEMPOROSS_MAST_BOTTOM_EAST,
		ObjectID.TEMPOROSS_TOTEM_NORTH,
		ObjectID.TEMPOROSS_TOTEM_SOUTH);
	static final Set<Integer> FIRE_OBJECT_IDS = Set.of(ObjectID.TEMPOROSS_FIRE_VISUALS);
	private static final Set<Integer> TRACKED_OBJECT_IDS = Set.of(
		ObjectID.TEMPOROSS_CANNON_1,
		ObjectID.TEMPOROSS_CANNON_2,
		ObjectID.TEMPOROSS_CANNON_3,
		ObjectID.TEMPOROSS_CANNON_4,
		ObjectID.TEMPOROSS_CRATE_BUCKET,
		ObjectID.TEMPOROSS_WATER_PUMP,
		ObjectID.TEMPOROSS_WATER_PUMP_2,
		ObjectID.TEMPOROSS_WATER_PUMP_BOTTOM_1,
		ObjectID.TEMPOROSS_WATER_PUMP_BOTTOM_2,
		ObjectID.TEMPOROSS_WATER_PUMP_DOCK,
		ObjectID.TEMPOROSS_MAST_BOTTOM_UNTETHERED,
		ObjectID.TEMPOROSS_TOTEM_UNTETHERED,
		ObjectID.TEMPOROSS_MAST_BOTTOM_WEST,
		ObjectID.TEMPOROSS_MAST_BOTTOM_EAST,
		ObjectID.TEMPOROSS_TOTEM_NORTH,
		ObjectID.TEMPOROSS_TOTEM_SOUTH,
		ObjectID.TEMPOROSS_FIRE_VISUALS);

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private KeyManager keyManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private TemporossSoloHelperConfig config;

	@Inject
	private TemporossSoloPanelOverlay panelOverlay;

	@Inject
	private TemporossSoloSceneOverlay sceneOverlay;

	private final TemporossRoute route = new TemporossRoute();
	private final Set<GameObject> trackedObjects = Collections.newSetFromMap(new IdentityHashMap<>());
	private final Set<NPC> trackedNpcs = Collections.newSetFromMap(new IdentityHashMap<>());

	private boolean inEncounter;
	private boolean routeActive;
	private boolean waveIncoming;
	private boolean defeated;
	private int missingHudTicks;
	private RouteSnapshot snapshot = new RouteSnapshot(0, 0, 0, null, false, false);

	private final HotkeyListener nextStepListener = new HotkeyListener(() -> config.nextStep())
	{
		@Override
		public void hotkeyPressed()
		{
			nextStep();
		}
	};

	private final HotkeyListener previousStepListener = new HotkeyListener(() -> config.previousStep())
	{
		@Override
		public void hotkeyPressed()
		{
			previousStep();
		}
	};

	private final HotkeyListener resetRouteListener = new HotkeyListener(() -> config.resetRoute())
	{
		@Override
		public void hotkeyPressed()
		{
			resetRoute();
		}
	};

	@Provides
	TemporossSoloHelperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TemporossSoloHelperConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(panelOverlay);
		overlayManager.add(sceneOverlay);
		keyManager.registerKeyListener(nextStepListener);
		keyManager.registerKeyListener(previousStepListener);
		keyManager.registerKeyListener(resetRouteListener);
		clientThread.invoke(this::checkEncounterState);
		log.debug("Tempoross Solo Helper started");
	}

	@Override
	protected void shutDown()
	{
		keyManager.unregisterKeyListener(nextStepListener);
		keyManager.unregisterKeyListener(previousStepListener);
		keyManager.unregisterKeyListener(resetRouteListener);
		overlayManager.remove(panelOverlay);
		overlayManager.remove(sceneOverlay);
		routeActive = false;
		clearEncounter();
		log.debug("Tempoross Solo Helper stopped");
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		checkEncounterState();
		if (!inEncounter)
		{
			return;
		}

		snapshot = buildSnapshot();
		if (routeActive && config.autoAdvance())
		{
			route.update(snapshot);
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOADING
			|| event.getGameState() == GameState.LOGIN_SCREEN
			|| event.getGameState() == GameState.HOPPING)
		{
			trackedObjects.clear();
			trackedNpcs.clear();
			waveIncoming = false;
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		trackObject(event.getGameObject());
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		trackedObjects.remove(event.getGameObject());
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		trackNpc(event.getNpc());
	}

	@Subscribe
	public void onNpcChanged(NpcChanged event)
	{
		trackNpc(event.getNpc());
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		trackedNpcs.remove(event.getNpc());
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.GAMEMESSAGE)
		{
			return;
		}

		String message = event.getMessage().toLowerCase();
		if (message.contains(WAVE_INCOMING_MESSAGE))
		{
			waveIncoming = true;
		}
		else if (message.contains(WAVE_SAFE_MESSAGE) || message.contains(WAVE_HIT_MESSAGE))
		{
			waveIncoming = false;
		}
	}

	void nextStep()
	{
		clientThread.invoke(route::next);
	}

	void previousStep()
	{
		clientThread.invoke(route::previous);
	}

	void resetRoute()
	{
		clientThread.invoke(route::reset);
	}

	void startRoute()
	{
		clientThread.invoke(() ->
		{
			routeActive = true;
			route.reset();
			if (inEncounter)
			{
				snapshot = buildSnapshot();
			}
			log.debug("Catch-26 route started manually");
		});
	}

	void stopRoute()
	{
		clientThread.invoke(() ->
		{
			routeActive = false;
			route.reset();
			log.debug("Catch-26 route stopped manually");
		});
	}

	boolean isInEncounter()
	{
		return inEncounter;
	}

	boolean isRouteActive()
	{
		return routeActive;
	}

	boolean isTemporossContextVisible()
	{
		return inEncounter || isVisible(client.getWidget(InterfaceID.TemporossLobbyHud.UNIVERSE));
	}

	boolean isWaveIncoming()
	{
		return waveIncoming;
	}

	boolean isTethered()
	{
		return client.getVarbitValue(VarbitID.TEMPOROSS_TETHERED) > 0;
	}

	RouteStage getStage()
	{
		return route.getStage();
	}

	RouteSnapshot getSnapshot()
	{
		return snapshot;
	}

	Set<GameObject> getTrackedObjects()
	{
		return trackedObjects;
	}

	Set<NPC> getTrackedNpcs()
	{
		return trackedNpcs;
	}

	String getProgressText()
	{
		switch (route.getStage())
		{
			case CATCH_26:
				return snapshot.getFish() + " / " + TemporossRoute.FIRST_FISH_TARGET + " fish";
			case LOAD_26:
			case LOAD_27_FIRST:
			case LOAD_REMAINDER:
			case LOAD_FINAL:
				return snapshot.getFish() + " fish left";
			case TAKE_FIVE_BUCKETS:
				return snapshot.getBuckets() + " / " + TemporossRoute.BUCKET_TARGET + " buckets";
			case HUMIDIFY_BUCKETS:
				return snapshot.getWaterBuckets() + " filled, " + snapshot.getEmptyBuckets() + " empty";
			case DOUSE_FIRES:
				return snapshot.getWaterBuckets() + " water buckets left";
			case DROP_BUCKETS:
				return snapshot.getBuckets() + " buckets left";
			case CATCH_27_FIRST:
			case CATCH_27_SECOND:
			case CATCH_27_FINAL:
				return snapshot.getFish() + " / " + TemporossRoute.FULL_FISH_TARGET + " fish";
			case ATTACK_FIRST:
			case KILL_TEMPOROSS:
				return formatEssence();
			case LOAD_FIVE:
				return Math.max(0, TemporossRoute.FULL_FISH_TARGET - snapshot.getFish()) + " / 5 loaded";
			case ATTACK_TO_TWELVE:
				return formatEssence() + " -> " + TemporossRoute.ESSENCE_TARGET + "%";
			case TAKE_ONE_BUCKET:
				return snapshot.getBuckets() + " / 1 bucket";
			case COMPLETE:
				return "Ready for the next game";
			default:
				return "";
		}
	}

	private String formatEssence()
	{
		return snapshot.getEssencePercent() == null
			? "Essence: read HUD"
			: "Essence: " + snapshot.getEssencePercent() + "%";
	}

	private void checkEncounterState()
	{
		boolean hudVisible = isVisible(client.getWidget(InterfaceID.TemporossHud.UNIVERSE))
			|| isVisible(client.getWidget(InterfaceID.TemporossHud.STATUS))
			|| isVisible(client.getWidget(InterfaceID.TemporossHud.ESSENCE_BAR_CONTAINER));
		if (hudVisible)
		{
			missingHudTicks = 0;
			if (!inEncounter)
			{
				beginEncounter();
			}
		}
		else if (inEncounter && ++missingHudTicks >= 3)
		{
			clearEncounter();
		}
	}

	private static boolean isVisible(Widget widget)
	{
		return widget != null && !widget.isHidden();
	}

	private void beginEncounter()
	{
		inEncounter = true;
		waveIncoming = false;
		defeated = false;
		route.reset();
		snapshot = buildSnapshot();
		scanSceneOnce();
		log.debug("Tempoross encounter detected; route reset");
	}

	private void clearEncounter()
	{
		inEncounter = false;
		waveIncoming = false;
		defeated = false;
		missingHudTicks = 0;
		trackedObjects.clear();
		trackedNpcs.clear();
		route.reset();
		snapshot = new RouteSnapshot(0, 0, 0, null, false, false);
	}

	private RouteSnapshot buildSnapshot()
	{
		int fish = 0;
		int emptyBuckets = 0;
		int waterBuckets = 0;
		ItemContainer inventory = client.getItemContainer(InventoryID.INV);
		if (inventory != null)
		{
			for (Item item : inventory.getItems())
			{
				switch (item.getId())
				{
					case ItemID.TEMPOROSS_RAW_HARPOONFISH:
					case ItemID.TEMPOROSS_HARPOONFISH:
					case ItemID.TEMPOROSS_CRYSTALLISED_HARPOONFISH:
						fish += item.getQuantity();
						break;
					case ItemID.BUCKET_EMPTY:
						emptyBuckets += item.getQuantity();
						break;
					case ItemID.BUCKET_WATER:
						waterBuckets += item.getQuantity();
						break;
					default:
						break;
				}
			}
		}

		return new RouteSnapshot(
			fish,
			emptyBuckets,
			waterBuckets,
			readEssencePercent(),
			containsNpc(SPIRIT_POOL_IDS),
			defeated);
	}

	private Integer readEssencePercent()
	{
		Widget essence = client.getWidget(InterfaceID.TemporossHud.ESSENCE);
		Integer textPercent = findPercent(essence);
		if (textPercent != null)
		{
			return textPercent;
		}

		Widget bar = client.getWidget(InterfaceID.TemporossHud.ESSENCE_BAR);
		Widget back = client.getWidget(InterfaceID.TemporossHud.ESSENCE_BAR_BACK);
		if (isVisible(bar) && isVisible(back) && back.getBounds().width > 0)
		{
			return Math.max(0, Math.min(100,
				Math.round(100f * bar.getBounds().width / back.getBounds().width)));
		}
		return null;
	}

	private static Integer findPercent(Widget widget)
	{
		if (widget == null)
		{
			return null;
		}

		Matcher matcher = PERCENT_PATTERN.matcher(widget.getText() == null ? "" : widget.getText());
		if (matcher.find())
		{
			return Integer.parseInt(matcher.group(1));
		}

		Widget[] children = widget.getChildren();
		if (children != null)
		{
			for (Widget child : children)
			{
				Integer childPercent = findPercent(child);
				if (childPercent != null)
				{
					return childPercent;
				}
			}
		}
		return null;
	}

	private boolean containsNpc(Set<Integer> ids)
	{
		for (NPC npc : trackedNpcs)
		{
			if (ids.contains(npc.getId()))
			{
				return true;
			}
		}
		return false;
	}

	private void scanSceneOnce()
	{
		trackedObjects.clear();
		trackedNpcs.clear();
		WorldView worldView = client.getTopLevelWorldView();
		if (worldView == null)
		{
			return;
		}

		for (Tile[][] plane : worldView.getScene().getTiles())
		{
			for (Tile[] row : plane)
			{
				for (Tile tile : row)
				{
					if (tile == null)
					{
						continue;
					}
					for (GameObject gameObject : tile.getGameObjects())
					{
						if (gameObject != null)
						{
							trackObject(gameObject);
						}
					}
				}
			}
		}

		for (NPC npc : worldView.npcs())
		{
			trackNpc(npc);
		}
	}

	private void trackObject(GameObject gameObject)
	{
		if (TRACKED_OBJECT_IDS.contains(gameObject.getId()))
		{
			trackedObjects.add(gameObject);
		}
	}

	private void trackNpc(NPC npc)
	{
		if (TRACKED_NPC_IDS.contains(npc.getId()))
		{
			trackedNpcs.add(npc);
		}
		if (VICTORY_NPC_IDS.contains(npc.getId()))
		{
			defeated = true;
		}
	}
}
