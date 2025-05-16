package dev.tocraft.musicinfo.core.config;

import dev.tocraft.musicinfo.core.services.impl.jellyfin.JellyfinService;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget.ButtonSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.annotation.SpriteSlot;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.Setting;
import net.labymod.api.configuration.settings.annotation.SettingSection;
import net.labymod.api.util.MethodOrder;
import java.util.concurrent.CompletableFuture;

public class JellyfinSettings extends Config {

  @SpriteSlot
  @SettingSection("generic")
  @TextFieldSetting
  private final ConfigProperty<String> serverURL = new ConfigProperty<>("http://localhost:8096");

  @TextFieldSetting
  private final ConfigProperty<String> clientName = new ConfigProperty<>("");

  @SettingSection("authentication")
  @TextFieldSetting
  private final ConfigProperty<String> username = new ConfigProperty<>("jellyfin");

  @TextFieldSetting
  private final ConfigProperty<String> password = new ConfigProperty<>("");

  @TextFieldSetting
  private final ConfigProperty<String> accessToken = new ConfigProperty<>("");

  public ConfigProperty<String> serverURL() {
    return serverURL;
  }

  public ConfigProperty<String> clientName() {
    return clientName;
  }

  public ConfigProperty<String> username() {
    return username;
  }

  public ConfigProperty<String> password() {
    return password;
  }

  public ConfigProperty<String> accessToken() {
    return accessToken;
  }

  @SuppressWarnings("unused")
  @ButtonSetting
  @MethodOrder(before = "accessToken")
  public void generateAccessToken(Setting setting) {
    String serverUrl = this.serverURL().get();
    String username = this.username().get();
    String password = this.password().get();

    // don't crash the client when the server is unreachable
    CompletableFuture.runAsync(() -> {
      this.accessToken().set(
          JellyfinService.getAccessToken(serverUrl, username, password));
      this.password().reset();
    });
  }
}
