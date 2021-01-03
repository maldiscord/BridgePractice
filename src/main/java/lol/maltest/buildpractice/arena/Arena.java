package lol.maltest.buildpractice.arena;

import com.connorlinfoot.titleapi.TitleAPI;
import lol.maltest.buildpractice.utils.ChatUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Arena {

    private final String name;
    private final Location location;
    private final Set<Player> players;

    public Arena(String name, Location location) {
        this.name = name;
        this.location = location;
        this.players = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public Set<Player> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }

}
