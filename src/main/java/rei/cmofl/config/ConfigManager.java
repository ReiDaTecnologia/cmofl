package rei.cmofl.config;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import rei.cmofl.ConfigurableMuteOnFocusLost;
import java.io.File;
import java.io.IOException;

public class ConfigManager
{
    private static final File configFile = FabricLoader.getInstance().getConfigDir().resolve("cmofl.toml").toFile();
    public static Config config = new Config();

    public static void load()
    {
        if (configFile.exists())
        {
            Toml toml = new Toml().read(configFile);
            Config config1 = toml.to(Config.class);
            if (config1 != null)
                config = config1;
        }
        else
            save(true);
    }

    public static void save()
    {
        save(false);
    }

    private static void save(boolean creatingConfig)
    {
        TomlWriter writer = new TomlWriter();
        try
        {
            writer.write(config, configFile);
        }
        catch (IOException ioException)
        {
            if (creatingConfig)
                ConfigurableMuteOnFocusLost.LOGGER.error(Text.translatable("cmofl.config.exception.create").getString(), ioException);
            else
            {
                ConfigurableMuteOnFocusLost.LOGGER.error(Text.translatable("cmofl.config.exception.save").getString(), ioException);
                ConfigurableMuteOnFocusLost.LOGGER.warn(Text.translatable("cmofl.config.exception.default").getString());
                config = new Config();
                try
                {
                    writer.write(config, configFile);
                }
                catch (IOException ignored) {}
            }
        }
    }
}