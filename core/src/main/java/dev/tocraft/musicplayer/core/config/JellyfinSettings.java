package dev.tocraft.musicplayer.core.config;

import dev.tocraft.musicplayer.core.services.impl.jellyfin.JellyfinService;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget.ButtonSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.Setting;
import net.labymod.api.util.MethodOrder;

// TODO: Maybe create some option to select, which jellyfin client should be watched
@SuppressWarnings("unused")
public class JellyfinSettings extends Config {

  @TextFieldSetting
  private final ConfigProperty<String> serverURL = new ConfigProperty<>("http://localhost:8096");

  @TextFieldSetting
  private final ConfigProperty<String> accessToken = new ConfigProperty<>("");

  @TextFieldSetting
  private final ConfigProperty<String> username = new ConfigProperty<>("jellyfin");

  @TextFieldSetting
  private final ConfigProperty<String> password = new ConfigProperty<>("");

  public ConfigProperty<String> serverURL() {
    return serverURL;
  }

  public ConfigProperty<String> accessToken() {
    return accessToken;
  }

  public ConfigProperty<String> username() {
    return username;
  }

  public ConfigProperty<String> password() {
    return password;
  }

  @ButtonSetting
  @MethodOrder(after = "password")
  public void generateAccessToken(Setting setting) {
    this.accessToken().set(
        JellyfinService.getAccessToken(this.serverURL().get(), this.username().get(),
            this.password().get()));
    this.username().reset();
    this.password().reset();
  }
}
