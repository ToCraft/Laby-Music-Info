package dev.tocraft.musicplayer.core;

import dev.tocraft.musicplayer.core.misc.ServiceProvider.ServiceType;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.property.ConfigProperty;

@SuppressWarnings("unused")
@ConfigName("settings")
public class MusicPlayerConfig extends AddonConfig {

  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  @DropdownSetting
  private final ConfigProperty<ServiceType> serviceType = new ConfigProperty<>(ServiceType.CIDER);

  @TextFieldSetting
  private final ConfigProperty<String> spotifyToken = new ConfigProperty<>("");

  @TextFieldSetting
  private final ConfigProperty<String> youtubePassword = new ConfigProperty<>("");

  @TextFieldSetting
  private final ConfigProperty<String> youtubeToken = new ConfigProperty<>("");

  @Override
  public ConfigProperty<Boolean> enabled() {
    return this.enabled;
  }

  public ConfigProperty<ServiceType> serviceType() {
    return this.serviceType;
  }

  public ConfigProperty<String> spotifyToken() {
    return this.spotifyToken;
  }

  public ConfigProperty<String> youtubePassword() {
    return this.youtubePassword;
  }

  public ConfigProperty<String> youtubeToken() {
    return this.youtubeToken;
  }
}
