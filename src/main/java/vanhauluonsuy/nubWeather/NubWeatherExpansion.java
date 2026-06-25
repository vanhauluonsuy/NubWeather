package vanhauluonsuy.nubWeather;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class NubWeatherExpansion extends PlaceholderExpansion {

    private final NubWeather plugin;

    public NubWeatherExpansion(NubWeather plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "nubweather";
    }

    @Override
    public String getAuthor() {
        return "vanhauluonsuy";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier == null) return null;

        String id = identifier.toLowerCase();
        if (id.equals("time")) return plugin.getFormattedTime();
        if (id.equals("time_full")) return plugin.getFormattedFullTime();
        if (id.equals("tick")) return String.valueOf(plugin.getCurrentGameTick());
        if (id.equals("period")) return plugin.getPeriod();

        return null;
    }
}