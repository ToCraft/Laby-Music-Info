package dev.tocraft.musicplayer.core;

import dev.tocraft.musicplayer.core.commands.ReconnectCommand;
import dev.tocraft.musicplayer.core.hudwidgets.PlayerTextHudWidget;
import dev.tocraft.musicplayer.core.listener.GameTickHandler;
import dev.tocraft.musicplayer.core.services.ServiceProvider;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class MusicPlayer extends LabyAddon<MusicPlayerConfig> {

  @Override
  protected void enable() {
    this.registerSettingCategory();
    this.registerCommand(new ReconnectCommand(this));

    ServiceProvider.initialize(this.labyAPI());
    ServiceProvider.updateCurrentService(this);
    logger().info("lol");
    configuration().serviceType()
        .addChangeListener(type -> ServiceProvider.updateCurrentService(this));
    logger().info("?");

    this.labyAPI().hudWidgetRegistry().register(new PlayerTextHudWidget("text_music_player"));

    this.registerListener(new GameTickHandler(this));

    this.logger().info("Music Player is now enabled.");
  }

  @Override
  protected Class<MusicPlayerConfig> configurationClass() {
    return MusicPlayerConfig.class;
  }
}
