package dev.tocraft.musicplayer.core;

import dev.tocraft.musicplayer.core.listener.MusicPlayerEventListener;
import dev.tocraft.musicplayer.core.hudwidgets.PlayerHudWidget;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.gui.hud.HudWidgetRegistry;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class MusicPlayer extends LabyAddon<MusicPlayerConfig> {

  @Override
  protected void enable() {
    this.registerSettingCategory();

    HudWidgetRegistry registry = this.labyAPI().hudWidgetRegistry();
    registry.register(new PlayerHudWidget("music_player", this));

    this.registerListener(new MusicPlayerEventListener(this));

    this.logger().info("Music Player is now enabled.");
  }

  @Override
  protected Class<MusicPlayerConfig> configurationClass() {
    return MusicPlayerConfig.class;
  }
}
