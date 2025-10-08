package rei.cmofl;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.sound.SoundCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rei.cmofl.config.ConfigManager;
import java.util.HashMap;
import java.util.Map;

public class ConfigurableMuteOnFocusLost implements ModInitializer
{
    public static final Logger LOGGER = LoggerFactory.getLogger("Configurable Mute On Focus Lost");
    private boolean lastFocused = true;
    private final Map<String, Double> previousVolumes = new HashMap<>();

	@Override
	public void onInitialize()
    {
        ConfigManager.load();
        ClientTickEvents.END_CLIENT_TICK.register(this::checkWindowFocus);
	}

    private void checkWindowFocus(MinecraftClient client)
    {
        if (ConfigManager.config.enabled)
        {
            boolean focused = client.isWindowFocused();
            if (focused != lastFocused)
            {
                if (!focused)
                {
                    for (Map.Entry<String, Integer> entry : ConfigManager.config.soundCategories.entrySet())
                    {
                        Integer value = entry.getValue();
                        if (value != -1)
                        {
                            String key = entry.getKey();
                            SimpleOption<Double> soundVolumeOption = MinecraftClient.getInstance().options.getSoundVolumeOption(SoundCategory.valueOf(key.toUpperCase()));
                            previousVolumes.put(key, soundVolumeOption.getValue());
                            soundVolumeOption.setValue(value / 100.0);
                        }
                    }
                }
                else
                {
                    for (Map.Entry<String, Double> entry : previousVolumes.entrySet())
                        MinecraftClient.getInstance().options.getSoundVolumeOption(SoundCategory.valueOf(entry.getKey().toUpperCase())).setValue(entry.getValue());
                    previousVolumes.clear();
                }
                lastFocused = focused;
            }
        }
    }
}