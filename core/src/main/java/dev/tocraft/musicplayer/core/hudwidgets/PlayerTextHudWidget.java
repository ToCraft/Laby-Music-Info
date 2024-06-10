package dev.tocraft.musicplayer.core.hudwidgets;

import dev.tocraft.musicplayer.core.MusicPlayer;
import dev.tocraft.musicplayer.core.events.SongUpdateEvent;
import dev.tocraft.musicplayer.core.misc.ServiceProvider;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.event.Subscribe;

public class PlayerTextHudWidget extends TextHudWidget<TextHudWidgetConfig> {

  private TextLine trackLine;
  private TextLine artistLine;
  private TextLine remainingTimeLine;
  private final MusicPlayer addon;

  public PlayerTextHudWidget(String id, MusicPlayer addon) {
    super(id);
    this.addon = addon;
  }

  @Override
  public void load(TextHudWidgetConfig config) {
    super.load(config);

    this.trackLine = super.createLine("Track", "Loading...");
    this.artistLine = super.createLine("Artist", "Loading...");
    this.remainingTimeLine = super.createLine("Remaining Time", "Loading...");
  }

  @Subscribe
  public void onSongUpdate(SongUpdateEvent event) {
    this.trackLine.updateAndFlush(event.track().name());
    this.artistLine.updateAndFlush(event.track().artists());
    this.remainingTimeLine.updateAndFlush(
        formatTime(event.track().remainingTime()));
  }

  @Override

  public boolean isVisibleInGame() {
    if (!addon.configuration().enabled().get()) {
      return false;
    } else {
      return ServiceProvider.getCurrentService() != null
          && ServiceProvider.getCurrentService().getState() != null;
    }
  }

  private String formatTime(int durationInMillis) {
    int second = durationInMillis % 60;
    int minute = (durationInMillis / 60) % 60;

    return String.format("%02d:%02d", minute, second);
  }
}
