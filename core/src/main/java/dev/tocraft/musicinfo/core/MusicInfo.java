package dev.tocraft.musicinfo.core;

import dev.tocraft.musicinfo.core.commands.ReconnectCommand;
import dev.tocraft.musicinfo.core.config.MusicInfoConfig;
import dev.tocraft.musicinfo.core.hudwidgets.PlayerTextHudWidget;
import dev.tocraft.musicinfo.core.listener.GameTickHandler;
import dev.tocraft.musicinfo.core.services.ServiceProvider;
import net.labymod.api.LabyAPI;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;
import org.jetbrains.annotations.Nullable;

@AddonMain
public class MusicInfo extends LabyAddon<MusicInfoConfig> {
  public static final String VERSION = "1.0";

  @Nullable
  private static MusicInfo INSTANCE;

  @Nullable
  public static MusicInfo getInstance() {
    return INSTANCE;
  }

  public static LabyAPI getLabyAPI() {
    if (INSTANCE != null) {
      return INSTANCE.labyAPI();
    } else {
      return null;
    }
  }

  @Override
  protected void enable() {
    INSTANCE = this;

    this.registerSettingCategory();
    this.registerCommand(new ReconnectCommand());

    ServiceProvider.updateCurrentService(this);
    configuration().serviceType()
        .addChangeListener(type -> ServiceProvider.updateCurrentService(this));

    this.labyAPI().hudWidgetRegistry().register(new PlayerTextHudWidget("text_music_info"));

    this.registerListener(new GameTickHandler(this));

    this.logger().info("Music Info Addon is now enabled.");
  }

  @Override
  protected Class<MusicInfoConfig> configurationClass() {
    return MusicInfoConfig.class;
  }
}
