package net.windwarrior.bukkit.tweakstats;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.PersistenceException;

import com.guntherdw.bukkit.LocationRecorder.LocationRecorder;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author lennart
 */
public class TweakStats extends JavaPlugin {

    private static Logger log = Logger.getLogger("Minecraft");
    //private final DeathListener playerDeathListener = new DeathListener();
    private final PlayerOnlineListener playerOnlineListener = new PlayerOnlineListener(this);
    private LocationRecorder locationRecorder = null;
    private TweakcraftUtils tcutils = null;


    @Override
    public void onEnable() {
        this.getLogger().info("[TweakStats] Enabling TweakStats!");
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new TweakStatsListener(this), this);

        registerLocationRecorder();
        registerTCUtils();

        try {
            this.getDatabase().find(TweakDeath.class);
        } catch (PersistenceException pe) {
            this.getLogger().severe("[TweakStats] not able to find database table, shutting down");
            this.getPluginLoader().disablePlugin(this);
        }

    }

    public void registerLocationRecorder() {
        Plugin p = this.getServer().getPluginManager().getPlugin("LocationRecorder");
        if (p != null)
            if (locationRecorder == null)
                locationRecorder = (LocationRecorder) p;
    }

    public void registerTCUtils() {
        Plugin p = this.getServer().getPluginManager().getPlugin("TweakcraftUtils");
        if (p != null)
            if (tcutils == null)
                tcutils = (TweakcraftUtils) p;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {

        if (command.getName().equalsIgnoreCase("onlinetime") && args.length == 1) {
            int[] time = ((PlayerOnlineListener) playerOnlineListener).getOnlineTime(args[0]);
            if (time != null) {
                sender.sendMessage("Player: " + args[0] + " is online for " + time[0] + ":" + time[1] + ":" + time[2]);
                return true;
            }
        }
        return false;
    }

    public PlayerOnlineListener getPlayerOnlineListener() {
        return this.playerOnlineListener;
    }

    public LocationRecorder getLocationRecorder() {
        return locationRecorder;
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(TweakDeath.class);
        return list;
    }
}
