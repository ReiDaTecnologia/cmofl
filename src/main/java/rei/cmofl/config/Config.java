package rei.cmofl.config;
import net.minecraft.sound.SoundCategory;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Config
{
    public boolean enabled = true;
    public boolean muteAllSounds = false;
    public Map<SoundCategory, Integer> soundCategories = new HashMap<>();

    public Config()
    {
        for (SoundCategory soundCategory : SoundCategory.values())
        {
            Integer value = Objects.equals(soundCategory, SoundCategory.MUSIC) || Objects.equals(soundCategory, SoundCategory.RECORDS) ? 0 : -1;
            soundCategories.put(soundCategory, value);
        }
    }
}
