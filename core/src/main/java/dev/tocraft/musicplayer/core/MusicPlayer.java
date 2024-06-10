package dev.tocraft.musicplayer.core;

import dev.tocraft.musicplayer.core.hudwidgets.PlayerTextHudWidget;
import dev.tocraft.musicplayer.core.misc.ServiceProvider;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class MusicPlayer extends LabyAddon<MusicPlayerConfig> {

  @Override
  protected void enable() {
    this.registerSettingCategory();

    ServiceProvider.initialize(this.labyAPI());
    ServiceProvider.updateCurrentService(configuration());
    configuration().serviceType()
        .addChangeListener(type -> ServiceProvider.updateCurrentService(configuration()));

    this.labyAPI().hudWidgetRegistry().register(new PlayerTextHudWidget("text_music_player", this));

    this.logger().info("Music Player is now enabled.");
  }

  @Override
  protected Class<MusicPlayerConfig> configurationClass() {
    return MusicPlayerConfig.class;
  }
}