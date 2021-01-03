package lol.maltest.buildpractice.listeners;

import com.connorlinfoot.titleapi.TitleAPI;
import lol.maltest.buildpractice.BuildPractice;
import lol.maltest.buildpractice.arena.Arena;
import lol.maltest.buildpractice.arena.ArenaManager;
import lol.maltest.buildpractice.utils.ChatUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class BlockListener implements Listener {

    private ArenaManager arenaManager;

    private ArenaListener arenaListener;

    private final BuildPractice plugin;

    public BlockListener(BuildPractice plugin) {
        this.plugin = plugin;
        this.arenaManager = plugin.getArenaManager();
        blocks = new HashMap<UUID, List<Block>>();
    }

    private final Map<UUID, List<Block>> blocks;


    public void onBlockPlaced(Player player, Block block) {
        List<Block> placed = blocks.get(player.getUniqueId());
        if (placed == null) {
            placed = new ArrayList<Block>();
            blocks.put(player.getUniqueId(), placed);
        }
        placed.add(block);
    }

    public List<Block> getBlocksPlacedBy(Player player) {
        return blocks.get(player.getUniqueId());
    }

    public void removePlayerFromMap(Player player) {
        blocks.remove(player.getUniqueId());
    }

    public UUID getWhoPlaced(Block block) {
        UUID result = null;
        Iterator<Map.Entry<UUID, List<Block>>> iterator = blocks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, List<Block>> entry = iterator.next();
            if (entry.getValue().contains(block)) {
                result = entry.getKey();
                break;
            }
        }
        return result;
    }

    public void removeBlock(Block block) {
        UUID player = getWhoPlaced(block);
        if (player != null) {
            blocks.get(player).remove(block);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Arena arena = arenaManager.getArena(player);
        if(arena == null) return;
        if(getBlocksPlacedBy(player) != null) {
            if (getBlocksPlacedBy(player).size() > 0) {
                for (Block blocks : getBlocksPlacedBy(player)) {
                    blocks.setType(Material.AIR);
                }
            }
        }
        removePlayerFromMap(player);
        arena.removePlayer(player);
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        Player player = e.getPlayer();
        if(arenaManager.getArena(player) != null) {
            Double X = arenaManager.getArena(player).getLocation().getX();
            Double Y = arenaManager.getArena(player).getLocation().getY();
            Double Z = arenaManager.getArena(player).getLocation().getZ();
            if (block.getZ() < -2 || block.getX() == X + 3 || block.getX() == X - 3) {
                if(player.hasPermission("bp.admin")) return;
                e.setCancelled(true);
                player.sendMessage(ChatUtil.clr("&cYou can't build here."));
            } else {
                onBlockPlaced(player,block);
            }
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Player player = e.getPlayer();
        if(!player.hasPermission("bp.admin")) {
            if(blocks.get(player.getUniqueId()) != null) {
                if (!blocks.get(player.getUniqueId()).contains(block)) {
                    e.setCancelled(true);
                    player.sendMessage(ChatUtil.clr("&cYou can only break blocks placed by you!"));
                }
            } else {
                e.setCancelled(true);
                player.sendMessage(ChatUtil.clr("&cYou can only break blocks placed by you!"));
            }
        }
    }

    @EventHandler
    public void onFall(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (arenaManager.getArena(player) != null) {
            if (player.getLocation().getY() < 60) {
                Arena arena = arenaManager.getArena(player);
                player.teleport(arena.getLocation());
                if(blocks.get(player.getUniqueId()) == null) return;
                if (blocks.get(player.getUniqueId()).size() > 0) {
                    player.sendMessage(ChatUtil.clr("&anice, you bridged &c&n" + getBlocksPlacedBy(player).size() + " blocks!"));
                    player.getInventory().clear();
                    ArenaListener.giveStuff(player);
                    for (Block blocks : getBlocksPlacedBy(player)) {
                        blocks.setType(Material.AIR);
                    }
                    blocks.get(player.getUniqueId()).clear();
                }
            }
        }
    }

}
