package emu.grasscutter.command.commands;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.ItemData;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.inventory.ItemType;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.ActionReason;

import java.util.List;

import static emu.grasscutter.utils.Language.translate;

@Command(label = "gift", usage = "gift <wp|art> [row] [index] @someone", aliases = {"gift"}, permission = "player.gift", permissionTargeted = "player.gift.others", description = "commands.gift.description")
public final class GiftCommand implements CommandHandler {

    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {
        if (args.size() < 3) {
            CommandHandler.sendMessage(sender, translate(sender, "commands.gift.arglength"));
            return;
        }
        if (targetPlayer == null) {
            CommandHandler.sendMessage(sender, translate(sender, "commands.gift.needTarget"));
            return;
        }
        String type = args.get(0);
        Integer row = null;
        Integer index = null;
        try {
            row = Integer.valueOf(args.get(1));
        } catch (NumberFormatException e) {
            CommandHandler.sendMessage(sender, translate(sender, "commands.gift.errornumber"));
            return;
        }
        try {
            index = Integer.valueOf(args.get(2));
        } catch (NumberFormatException e) {
            CommandHandler.sendMessage(sender, translate(sender, "commands.gift.errornumber"));
            return;
        }
        List<GameItem> items = null;
        switch (type) {
            default:
                CommandHandler.sendMessage(sender, translate(sender, "commands.gift.inventoryerror"));
                return;
            case "wp":
                items = sender.getInventory().getItems().values().stream()
                    .filter(item -> item.getItemType() == ItemType.ITEM_WEAPON)
                    .toList();
                break;
            case "art":
                items = sender.getInventory().getItems().values().stream()
                    .filter(item -> item.getItemType() == ItemType.ITEM_RELIQUARY)
                    .toList();
                break;
        }
        if (items.size() == 0) {
            CommandHandler.sendMessage(sender, translate(sender, "commands.gift.none"));
            return;
        }
        if (items.size() < row * 8 + index) {
            CommandHandler.sendMessage(sender, translate(sender, "commands.gift.outbounds"));
            return;
        }
        GameItem item = items.get(row * 8 + index - 1);
        if (item.isLocked()) {
            CommandHandler.sendMessage(sender, translate(sender, "commands.gift.locked"));
            return;
        }
        if (item.isEquipped()) {
            CommandHandler.sendMessage(sender, translate(sender, "commands.gift.equipped"));
            return;
        }
        // 创建新的item
        ItemData itemData = GameData.getItemDataMap().get(item.getItemData().getId());
        GameItem gift = new GameItem(itemData);
        gift.setLevel(item.getLevel());
        gift.setExp(item.getExp());
        gift.setTotalExp(item.getTotalExp());
        gift.setPromoteLevel(item.getPromoteLevel());
        switch (type) {
            case "wp":
                gift.setRefinement(item.getRefinement());
                for (Integer affix: item.getAffixes()) {
                    gift.getAffixes().add(affix);
                }
                break;
            case "art":
                gift.setMainPropId(item.getMainPropId());
                for (Integer propId: item.getAppendPropIdList()) {
                    gift.getAppendPropIdList().add(propId);
                }
                break;
            default:
                CommandHandler.sendMessage(sender, "NO type cased");
                break;
        }
        sender.getInventory().removeItem(item);
        targetPlayer.getInventory().addItem(gift, ActionReason.SubfieldDrop);
        CommandHandler.sendMessage(sender, translate(sender, "commands.gift.success"));
        CommandHandler.sendMessage(targetPlayer, translate(sender, "commands.gift.received"));
    }
}
