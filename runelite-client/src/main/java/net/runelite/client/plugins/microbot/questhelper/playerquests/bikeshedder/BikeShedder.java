/*
 * Copyright (c) 2024, pajlada <https://github.com/pajlada>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.microbot.questhelper.playerquests.bikeshedder;

import com.google.common.collect.ImmutableMap;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.ComponentID;
import net.runelite.client.plugins.microbot.questhelper.requirements.util.Spellbook;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.Zone;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.ZoneRequirement;
import net.runelite.client.plugins.microbot.questhelper.steps.*;
import net.runelite.client.plugins.microbot.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.microbot.questhelper.panel.PanelDetails;
import net.runelite.client.plugins.microbot.questhelper.questhelpers.BasicQuestHelper;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.player.SpellbookRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.widget.WidgetTextRequirement;

import net.runelite.client.plugins.microbot.questhelper.steps.widget.NormalSpells;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.runelite.client.plugins.microbot.questhelper.requirements.util.LogicHelper.*;

public class BikeShedder extends BasicQuestHelper {
    private DetailedQuestStep moveToLumbridge;
    private DetailedQuestStep confuseHans;
    private DetailedQuestStep equipLightbearer;

    private ItemRequirement anyLog;
    private ObjectStep useLogOnBush;

    private ItemRequirement oneCoin;
    private ItemRequirement manyCoins;
    private ObjectStep useCoinOnBush;
    private ObjectStep useManyCoinsOnBush;

    private Zone conditionalRequirementZone;
    private ZoneRequirement conditionalRequirementZoneRequirement;
    private ZoneRequirement conditionalRequirementZoneSouthRequirement;
    private ZoneRequirement conditionalRequirementZoneNorthRequirement;
    private ItemRequirement conditionalRequirementCoins;
    private DetailedQuestStep conditionalRequirementLookAtCoins;
    private ItemRequirement conditionalRequirementGoldBar;
    private WidgetTextRequirement lookAtCooksAssistantRequirement;
    private DetailedQuestStep lookAtCooksAssistant;
    private WidgetTextRequirement lookAtCooksAssistantTextRequirement;
    private ZoneRequirement byStaircaseInSunrisePalace;
    private ObjectStep goDownstairsInSunrisePalace;

    @Override
    public Map<Integer, QuestStep> loadSteps() {
        var lumbridge = new Zone(new WorldPoint(3217, 3210, 0), new WorldPoint(3226, 3228, 0));
        var outsideLumbridge = new ZoneRequirement(false, lumbridge);
        moveToLumbridge.setHighlightZone(lumbridge);
        var steps = new ConditionalStep(this, confuseHans);
        steps.addStep(byStaircaseInSunrisePalace, goDownstairsInSunrisePalace);
        steps.addStep(outsideLumbridge, moveToLumbridge);
        steps.addStep(new ZoneRequirement(new WorldPoint(3222, 3218, 0)), equipLightbearer);
        steps.addStep(new ZoneRequirement(new WorldPoint(3223, 3218, 0)), useLogOnBush);
        steps.addStep(new ZoneRequirement(new WorldPoint(3222, 3217, 0)), useCoinOnBush);
        steps.addStep(new ZoneRequirement(new WorldPoint(3223, 3216, 0)), useManyCoinsOnBush);
        steps.addStep(conditionalRequirementZoneRequirement, conditionalRequirementLookAtCoins);
        steps.addStep(new ZoneRequirement(new WorldPoint(3224, 3221, 0)), lookAtCooksAssistant);
        return new ImmutableMap.Builder<Integer, QuestStep>()
                .put(-1, steps)
                .build();
    }

    @Override
    protected void setupRequirements() {
        moveToLumbridge = new DetailedQuestStep(this, new WorldPoint(3221, 3218, 0), "Move to outside Lumbridge Castle");

        var normalSpellbook = new SpellbookRequirement(Spellbook.NORMAL);

        confuseHans = new NpcStep(this, NpcID.HANS, new WorldPoint(3221, 3218, 0), "Cast Confuse on Hans", normalSpellbook);
        confuseHans.addSpellHighlight(NormalSpells.CONFUSE);

        var lightbearer = new ItemRequirement("Lightbearer", ItemID.LIGHTBEARER).highlighted();
        equipLightbearer = new DetailedQuestStep(this, "Equip a Lightbearer", lightbearer.equipped());

        anyLog = new ItemRequirement("Any log", ItemCollections.LOGS_FOR_FIRE).highlighted();
        useLogOnBush = new ObjectStep(this, NullObjectID.NULL_10778, new WorldPoint(3223, 3217, 0), "Use log on bush", anyLog);
        useLogOnBush.addIcon(ItemID.LOGS);

        oneCoin = new ItemRequirement("Coins", ItemCollections.COINS, 1);
        oneCoin.setHighlightInInventory(true);
        useCoinOnBush = new ObjectStep(this, NullObjectID.NULL_10778, new WorldPoint(3223, 3217, 0), "Use coins on the bush.", oneCoin);
        useCoinOnBush.addIcon(ItemID.COINS);

        manyCoins = new ItemRequirement("Coins", ItemCollections.COINS, 100);
        manyCoins.setHighlightInInventory(true);
        useManyCoinsOnBush = new ObjectStep(this, NullObjectID.NULL_10778, new WorldPoint(3223, 3217, 0), "Use many coins on the bush.", manyCoins);
        useManyCoinsOnBush.addIcon(ItemID.COINS);

        conditionalRequirementZone = new Zone(new WorldPoint(3223, 3221, 0), new WorldPoint(3223, 3223, 0));
        conditionalRequirementZoneRequirement = new ZoneRequirement(conditionalRequirementZone);
        conditionalRequirementZoneSouthRequirement = new ZoneRequirement(new WorldPoint(3223, 3221, 0));
        conditionalRequirementZoneNorthRequirement = new ZoneRequirement(new WorldPoint(3223, 3223, 0));

        conditionalRequirementCoins = new ItemRequirement("Coins", ItemCollections.COINS, 50);
        conditionalRequirementCoins.setTooltip("Obtained by robbing a bank");
        conditionalRequirementCoins.setHighlightInInventory(true);
        conditionalRequirementCoins.setConditionToHide(conditionalRequirementZoneSouthRequirement);

        conditionalRequirementGoldBar = new ItemRequirement("Gold Bar", ItemID.GOLD_BAR, 1);
        conditionalRequirementGoldBar.setTooltip("Obtained by robbing a bank");
        conditionalRequirementGoldBar.setHighlightInInventory(true);
        conditionalRequirementGoldBar.setConditionToHide(or(conditionalRequirementZoneNorthRequirement, conditionalRequirementZoneSouthRequirement));

        conditionalRequirementLookAtCoins = new DetailedQuestStep(this, "Admire the coins in your inventory.", conditionalRequirementCoins);

        lookAtCooksAssistantRequirement = new WidgetTextRequirement(ComponentID.DIARY_TITLE, "Cook's Assistant");
        lookAtCooksAssistantRequirement.setDisplayText("Cook's Assistant quest journal open");
        lookAtCooksAssistantTextRequirement = new WidgetTextRequirement(ComponentID.DIARY_TEXT, true, "he now lets me use his high quality range");
        lookAtCooksAssistantTextRequirement.setDisplayText("Cook's Assistant quest journal open & received reward (checking text)");
        lookAtCooksAssistant = new DetailedQuestStep(this, "Open the Cook's Assistant quest journal. You must have started the quest for this test to work.", lookAtCooksAssistantRequirement, lookAtCooksAssistantTextRequirement);

        var upstairsInSunrisePalace = new Zone(new WorldPoint(1684, 3162, 1), new WorldPoint(1691, 3168, 1));
        byStaircaseInSunrisePalace = new ZoneRequirement(upstairsInSunrisePalace);
        goDownstairsInSunrisePalace = new ObjectStep(getQuest().getQuestHelper(), ObjectID.STAIRCASE_52627, new WorldPoint(1690, 3164, 1), "Climb downstairs, ensure stairs are well highlighted!");
    }

    @Override
    public List<PanelDetails> getPanels() {
        var panels = new ArrayList<PanelDetails>();

        panels.add(new PanelDetails("Move to Lumbridge", List.of(moveToLumbridge)));
        panels.add(new PanelDetails("Normal Spellbook", List.of(confuseHans)));
        panels.add(new PanelDetails("Equip Lightbearer", List.of(equipLightbearer)));
        panels.add(new PanelDetails("Use log on mysterious bush", List.of(useLogOnBush), List.of(anyLog)));
        panels.add(new PanelDetails("Use coins on mysterious bush", List.of(useCoinOnBush, useManyCoinsOnBush), List.of(oneCoin, manyCoins)));
        panels.add(new PanelDetails("Conditional requirement", List.of(conditionalRequirementLookAtCoins), List.of(conditionalRequirementCoins, conditionalRequirementGoldBar)));
        panels.add(new PanelDetails("Quest state", List.of(lookAtCooksAssistant), List.of(lookAtCooksAssistantRequirement, lookAtCooksAssistantTextRequirement)));
        panels.add(new PanelDetails("Ensure staircase upstairs in Sunrise Palace is highlighted", List.of(goDownstairsInSunrisePalace), List.of()));

        return panels;
    }
}