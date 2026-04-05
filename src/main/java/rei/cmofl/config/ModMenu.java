package rei.cmofl.config;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ModMenu implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return this::createConfigScreen;
    }

    @SuppressWarnings("rawtypes")
    private Screen createConfigScreen(Screen parent)
    {
        ConfigBuilder configBuilder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("cmofl.config.title"))
                .setSavingRunnable(ConfigManager::save);

        ConfigEntryBuilder configEntryBuilder = configBuilder.entryBuilder();
        ConfigCategory configCategory = configBuilder.getOrCreateCategory(Component.literal("cmofl.config.title"));

        configCategory.addEntry(configEntryBuilder
                .startBooleanToggle(Component.translatable("cmofl.config.enabled"), ConfigManager.config.enabled)
                .setTooltip(Component.translatable("cmofl.config.enabled.tooltip"))
                .setDefaultValue(true)
                .setSaveConsumer(value -> ConfigManager.config.enabled = value)
                .build());

        configCategory.addEntry(configEntryBuilder
                .startBooleanToggle(Component.translatable("cmofl.config.muteallsounds"), ConfigManager.config.muteAllSounds)
                .setTooltip(Component.translatable("cmofl.config.muteallsounds.tooltip"))
                .setDefaultValue(false)
                .setSaveConsumer(value -> ConfigManager.config.muteAllSounds = value)
                .build());

        List<AbstractConfigListEntry> abstractConfigListEntryList = new ArrayList<>();
        for (Map.Entry<SoundSource, Integer> entry : ConfigManager.config.soundSources.entrySet())
        {
            SoundSource soundSource = entry.getKey();
            abstractConfigListEntryList.add(configEntryBuilder
                    .startIntSlider(Component.literal(soundSource.getName()), entry.getValue(), -1, 100)
                    .setDefaultValue(soundSource.equals(SoundSource.MUSIC) || soundSource.equals(SoundSource.RECORDS) ? 0 : -1)
                    .setSaveConsumer(value -> ConfigManager.config.soundSources.put(soundSource, value))
                    .setTextGetter(integer ->
                    {
                        if (integer == -1)
                            return Component.translatable("cmofl.config.sound_categories.off");
                        return Component.translatable(integer + "%");
                    })
                    .build());
        }

        configCategory.addEntry(configEntryBuilder
                .startSubCategory(Component.translatable("cmofl.config.sound_categories"), abstractConfigListEntryList)
                .setTooltip(Component.translatable("cmofl.config.sound_categories.tooltip"))
                .build());

        return configBuilder.build();
    }
}