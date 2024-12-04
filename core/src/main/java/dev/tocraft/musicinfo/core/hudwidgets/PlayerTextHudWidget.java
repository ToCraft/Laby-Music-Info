package dev.tocraft.musicinfo.core.hudwidgets;

import dev.tocraft.musicinfo.core.events.ServiceEndEvent;
import dev.tocraft.musicinfo.core.events.SongUpdateEvent;
import dev.tocraft.musicinfo.core.hudwidgets.PlayerTextHudWidget.PlayerTextHudWidgetConfig;
import dev.tocraft.musicinfo.core.services.ServiceProvider;
import java.util.Collection;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine.State;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.event.Subscribe;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PlayerTextHudWidget extends TextHudWidget<PlayerTextHudWidgetConfig> {

  private TextLine trackLine;
  private TextLine artistsLine;
  private TextLine albumLine;
  private TextLine durationLine;
  private TextLine playTimeLine;
  private TextLine remainingTimeLine;

  public PlayerTextHudWidget(String id) {
    super(id, PlayerTextHudWidgetConfig.class);
  }

  @Override
  public void load(PlayerTextHudWidgetConfig config) {
    super.load(config);

    ServiceProvider.connect();

    this.trackLine = super.createLine("Track", "Loading...");
    this.artistsLine = super.createLine("Artists", "Loading...");
    this.albumLine = super.createLine("Album", "Loading...");
    this.durationLine = super.createLine("Duration", "Loading...");
    this.playTimeLine = super.createLine("Play Time", "Loading...");
    this.remainingTimeLine = super.createLine("Remaining Time", "Loading...");

    if (!config.showTrack().get()) {
      this.trackLine.setState(State.DISABLED);
    }
    if (!config.showArtists().get()) {
      this.artistsLine.setState(State.DISABLED);
    }
    if (!config.showAlbum().get()) {
      this.albumLine.setState(State.DISABLED);
    }
    if (!config.showDuration().get()) {
      this.durationLine.setState(State.DISABLED);
    }
    if (!config.showPlayTime().get()) {
      this.playTimeLine.setState(State.DISABLED);
    }
    if (!config.showRemainingTime().get()) {
      this.remainingTimeLine.setState(State.DISABLED);
    }
  }

  @Subscribe
  public void onSongUpdate(@NotNull SongUpdateEvent event) {
    setTextField(trackLine, event.track().name());
    setTextField(artistsLine, event.track().artists());
    setTextField(albumLine, event.track().album());
    setTextField(durationLine, formatTime(event.track().duration()));
    setTextField(playTimeLine, formatTime(event.track().playTime()));
    setTextField(remainingTimeLine, formatTime(event.track().remainingTime()));
  }

  private static void setTextField(@NotNull TextLine line, Object value) {
    if (line.state() != State.DISABLED) {
      if ((value instanceof String s && s.isBlank())
          || (value instanceof Collection<?> c && c.isEmpty())
          || value instanceof Integer i && i < 0) {
        line.setState(State.HIDDEN);
      } else {
        line.setState(State.VISIBLE);
        // reformat Iterable to a more beautiful layout
        if (value instanceof Iterable<?> c) {
          StringBuilder stringBuilder = new StringBuilder();
          for (Object o : c) {
            if (!stringBuilder.isEmpty()) {
              stringBuilder.append(", ");
            }
            stringBuilder.append(o.toString());
          }
          line.updateAndFlush(stringBuilder.toString());
        } else {
          line.updateAndFlush(value);
        }
      }
    }
  }

  @Subscribe
  public void onServiceEnd(@NotNull ServiceEndEvent event) {
    // disable items - no song playing
    if (!event.error().isBlank()) {
      this.trackLine.updateAndFlush(event.error());
      this.trackLine.setState(State.VISIBLE);
    } else if (config.showTrack().get()) {
      this.trackLine.setState(State.HIDDEN);
    }
    if (config.showArtists().get()) {
      this.artistsLine.setState(State.HIDDEN);
    }
    if (config.showDuration().get()) {
      this.durationLine.setState(State.HIDDEN);
    }
    if (config.showPlayTime().get()) {
      this.playTimeLine.setState(State.HIDDEN);
    }
    if (config.showRemainingTime().get()) {
      this.remainingTimeLine.setState(State.HIDDEN);
    }
  }

  @Override
  public boolean isVisibleInGame() {
    return ServiceProvider.getCurrentService() != null
        && ServiceProvider.getCurrentService().isActive();
  }

  @Contract(pure = true)
  private @NotNull String formatTime(int durationInSeconds) {
    if (durationInSeconds < 0) {
      return "";
    }

    int second = durationInSeconds % 60;
    int minute = (durationInSeconds / 60) % 60;

    return String.format("%02d:%02d", minute, second);
  }

  public static class PlayerTextHudWidgetConfig extends TextHudWidgetConfig {

    @SwitchSetting
    private final ConfigProperty<Boolean> showTrack = new ConfigProperty<>(true);
    @SwitchSetting
    private final ConfigProperty<Boolean> showArtists = new ConfigProperty<>(true);
    @SwitchSetting
    private final ConfigProperty<Boolean> showAlbum = new ConfigProperty<>(true);
    @SwitchSetting
    private final ConfigProperty<Boolean> showDuration = new ConfigProperty<>(false);
    @SwitchSetting
    private final ConfigProperty<Boolean> showPlayTime = new ConfigProperty<>(false);
    @SwitchSetting
    private final ConfigProperty<Boolean> showRemainingTime = new ConfigProperty<>(true);

    public ConfigProperty<Boolean> showTrack() {
      return this.showTrack;
    }

    public ConfigProperty<Boolean> showArtists() {
      return this.showArtists;
    }

    public ConfigProperty<Boolean> showAlbum() {
      return this.showAlbum;
    }

    public ConfigProperty<Boolean> showDuration() {
      return this.showDuration;
    }

    public ConfigProperty<Boolean> showPlayTime() {
      return this.showPlayTime;
    }

    public ConfigProperty<Boolean> showRemainingTime() {
      return this.showRemainingTime;
    }
  }
}
