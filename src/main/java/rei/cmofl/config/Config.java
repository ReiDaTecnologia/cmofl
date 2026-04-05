package rei.cmofl.config;
import net.minecraft.sounds.SoundSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Config
{
    public boolean enabled = true;
    public boolean muteAllSounds = false;
    public Map<SoundSource, Integer> soundSources = new HashMap<>();

    public Config()
    {
        for (SoundSource soundSource : SoundSource.values())
        {
            Integer value = Objects.equals(soundSource, SoundSource.MUSIC) || Objects.equals(soundSource, SoundSource.RECORDS) ? 0 : -1;
            soundSources.put(soundSource, value);
        }
    }
}