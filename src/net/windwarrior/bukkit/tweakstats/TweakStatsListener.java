package net.windwarrior.bukkit.tweakstats;

import java.util.Date;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 *
 * @author lennart
 */
public class TweakStatsListener implements Listener {

    private final TweakStats ts;

    public TweakStatsListener(TweakStats ts) {
        this.ts = ts;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent pde) {
        Player entity = (Player) pde.getEntity();
        EntityDamageEvent lastDamageCause = entity.getLastDamageCause();
        DamageCause dc = lastDamageCause.getCause();
        String otherentity = null;
        if (lastDamageCause instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent edb = (EntityDamageByEntityEvent) lastDamageCause;
            Entity damager = edb.getDamager();
            if (damager instanceof Projectile){
                Projectile pj = (Projectile) damager;
                if(pj.getShooter() instanceof Player){
                    Player pl = ((Player) pj.getShooter());
                    otherentity = pl.getName();
                }else{
                    otherentity = pj.getShooter().getType().getName();
                }
                
            }else if (damager instanceof Player) {
                Player pl = (Player) damager;
                otherentity = pl.getName();
            } else {
                otherentity = damager.getType().getName();
            }

        }
        TweakDeath td = new TweakDeath(
                stripName(entity.getName()),
                entity.getLocation().getWorld().getName(),
                dc.toString(), 
                entity.getLocation().getX(),
                entity.getLocation().getY(),
                entity.getLocation().getZ(),
                new Date(System.currentTimeMillis()),
                stripName(otherentity));  
        ts.getDatabase().save(td);

    }
    
    public String stripName(String name){
        return name != null ? name.replaceAll("§[0-9a-fk-or]", "").replaceAll("&c[0-9a-fk-or]", "") : null;
    }
}
