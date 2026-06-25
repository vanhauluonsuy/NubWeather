package vanhauluonsuy.nubWeather;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class NubWeather extends JavaPlugin implements Listener {

    private BukkitTask syncTask;
    private ZoneId zoneId;
    private static final DateTimeFormatter FULL_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        loadTimeZone();

        prepareWorlds();
        bypassWorldGuardLocks();
        registerPlaceholderAPI();

        getServer().getPluginManager().registerEvents(this, this);
        startSyncTask();

        getLogger().info("NubWeather enabled!");
    }

    @Override
    public void onDisable() {
        if (syncTask != null && !syncTask.isCancelled()) {
            syncTask.cancel();
        }
    }

    private void loadTimeZone() {
        String tz = getConfig().getString("timezone", "Asia/Ho_Chi_Minh");
        try {
            zoneId = ZoneId.of(tz);
            getLogger().info("Timezone set to: " + zoneId);
        } catch (Exception e) {
            getLogger().warning("Invalid timezone '" + tz + "', falling back to system default.");
            zoneId = ZoneId.systemDefault();
        }
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    private void startSyncTask() {
        if (syncTask != null && !syncTask.isCancelled()) {
            syncTask.cancel();
        }
        long interval = Math.max(1, getConfig().getLong("sync-interval-ticks", 20L));
        syncTask = Bukkit.getScheduler().runTaskTimer(this, this::sync, 0L, interval);
    }

    private void sync() {
        if (getConfig().getBoolean("sync-time", true)) {
            syncTime();
        }
        if (getConfig().getBoolean("sync-weather", true)) {
            syncWeather();
        }
        resetPlayerLocks();
    }

    private void syncTime() {
        long target = getCurrentGameTick();

        for (World world : getServer().getWorlds()) {
            world.setTime(target);
        }
    }

    public long getCurrentGameTick() {
        LocalTime now = LocalTime.now(zoneId);
        long secondOfDay = now.toSecondOfDay();

        long target = Math.round((secondOfDay / 3600.0 - 6) * 1000) % 24000;
        if (target < 0) target += 24000;

        return target;
    }

    public LocalTime getCurrentRealTime() {
        return LocalTime.now(zoneId);
    }

    public String getFormattedTime() {
        LocalTime now = LocalTime.now(zoneId);
        return now.getHour() + "h:" + String.format("%02d", now.getMinute()) + "p";
    }

    /**
     * Hiển thị dạng 12:20:50, không có nano giây.
     */
    public String getFormattedFullTime() {
        return LocalTime.now(zoneId).format(FULL_TIME_FORMAT);
    }

    public String getPeriod() {
        int hour = LocalTime.now(zoneId).getHour();
        if (hour >= 5 && hour < 12) return getConfig().getString("periods.morning", "Sáng");
        if (hour >= 12 && hour < 14) return getConfig().getString("periods.noon", "Trưa");
        if (hour >= 14 && hour < 18) return getConfig().getString("periods.afternoon", "Chiều");
        return getConfig().getString("periods.night", "Tối");
    }

    private void syncWeather() {
        World master = getMasterWorld();
        if (master == null) return;

        boolean storm = master.hasStorm();
        boolean thunder = master.isThundering();
        int duration = 20 * 60 * 60 * 24;

        for (World world : getServer().getWorlds()) {
            if (world.hasStorm() != storm) {
                world.setStorm(storm);
            }
            if (world.isThundering() != thunder) {
                world.setThundering(thunder);
            }

            if (storm) {
                world.setWeatherDuration(duration);
                world.setClearWeatherDuration(0);
                if (thunder) {
                    world.setThunderDuration(duration);
                }
            } else {
                world.setClearWeatherDuration(duration);
                world.setWeatherDuration(0);
            }
        }
    }

    private World getMasterWorld() {
        String name = getConfig().getString("weather-master", "world");
        if (name != null && !name.isBlank()) {
            World w = getServer().getWorld(name);
            if (w != null) return w;
        }
        return getServer().getWorlds().stream()
                .filter(w -> w.getEnvironment() == World.Environment.NORMAL)
                .findFirst()
                .orElse(getServer().getWorlds().isEmpty() ? null : getServer().getWorlds().get(0));
    }

    private void resetPlayerLocks() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.resetPlayerWeather();
            player.resetPlayerTime();
        }
    }

    private void prepareWorlds() {
        for (World world : getServer().getWorlds()) {
            prepareWorld(world);
        }
    }

    private void prepareWorld(World world) {
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
    }

    private void bypassWorldGuardLocks() {
        if (!getConfig().getBoolean("bypass-worldguard", true)) return;

        Plugin wg = getServer().getPluginManager().getPlugin("WorldGuard");
        if (wg == null || !wg.isEnabled()) return;

        try {
            Class<?> wgPluginClass = Class.forName("com.sk89q.worldguard.bukkit.WorldGuardPlugin");
            Object wgInstance = wgPluginClass.getMethod("inst").invoke(null);
            Object regionContainer = wgPluginClass.getMethod("getRegionContainer").invoke(wgInstance);

            Class<?> flagsClass = Class.forName("com.sk89q.worldguard.protection.flags.Flags");
            Object weatherLockFlag = flagsClass.getField("WEATHER_LOCK").get(null);
            Object timeLockFlag = flagsClass.getField("TIME_LOCK").get(null);
            Class<?> flagClass = Class.forName("com.sk89q.worldguard.protection.flags.Flag");

            for (World world : getServer().getWorlds()) {
                Object regionManager = regionContainer.getClass()
                        .getMethod("get", World.class)
                        .invoke(regionContainer, world);
                if (regionManager == null) continue;

                Object globalRegion = regionManager.getClass()
                        .getMethod("getRegion", String.class)
                        .invoke(regionManager, "__global__");
                if (globalRegion == null) continue;

                globalRegion.getClass()
                        .getMethod("setFlag", flagClass, Object.class)
                        .invoke(globalRegion, weatherLockFlag, null);

                globalRegion.getClass()
                        .getMethod("setFlag", flagClass, Object.class)
                        .invoke(globalRegion, timeLockFlag, null);
            }

            getLogger().info("Bypassed WorldGuard weather/time locks.");
        } catch (Exception | NoClassDefFoundError e) {
            getLogger().warning("Could not bypass WorldGuard locks: " + e.getMessage());
        }
    }

    private void registerPlaceholderAPI() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) return;
        try {
            new NubWeatherExpansion(this).register();
            getLogger().info("PlaceholderAPI expansion registered.");
        } catch (Exception e) {
            getLogger().warning("Failed to register PlaceholderAPI expansion: " + e.getMessage());
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        player.resetPlayerWeather();
        player.resetPlayerTime();
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        World world = event.getWorld();
        prepareWorld(world);
        bypassWorldGuardLocks();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("nubweather")) return false;

        if (!sender.hasPermission("nubweather.admin")) {
            sender.sendMessage(color(msg("no-perm")));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(color("&eUsage: /nubweather reload|sync|time"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                reloadConfig();
                saveDefaultConfig();
                loadTimeZone();
                prepareWorlds();
                bypassWorldGuardLocks();
                registerPlaceholderAPI();
                startSyncTask();
                sender.sendMessage(color(msg("reload")));
            }
            case "sync" -> {
                sync();
                sender.sendMessage(color(msg("sync")));
            }
            case "time" -> {
                sender.sendMessage(color("&eThời gian thực: &f" + getFormattedTime()));
                sender.sendMessage(color("&eGame tick: &f" + getCurrentGameTick()));
                sender.sendMessage(color("&eBuổi: &f" + getPeriod()));
            }
            default -> sender.sendMessage(color("&eUsage: /nubweather reload|sync|time"));
        }

        return true;
    }

    private String msg(String key) {
        String prefix = getConfig().getString("messages.prefix", "&6[NubWeather] &r");
        return prefix + getConfig().getString("messages." + key, key);
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}