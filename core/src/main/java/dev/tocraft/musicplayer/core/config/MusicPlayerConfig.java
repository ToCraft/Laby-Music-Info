package dev.tocraft.musicplayer.core.config;

import dev.tocraft.musicplayer.core.services.ServiceProvider;
import dev.tocraft.musicplayer.core.services.ServiceProvider.ServiceType;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget.ButtonSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.annotation.SpriteSlot;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.Setting;
import net.labymod.api.configuration.settings.annotation.SettingSection;
import net.labymod.api.util.MethodOrder;

@ConfigName("settings")
public class MusicPlayerConfig extends AddonConfig {

  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  @DropdownSetting
  private final ConfigProperty<ServiceType> serviceType = new ConfigProperty<>(
      ServiceType.CIDER_CLASSIC);

  @SwitchSetting
  private final ConfigProperty<Boolean> autoReconnect = new ConfigProperty<>(false);

  @SettingSection("services")
  @SpriteSlot(x = 1, y = 3)
  private final JellyfinSettings jellyfin = new JellyfinSettings();

  @Override
  public ConfigProperty<Boolean> enabled() {
    return this.enabled;
  }

  public ConfigProperty<Boolean> autoReconnect() {
    return this.autoReconnect;
  }

  public ConfigProperty<ServiceType> serviceType() {
    return this.serviceType;
  }

  public JellyfinSettings jellyfin() {
    return jellyfin;
  }

  @ButtonSetting
  @MethodOrder(after = "serviceType")
  public void reconnect(Setting setting) {
    ServiceProvider.reload();
  }
}
