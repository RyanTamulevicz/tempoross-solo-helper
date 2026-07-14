package com.temporosssolo;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.NPC;
import net.runelite.api.Tile;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

@Slf4j
@PluginDescriptor(
	name = "Tempoross Solo Helper",
	description = "Quest-style guidance for selectable solo Tempoross methods",
	tags = {"tempoross", "solo", "fishing", "helper"}
)
public class TemporossSoloHelperPlugin extends Plugin
{
	private static final String LOAD_STARTED_MESSAGE = "you start loading harpoonfish into the cannon";
	private static final int LOAD_CONFIRM_DISTANCE_TILES = 6;
	private static final int LOAD_CANDIDATE_MAX_AGE_TICKS = 3;
	private static final Pattern PERCENT_PATTERN = Pattern.compile("(\\d{1,3})%");

	static final Set<Integer> FISHING_SPOT_IDS = Set.of(
		NpcID.TEMPOROSS_HARPOONFISH_FISHINGSPOT_NORTH,
		NpcID.TEMPOROSS_HARPOONFISH_FISHINGSPOT_SOUTH,
		NpcID.TEMPOROSS_HARPOONFISH_FISHINGSPOT_SPECIAL);
	static final Set<Integer> DOUBLE_FISHING_SPOT_IDS = Set.of(
		NpcID.TEMPOROSS_HARPOONFISH_FISHINGSPOT_SPECIAL);
	static final Set<Integer> SPIRIT_POOL_IDS = Set.of(NpcID.TEMPOROSS_P2_FISHINGSPOT);
	static final Set<Integer> FIRE_NPC_IDS = Set.of(NpcID.TEMPOROSS_FIRE_HITBOX);
	static final Set<Integer> AMMUNITION_CRATE_IDS = Set.of(
		NpcID.TEMPOROSS_NPC_CRATE_AMMUNITION_1,
		NpcID.TEMPOROSS_NPC_CRATE_AMMUNITION_2,
		NpcID.TEMPOROSS_NPC_CRATE_AMMUNITION_3,
		NpcID.TEMPOROSS_NPC_CRATE_AMMUNITION_4);
	static final Set<Integer> VICTORY_NPC_IDS = Set.of(
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
		NpcID.TEMPOROSS_NPC_CRATE_AMMUNITION_1,
		NpcID.TEMPOROSS_NPC_CRATE_AMMUNITION_2,
		NpcID.TEMPOROSS_NPC_CRATE_AMMUNITION_3,
		NpcID.TEMPOROSS_NPC_CRATE_AMMUNITION_4,
		NpcID.TEMPOROSS_INSTANCE_HOST_E_VICTORY,
		NpcID.TEMPOROSS_INSTANCE_HOST_W_VICTORY,
		NpcID.TEMPOROSS_INSTANCE_HOST_N_VICTORY,
		NpcID.TEMPOROSS_INSTANCE_HOST_S_VICTORY);

	static final Set<Integer> FIRE_OBJECT_IDS = Set.of(ObjectID.TEMPOROSS_FIRE_VISUALS);
	static final Set<Integer> COOKING_SHRINE_IDS = Set.of(ObjectID.TEMPOROSS_SHRINE_FIRE);
	private static final Set<Integer> TRACKED_OBJECT_IDS = Set.of(
		ObjectID.TEMPOROSS_FIRE_VISUALS,
		ObjectID.TEMPOROSS_SHRINE_FIRE);

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
	private final TemporossWorkingSide workingSide = new TemporossWorkingSide();
	private final Set<GameObject> trackedObjects = Collections.newSetFromMap(new IdentityHashMap<>());
	private final Set<NPC> trackedNpcs = Collections.newSetFromMap(new IdentityHashMap<>());

	private boolean inEncounter;
	private boolean defeated;
	private int missingHudTicks;
	private int workingSideCandidateTick = -1;
	private int lastLoadActivityTick = -1;
	private int lastLoadHopperKey = -1;
	private boolean loadInteractionActive;
	private RouteSnapshot snapshot = new RouteSnapshot(
		0,
		0,
		0,
		TemporossRoute.FIRST_FISH_TARGET,
		null,
		false,
		false,
		false,
		false);

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
		route.setMethod(config.method());
		overlayManager.add(panelOverlay);
		overlayManager.add(sceneOverlay);
		keyManager.registerKeyListener(nextStepListener);
		keyManager.registerKeyListener(previousStepListener);
		keyManager.registerKeyListener(resetRouteListener);
		clientThread.invoke(this::checkEncounterState);
		log.debug("Tempoross Solo Helper started");
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!TemporossSoloHelperConfig.GROUP.equals(event.getGroup())
			|| !"method".equals(event.getKey()))
		{
			return;
		}

		clientThread.invoke(() ->
		{
			route.setMethod(config.method());
			resetWorkingSide();
			refreshAmmunitionCrateLocations();
			if (inEncounter)
			{
				snapshot = buildSnapshot();
			}
			log.debug("Tempoross method changed to {}; route reset", route.getMethod());
		});
	}

	@Override
	protected void shutDown()
	{
		keyManager.unregisterKeyListener(nextStepListener);
		keyManager.unregisterKeyListener(previousStepListener);
		keyManager.unregisterKeyListener(resetRouteListener);
		overlayManager.remove(panelOverlay);
		overlayManager.remove(sceneOverlay);
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

		updateWorkingSideFromInteraction();
		RouteStage stageBeforeUpdate = route.getStage();
		int fishBeforeUpdate = snapshot.getTotalFish();
		snapshot = buildSnapshot();
		if (!isWorkingSideKnown()
			&& isLoadStage(stageBeforeUpdate)
			&& snapshot.getTotalFish() < fishBeforeUpdate)
		{
			confirmWorkingSideFromLoad();
			snapshot = buildSnapshot();
		}
		if (config.autoAdvance())
		{
			route.update(snapshot);
		}
		if (!isLoadStage(route.getStage()))
		{
			loadInteractionActive = false;
			lastLoadHopperKey = -1;
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
			resetWorkingSide();
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
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (!inEncounter)
		{
			return;
		}

		NPC npc = event.getMenuEntry().getNpc();
		boolean loadClick = isLoadStage(route.getStage())
			&& npc != null
			&& AMMUNITION_CRATE_IDS.contains(npc.getId())
			&& "Load".equalsIgnoreCase(event.getMenuOption());
		loadInteractionActive = loadClick;
		if (loadClick)
		{
			lastLoadActivityTick = client.getTickCount();
			lastLoadHopperKey = getHopperKey(npc);
			if (!isWorkingSideKnown())
			{
				observeWorkingSideCandidate(npc);
			}
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (inEncounter
			&& isLoadStage(route.getStage())
			&& event.getType() == ChatMessageType.GAMEMESSAGE
			&& event.getMessage().toLowerCase().contains(LOAD_STARTED_MESSAGE))
		{
			lastLoadActivityTick = client.getTickCount();
			loadInteractionActive = true;
			if (!isWorkingSideKnown())
			{
				confirmWorkingSideFromLoad();
			}
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

	boolean isInEncounter()
	{
		return inEncounter;
	}

	RouteStage getStage()
	{
		return route.getStage();
	}

	RouteTarget getTarget()
	{
		return route.getTarget(snapshot);
	}

	TemporossMethod getMethod()
	{
		return route.getMethod();
	}

	int getStageNumber()
	{
		return route.getStageNumber();
	}

	int getStageCount()
	{
		return route.getStageCount();
	}

	int getTripCount()
	{
		return route.getTripCount();
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
				return snapshot.getTotalFish() + " / " + TemporossRoute.getFirstFishTarget(snapshot) + " fish";
			case LOAD_26:
			case LOAD_27_FIRST:
			case LOAD_REMAINDER:
			case LOAD_FINAL:
				return snapshot.getTotalFish() + " fish left";
			case CATCH_27_FIRST:
			case CATCH_27_SECOND:
			case CATCH_27_FINAL:
				return snapshot.getTotalFish() + " / " + TemporossRoute.FULL_FISH_TARGET + " fish";
			case ATTACK_FIRST:
			case KILL_TEMPOROSS:
			case MIX_ATTACK_FIRST:
			case MIX_ATTACK_SECOND:
			case MIX_ATTACK_FINAL:
				return formatEssence();
			case LOAD_THREE:
				return Math.min(3, Math.max(0, TemporossRoute.FULL_FISH_TARGET - snapshot.getTotalFish()))
					+ " / 3 loaded";
			case ATTACK_TO_TEN:
				return formatEssence() + " -> " + TemporossRoute.ESSENCE_TARGET + "%";
			case MIX_FISH_17:
				return cookingTargetProgress(TemporossRoute.MIX_FIRST_LOAD_TARGET, "fish");
			case MIX_COOK_17:
				return preparedProgress(TemporossRoute.MIX_FIRST_LOAD_TARGET);
			case MIX_LOAD_17:
				return loadedProgress(TemporossRoute.MIX_FIRST_LOAD_TARGET);
			case MIX_FIRES_FIRST:
				return "Drop buckets, then fish";
			case MIX_FISH_19_FIRST:
			case MIX_FISH_19_SECOND:
				return cookingTargetProgress(TemporossRoute.MIX_STANDARD_LOAD_TARGET, "fish");
			case MIX_COOK_19_FIRST:
			case MIX_COOK_19_SECOND:
				return preparedProgress(TemporossRoute.MIX_STANDARD_LOAD_TARGET);
			case MIX_LOAD_19_FIRST:
			case MIX_LOAD_19_SECOND:
				return loadedProgress(TemporossRoute.MIX_STANDARD_LOAD_TARGET);
			case MIX_FISH_FINAL_28:
				return cookingTargetProgress(TemporossRoute.MIX_FINAL_LOAD_TARGET, "fish");
			case MIX_COOK_FINAL_28:
				return preparedProgress(TemporossRoute.MIX_FINAL_LOAD_TARGET);
			case MIX_LOAD_FINAL_28:
				return Math.min(route.getFirstFinalHopperLoaded(), TemporossRoute.MIX_FINAL_HOPPER_TARGET)
					+ " / 14 + "
					+ Math.min(route.getSecondFinalHopperLoaded(), TemporossRoute.MIX_FINAL_HOPPER_TARGET)
					+ " / 14 loaded";
			case MIX_POST_KILL_BUCKETS:
				return snapshot.getWaterBuckets() + " / 5 filled; "
					+ snapshot.getTotalBuckets() + " / 5 collected";
			case COMPLETE:
				return "Select Leave";
			default:
				return "";
		}
	}

	String getInstructionText()
	{
		int excessFish = route.getExcessFish(snapshot);
		if (excessFish > 0)
		{
			return "Drop " + excessFish + " extra raw fish to keep the cooked load exact.";
		}
		if (route.getStage() == RouteStage.MIX_LOAD_FINAL_28
			&& route.getFirstFinalHopperLoaded() >= TemporossRoute.MIX_FINAL_HOPPER_TARGET)
		{
			return "First cannon has 14. Click off and load 14 into the other cannon.";
		}
		return route.getStage().getInstruction();
	}

	private String cookingTargetProgress(int target, String unit)
	{
		return snapshot.getCookableFish() + " / " + target + " " + unit;
	}

	private String preparedProgress(int target)
	{
		return snapshot.getPreparedFish() + " / " + target + " cooked";
	}

	private String loadedProgress(int target)
	{
		return route.getPreparedLoadedThisStage() + " / " + target + " loaded";
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
		defeated = false;
		resetWorkingSide();
		route.setMethod(config.method());
		route.reset();
		snapshot = buildSnapshot();
		scanSceneOnce();
		refreshAmmunitionCrateLocations();
		log.debug("Tempoross encounter detected; route reset");
	}

	private void clearEncounter()
	{
		inEncounter = false;
		defeated = false;
		missingHudTicks = 0;
		resetWorkingSide();
		trackedObjects.clear();
		trackedNpcs.clear();
		route.reset();
		snapshot = new RouteSnapshot(
			0,
			0,
			0,
			TemporossRoute.FIRST_FISH_TARGET,
			null,
			false,
			false,
			false,
			false);
	}

	private RouteSnapshot buildSnapshot()
	{
		int rawFish = 0;
		int cookedFish = 0;
		int crystallisedFish = 0;
		int waterBuckets = 0;
		int emptyBuckets = 0;
		int freeInventorySlots = TemporossRoute.FIRST_FISH_TARGET;
		ItemContainer inventory = client.getItemContainer(InventoryID.INV);
		if (inventory != null)
		{
			freeInventorySlots = Math.max(0, inventory.size() - inventory.count());
			for (Item item : inventory.getItems())
			{
				switch (item.getId())
				{
					case ItemID.TEMPOROSS_RAW_HARPOONFISH:
						rawFish += item.getQuantity();
						break;
					case ItemID.TEMPOROSS_HARPOONFISH:
						cookedFish += item.getQuantity();
						break;
					case ItemID.TEMPOROSS_CRYSTALLISED_HARPOONFISH:
						crystallisedFish += item.getQuantity();
						break;
					case ItemID.BUCKET_WATER:
						waterBuckets += item.getQuantity();
						break;
					case ItemID.BUCKET_EMPTY:
						emptyBuckets += item.getQuantity();
						break;
					default:
						break;
				}
			}
		}

		return new RouteSnapshot(
			rawFish,
			cookedFish,
			crystallisedFish,
			freeInventorySlots,
			readEssencePercent(),
			containsNpc(DOUBLE_FISHING_SPOT_IDS),
			containsNpc(SPIRIT_POOL_IDS),
			isLoadingFish(),
			lastLoadHopperKey,
			waterBuckets,
			emptyBuckets,
			defeated);
	}

	private boolean isLoadingFish()
	{
		if (loadInteractionActive
			|| (lastLoadActivityTick >= 0
				&& client.getTickCount() - lastLoadActivityTick <= LOAD_CANDIDATE_MAX_AGE_TICKS))
		{
			return true;
		}

		if (client.getLocalPlayer() == null)
		{
			return false;
		}

		Actor interacting = client.getLocalPlayer().getInteracting();
		return interacting instanceof NPC
			&& AMMUNITION_CRATE_IDS.contains(((NPC) interacting).getId());
	}

	boolean isOnWorkingSide(LocalPoint location)
	{
		return workingSide.contains(location);
	}

	boolean isPreferredLoadTarget(NPC npc)
	{
		return route.isPreferredLoadHopper(getHopperKey(npc));
	}

	private static int getHopperKey(NPC npc)
	{
		LocalPoint location = npc.getLocalLocation();
		return (location.getX() << 16) ^ (location.getY() & 0xFFFF);
	}

	private boolean isWorkingSideKnown()
	{
		return workingSide.isKnown();
	}

	private void updateWorkingSideFromInteraction()
	{
		if (client.getLocalPlayer() == null)
		{
			return;
		}

		Actor interacting = client.getLocalPlayer().getInteracting();
		if (interacting instanceof NPC)
		{
			NPC npc = (NPC) interacting;
			if (!isWorkingSideKnown()
				&& isLoadStage(route.getStage())
				&& AMMUNITION_CRATE_IDS.contains(npc.getId()))
			{
				observeWorkingSideCandidate(npc);
			}
			if (isLoadStage(route.getStage()) && AMMUNITION_CRATE_IDS.contains(npc.getId()))
			{
				lastLoadActivityTick = client.getTickCount();
				lastLoadHopperKey = getHopperKey(npc);
				loadInteractionActive = true;
			}
		}
	}

	private void observeWorkingSideCandidate(NPC npc)
	{
		workingSide.observeCandidate(npc.getLocalLocation());
		workingSideCandidateTick = client.getTickCount();
	}

	private void confirmWorkingSideFromLoad()
	{
		if (client.getLocalPlayer() == null
			|| workingSide.getCandidate() == null
			|| client.getTickCount() - workingSideCandidateTick > LOAD_CANDIDATE_MAX_AGE_TICKS)
		{
			return;
		}

		NPC candidateCrate = null;
		for (NPC npc : getTrackedAmmunitionCrates())
		{
			if (npc.getLocalLocation().equals(workingSide.getCandidate()))
			{
				candidateCrate = npc;
				break;
			}
		}

		if (candidateCrate == null
			|| candidateCrate.getWorldLocation().distanceTo(client.getLocalPlayer().getWorldLocation())
				> LOAD_CONFIRM_DISTANCE_TILES)
		{
			return;
		}

		if (workingSide.confirmCandidate())
		{
			log.debug("Working Tempoross island shore confirmed from successful loading");
		}
	}

	private void refreshAmmunitionCrateLocations()
	{
		for (NPC crate : getTrackedAmmunitionCrates())
		{
			workingSide.rememberCrate(crate.getLocalLocation());
		}
	}

	private static boolean isLoadStage(RouteStage stage)
	{
		return stage.getTarget() == RouteTarget.LOAD;
	}

	private List<NPC> getTrackedAmmunitionCrates()
	{
		List<NPC> crates = new ArrayList<>(AMMUNITION_CRATE_IDS.size());
		for (NPC npc : trackedNpcs)
		{
			if (AMMUNITION_CRATE_IDS.contains(npc.getId()))
			{
				crates.add(npc);
			}
		}
		return crates;
	}

	private void resetWorkingSide()
	{
		workingSide.reset();
		workingSideCandidateTick = -1;
		lastLoadActivityTick = -1;
		lastLoadHopperKey = -1;
		loadInteractionActive = false;
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
			if (inEncounter
				&& AMMUNITION_CRATE_IDS.contains(npc.getId())
				&& !isWorkingSideKnown())
			{
				refreshAmmunitionCrateLocations();
			}
		}
		if (VICTORY_NPC_IDS.contains(npc.getId()))
		{
			defeated = true;
		}
	}
}
