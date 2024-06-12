package dev.tocraft.musicplayer.core;

import dev.tocraft.musicplayer.core.commands.ReconnectCommand;
import dev.tocraft.musicplayer.core.config.MusicPlayerConfig;
import dev.tocraft.musicplayer.core.hudwidgets.PlayerTextHudWidget;
import dev.tocraft.musicplayer.core.listener.GameTickHandler;
import dev.tocraft.musicplayer.core.services.ServiceProvider;
import net.labymod.api.LabyAPI;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;
import org.jetbrains.annotations.Nullable;

@AddonMain
public class MusicPlayer extends LabyAddon<MusicPlayerConfig> {

  @Nullable
  private static MusicPlayer INSTANCE;

  @Nullable
  public static MusicPlayer getInstance() {
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
    this.registerCommand(new ReconnectCommand(this));

    ServiceProvider.updateCurrentService(this);
    configuration().serviceType()
        .addChangeListener(type -> ServiceProvider.updateCurrentService(this));

    this.labyAPI().hudWidgetRegistry().register(new PlayerTextHudWidget("text_music_player"));

    this.registerListener(new GameTickHandler(this));

    this.logger().info("Music Player is now enabled.");
  }

  @Override
  protected Class<MusicPlayerConfig> configurationClass() {
    return MusicPlayerConfig.class;
  }
}
