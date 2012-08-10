package net.windwarrior.bukkit.tweakstats;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

public class PlayerOnlineListener implements Listener {
    Map<Player, Long> playerList = new HashMap<Player, Long>();
    Map<String, Long> lastChatList = new HashMap<String, Long>();
    private long afkTIME = 10 * 60; // 10*60; // 5 minutes

    private static Logger log = Logger.getLogger("Minecraft");
    private TweakStats plugin;

    public PlayerOnlineListener(TweakStats instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // log.info("hai");
        // super.onPlayerJoin(event);
        Player eventPlayer = event.getPlayer();
        playerList.put(eventPlayer, System.currentTimeMillis());
        plugin.getLocationRecorder().activity(eventPlayer.getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player eventPlayer = event.getPlayer();
        if (playerList.containsKey(eventPlayer)) {
            long timediff = System.currentTimeMillis() - playerList.get(eventPlayer);
            playerList.remove(eventPlayer);
        }
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        Player p = event.getPlayer();
        lastChatList.put(p.getName(), System.currentTimeMillis());
        plugin.getLocationRecorder().activity(p.getName());
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player p = event.getPlayer();
        lastChatList.put(p.getName(), System.currentTimeMillis());
        if (event.getMessage().startsWith("/")) {
            String[] cmdarray = event.getMessage().split("/ /");
            String cmd = "";
            if (cmdarray.length > 0) cmd = cmdarray[0].substring(1);
            if (!cmd.equalsIgnoreCase("afk")) plugin.getLocationRecorder().activity(p.getName());
            else plugin.getLocationRecorder().markAFK(p.getName());
        }
    }

    public long getLastActiveTime(String player) {
        long time = 0L;
        if (lastChatList.containsKey(player)) {
            time = lastChatList.get(player);
        }
        long movetime = plugin.getLocationRecorder().getLastMoveTime(player);
        if (movetime > time)
            time = movetime;

        return time;

    }

    public boolean isAFK(String player) {
        return (getLastActiveTime(player) / 1000) < (System.currentTimeMillis() / 1000) - afkTIME;
    }

    public long getConnectedTime(String player) {
        for (Entry<Player, Long> p : playerList.entrySet()) {
            if (p.getKey().getName().equalsIgnoreCase(player)) {
                return p.getValue();
            }
        }
        return 0L;
    }

    public int[] getOnlineTime(String player) {
        for (Entry<Player, Long> p : playerList.entrySet()) {
            if (p.getKey().getName().equalsIgnoreCase(player)) {
                long joinTime = p.getValue();
                long timediff = System.currentTimeMillis() - joinTime;
                return getHumanReadableTime(timediff);
            }
        }

        return null;

    }


    public int[] getHumanReadableTime(long timediff) {
        int milliseconds = (int) (timediff % 1000);
        long time = timediff / 1000;
        int seconds = (int) time % 60;
        int minutes = (int) (((time - seconds) / 60) % 60);
        int hours = (int) (((time - seconds - minutes * 60)) / 3600);

        return new int[]{hours, minutes, seconds, milliseconds};
    }
}
