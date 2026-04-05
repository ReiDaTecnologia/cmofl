package rei.cmofl;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rei.cmofl.config.ConfigManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigurableMuteOnFocusLost implements ModInitializer
{
    public static final Logger LOGGER = LoggerFactory.getLogger("Configurable Mute On Focus Lost");
    private boolean lastFocused = true;
    private final Map<SoundSource, Double> previousVolumes = new HashMap<>();

	@Override
	public void onInitialize()
    {
        ConfigManager.load();
        ClientTickEvents.END_CLIENT_TICK.register(this::checkWindowFocus);
	}

    private void checkWindowFocus(Minecraft mc)
    {
        if (ConfigManager.config.enabled)
        {
            boolean focused = mc.isWindowActive();
            SoundManager soundManager = mc.getSoundManager();
            if (focused != lastFocused)
            {
                if (!focused)
                {
                    if (ConfigManager.config.muteAllSounds)
                    {
                        for (SoundSource soundSource : SoundSource.values())
                        {
                            OptionInstance<Double> soundSourceOptionInstance = mc.options.getSoundSourceOptionInstance(soundSource);
                            previousVolumes.put(soundSource, soundSourceOptionInstance.get());
                            soundSourceOptionInstance.set(0.0);
                            setPreview(soundSourceOptionInstance, 0.0, soundManager, soundSource);
                        }
                    }
                    else
                    {
                        for (Map.Entry<SoundSource, Integer> entry : ConfigManager.config.soundSources.entrySet())
                        {
                            Integer value = entry.getValue();
                            if (value != -1)
                            {
                                SoundSource soundSource = entry.getKey();
                                OptionInstance<Double> soundSourceOptionInstance = mc.options.getSoundSourceOptionInstance(soundSource);
                                previousVolumes.put(soundSource, soundSourceOptionInstance.get());
                                setPreview(soundSourceOptionInstance,value / 100.0, soundManager, soundSource);
                            }
                        }
                    }
                }
                else
                {
                    for (Map.Entry<SoundSource, Double> entry : previousVolumes.entrySet())
                    {
                        SoundSource soundSource = entry.getKey();
                        OptionInstance<Double> soundSourceOptionInstance = mc.options.getSoundSourceOptionInstance(soundSource);
                        setPreview(soundSourceOptionInstance, entry.getValue(), soundManager, soundSource);
                    }
                    previousVolumes.clear();
                }
                lastFocused = focused;
            }
        }
    }

    // After changing the value, OptionInstance.set() call a consumer that play a preview sound after calling SoundManager.refreshCategoryVolume(),
    // this avoid that by just changing the value and calling refreshCategoryVolume directly.
    private void setPreview(OptionInstance<Double> doubleOptionInstance, double value, SoundManager soundManager, SoundSource soundSource)
    {
        if (!Objects.equals(doubleOptionInstance.value, value))
        {
            doubleOptionInstance.value = value;
            soundManager.refreshCategoryVolume(soundSource);
        }
    }
}