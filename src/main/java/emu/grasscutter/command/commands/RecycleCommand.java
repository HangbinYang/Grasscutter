package emu.grasscutter.command.commands;

import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.data.excels.ItemData;
import emu.grasscutter.game.inventory.Inventory;
import emu.grasscutter.game.inventory.ItemType;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.data.GameData;
import emu.grasscutter.game.props.ActionReason;

import java.util.List;
import java.util.LinkedList;

import static emu.grasscutter.utils.Language.translate;

@Command(label = "recycle", usage = "recycle <wp|art>",
        description = "commands.recycle.description",
        aliases = {"recycle"}, permission = "player.recycle", permissionTargeted = "player.recycle.others")

public final class RecycleCommand implements CommandHandler {

    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {
        if (args.size() < 1) {
            CommandHandler.sendMessage(sender, translate(sender, "commands.recycle.command_usage"));
            return;
        }
        Inventory playerInventory = targetPlayer.getInventory();
        List<GameItem> toDelete = null;
        List<GameItem> toGive = new LinkedList<>();
        
        switch (args.get(0)) {
            case "wp" -> {
            	toDelete = playerInventory.getItems().values().stream()
                    .filter(item -> item.getItemType() == ItemType.ITEM_WEAPON)
                    .filter(item -> !item.isLocked() && !item.isEquipped())
                    .filter(item -> item.getItemData().getRankLevel() < 4)
                    .toList();
                Integer giveCount = 0;
                for (GameItem item: toDelete) {
                    giveCount += item.getItemData().getRankLevel();
                }
                if (giveCount > 0) {
                    ItemData itemData = GameData.getItemDataMap().get(104013);
                    GameItem newItem = new GameItem(itemData);
                    newItem.setLevel(1);
                    newItem.setCount(giveCount);
                    toGive.add(newItem);
                    targetPlayer.getInventory().addItems(toGive, ActionReason.SubfieldDrop);
                    CommandHandler.sendMessage(sender, translate(sender, "commands.recycle.weapons", targetPlayer.getNickname()));
                }
            }
            case "art" -> {
            	toDelete = playerInventory.getItems().values().stream()
                    .filter(item -> item.getItemType() == ItemType.ITEM_RELIQUARY)
                    .filter(item -> item.getLevel() == 1 && item.getExp() == 0)
                    .filter(item -> !item.isLocked() && !item.isEquipped())
                    .toList();
                Integer giveCount = 0;
                for (GameItem item: toDelete) {
                    if (item.getItemData().getRankLevel() == 5) {
                        giveCount += 2;
                    } else {
                        giveCount += 1;
                    } 
                    giveCount += item.getItemData().getRankLevel();
                }
                if (giveCount > 0) {
                    ItemData itemData = GameData.getItemDataMap().get(105003);
                    GameItem newItem = new GameItem(itemData);
                    newItem.setLevel(1);
                    newItem.setCount(giveCount);
                    toGive.add(newItem);
                    targetPlayer.getInventory().addItems(toGive, ActionReason.SubfieldDrop);
                    CommandHandler.sendMessage(sender, translate(sender, "commands.recycle.artifacts", targetPlayer.getNickname()));
                }
            }
        }
        
        if (toDelete != null) {
        	playerInventory.removeItems(toDelete);
        }
    }
}
