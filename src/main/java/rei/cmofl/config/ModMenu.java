package rei.cmofl.config;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
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

    private Screen createConfigScreen(Screen parent)
    {
        ConfigBuilder configBuilder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("cmofl.config.title"))
                .setSavingRunnable(ConfigManager::save);

        ConfigEntryBuilder configEntryBuilder = configBuilder.entryBuilder();
        ConfigCategory configCategory = configBuilder.getOrCreateCategory(Text.literal("cmofl.config.title"));

        configCategory.addEntry(configEntryBuilder
                .startBooleanToggle(Text.translatable("cmofl.config.enabled"), ConfigManager.config.enabled)
                .setTooltip(Text.translatable("cmofl.config.enabled.tooltip"))
                .setSaveConsumer(value -> ConfigManager.config.enabled = value)
                .build());

        List<AbstractConfigListEntry> abstractConfigListEntryList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : ConfigManager.config.soundCategories.entrySet())
        {
            String key = entry.getKey();
            abstractConfigListEntryList.add(configEntryBuilder.startIntSlider(Text.literal(key), entry.getValue(), -1, 100)
                    .setSaveConsumer(value -> ConfigManager.config.soundCategories.put(key, value))
                    .setTextGetter(integer ->
                    {
                        if (integer == -1)
                            return Text.translatable("cmofl.config.sound_categories.off");
                        return Text.translatable(integer + "%");
                    })
                    .build());
        }

        configCategory.addEntry(configEntryBuilder
                .startSubCategory(Text.translatable("cmofl.config.sound_categories"), abstractConfigListEntryList)
                .setTooltip(Text.translatable("cmofl.config.sound_categories.tooltip"))
                .build());

        return configBuilder.build();
    }
}
