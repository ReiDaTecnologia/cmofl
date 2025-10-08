package rei.cmofl.config;
import net.minecraft.sound.SoundCategory;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Config
{
    public boolean enabled = true;
    public Map<String, Integer> soundCategories = new HashMap<>();

    public Config()
    {
        for (SoundCategory soundCategory : SoundCategory.values())
        {
            String key = soundCategory.getName();
            Integer value = Objects.equals(key, SoundCategory.MUSIC.getName()) || Objects.equals(key, SoundCategory.RECORDS.getName()) ? 0 : -1;
            if (Objects.equals(key, SoundCategory.RECORDS.getName()) || Objects.equals(key, SoundCategory.BLOCKS.getName()) || Objects.equals(key, SoundCategory.PLAYERS.getName()))
                key = key + "s"; // For some reason their names are missing an "s" making SoundCategory.valueOf fail to find them
            soundCategories.put(key, value);
        }
    }
}
