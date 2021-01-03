package lol.maltest.buildpractice.listeners;

import com.connorlinfoot.titleapi.TitleAPI;
import lol.maltest.buildpractice.BuildPractice;
import lol.maltest.buildpractice.arena.Arena;
import lol.maltest.buildpractice.arena.ArenaManager;
import lol.maltest.buildpractice.utils.ChatUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ArenaListener implements Listener {

    private final ArenaManager arenaManager;


    private final BuildPractice plugin;

    public ArenaListener(BuildPractice plugin) {
        this.plugin = plugin;
        this.arenaManager = plugin.getArenaManager();
    }


    @EventHandler
    public void onhealth(EntityDamageEvent e) {
        e.setDamage(0);
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

                         @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Arena found;
        for(Arena arena : arenaManager.getArenaList()) {
            if(arena == null) return;
            if(arena.getPlayers().size() < 1) {
                found = arena;
                Player player = event.getPlayer();
                found.addPlayer(player);
                player.sendMessage(ChatUtil.clr("&aFound available arena; &c&n" + arena.getName() + " &ateleporting..."));
                player.getInventory().clear();
                giveStuff(player);
                player.teleport(found.getLocation());
                player.sendMessage(ChatUtil.clr("&aEnjoy playing bridge practice!"));
                return;
            }
        }
    }

    public static void giveStuff(Player player) {
        ItemStack blocks = new ItemStack(Material.SANDSTONE, 64);
        ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE, 1);
        ItemMeta pickaxeMeta = pickaxe.getItemMeta();
        pickaxeMeta.addEnchant(Enchantment.DURABILITY, 10000, true);
        pickaxeMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        pickaxe.setItemMeta(pickaxeMeta);
        player.getInventory().setItem(0, blocks);
        player.getInventory().setItem(1, blocks);
        player.getInventory().setItem(2, pickaxe);
    }

    @EventHandler
    public void onStepGoldplate(PlayerInteractEvent e) {

        if (e.getAction() != Action.PHYSICAL) {
            return;
        }
        if (e.getClickedBlock().getType() != Material.GOLD_PLATE) {
            return;
        }

        Player player = e.getPlayer();
        if(arenaManager.getArena(player) == null) return;

        Arena arena = arenaManager.getArena(player);

        win(player);
    }


    @EventHandler
    public void noFall(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player) {
            if(e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        if(arenaManager.getArena(player) != null) {
            player.teleport(arenaManager.getArena(player).getLocation());
            player.sendMessage(ChatUtil.clr("&chow tf did you die? report in discord pls"));
        }
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if(arenaManager.getArena(player) != null) {
            Arena playerArena = arenaManager.getArena(player);
            double X = playerArena.getLocation().getBlockX();
            double Z = playerArena.getLocation().getBlockX();
            if(player.getLocation().getBlockX() == X + 5 || player.getLocation().getBlockX() == X - 5 || player.getLocation().getBlockZ() == Z - 5) {
                player.teleport(playerArena.getLocation());
                player.sendMessage(ChatUtil.clr("&cYou can't go outside your island!"));
            }
        }
    }

    public void win(Player player) {
        if(arenaManager.getArena(player) != null) {
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);

            TitleAPI.sendTitle(player, 10, 60, 20, ChatUtil.clr("&e&lgg you did it!"), ChatUtil.clr("&c&onow do it again :)"));
            for (Block blocks : plugin.getBlockListener().getBlocksPlacedBy(player)) {
                blocks.setType(Material.AIR);
            }
            player.teleport(arenaManager.getArena(player).getLocation());
        }
    }
}
