package dev.tocraft.musicplayer.core.hudwidgets;

import dev.tocraft.musicplayer.core.MusicPlayer;
import dev.tocraft.musicplayer.core.hudwidgets.PlayerHudWidget.PlayerHudWidgetConfig;
import dev.tocraft.musicplayer.core.widgets.PlayerWidget;
import net.labymod.api.client.gui.hud.hudwidget.HudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.widget.WidgetHudWidget;
import net.labymod.api.client.gui.screen.widget.widgets.hud.HudWidgetWidget;

public class PlayerHudWidget extends WidgetHudWidget<PlayerHudWidgetConfig> {

  private final MusicPlayer addon;

  public PlayerHudWidget(String id, MusicPlayer addon) {
    super(id, PlayerHudWidgetConfig.class);
    this.addon = addon;
  }

  @Override
  public void initialize(HudWidgetWidget widget) {
    super.initialize(widget);

    PlayerWidget playerWidget = new PlayerWidget(addon);
    widget.addChild(playerWidget);
    widget.addId("player");
  }

  @Override
  public boolean isVisibleInGame() {
    return addon.configuration().supportCiderSh().get();
  }

  public static class PlayerHudWidgetConfig extends HudWidgetConfig {

  }
}
