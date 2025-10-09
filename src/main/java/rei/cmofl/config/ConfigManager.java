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
            Config config1 = null;
            try
            {
                Toml toml = new Toml().read(configFile);
                config1 = toml.to(Config.class);
            }
            catch (Exception exception)
            {
                ConfigurableMuteOnFocusLost.LOGGER.error(getTextString("cmofl.config.exception.load"), exception);
                ConfigurableMuteOnFocusLost.LOGGER.warn(getTextString("cmofl.config.exception.default"));
                resetConfigToDefault();
            }
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
                ConfigurableMuteOnFocusLost.LOGGER.error(getTextString("cmofl.config.exception.create"), ioException);
            else
            {
                ConfigurableMuteOnFocusLost.LOGGER.error(Text.translatable("cmofl.config.exception.save").getString(), ioException);
                ConfigurableMuteOnFocusLost.LOGGER.warn(Text.translatable("cmofl.config.exception.default").getString());
                resetConfigToDefault();
            }
        }
    }

    private static void resetConfigToDefault()
    {
        TomlWriter writer = new TomlWriter();
        config = new Config();
        try
        {
            writer.write(config, configFile);
        }
        catch (IOException ignored) {}
    }

    private static String getTextString(String langKey) // For some reason, during mod initialization Text.translatable("langKey").getString() returns the langKey instead of the actual text based in the game's language, I couldn't find an alternative so hardcoded English it is...
    {
        String textString = Text.translatable(langKey).getString();
        if (langKey.equals(textString))
        {
            switch (langKey)
            {
                case "cmofl.config.exception.default":
                    return "Resetting config to default as a fix.";
                case "cmofl.config.exception.create":
                    return "An exception was been thrown while trying to create the config:";
                case "cmofl.config.exception.load":
                    return "An exception was been thrown while trying to load the config:";
            }
        }
        return textString;
    }
}
