package dev.tocraft.musicplayer.core.hudwidgets;

import dev.tocraft.musicplayer.core.events.ServiceEndEvent;
import dev.tocraft.musicplayer.core.events.SongUpdateEvent;
import dev.tocraft.musicplayer.core.hudwidgets.PlayerTextHudWidget.PlayerTextHudWidgetConfig;
import dev.tocraft.musicplayer.core.misc.ServiceProvider;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine.State;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.event.Subscribe;

public class PlayerTextHudWidget extends TextHudWidget<PlayerTextHudWidgetConfig> {

  private TextLine trackLine;
  private TextLine artistsLine;
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
    this.durationLine = super.createLine("Duration", "Loading...");
    this.playTimeLine = super.createLine("Play Time", "Loading...");
    this.remainingTimeLine = super.createLine("Remaining Time", "Loading...");

    if (!config.showTrack().get()) {
      this.trackLine.setState(State.DISABLED);
    }
    if (!config.showArtists().get()) {
      this.artistsLine.setState(State.DISABLED);
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
  public void onSongUpdate(SongUpdateEvent event) {
    if (event.isPlaying()) {
      // make all items visible
      if (config.showTrack().get()) {
        this.trackLine.setState(State.VISIBLE);
      }
      if (config.showArtists().get()) {
        this.artistsLine.setState(State.VISIBLE);
      }
      if (config.showDuration().get()) {
        this.durationLine.setState(State.VISIBLE);
      }
      if (config.showPlayTime().get()) {
        this.playTimeLine.setState(State.VISIBLE);
      }
      if (config.showRemainingTime().get()) {
        this.remainingTimeLine.setState(State.VISIBLE);
      }
      // set data
      this.trackLine.updateAndFlush(event.track().name());
      this.artistsLine.updateAndFlush(
          event.track().artists().size() == 1 ? event.track().artists().getFirst()
              : event.track().artists());
      this.durationLine.updateAndFlush(formatTime(event.track().duration()));
      this.playTimeLine.updateAndFlush(formatTime(event.track().playTime()));
      this.remainingTimeLine.updateAndFlush(formatTime(event.track().remainingTime()));
    } else {
      // disable items - no song playing
      if (config.showTrack().get()) {
        this.trackLine.updateAndFlush("Nothing Playing");
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
  }

  @Subscribe
  public void onServiceEnd(ServiceEndEvent event) {
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
        && ServiceProvider.getCurrentService().getState() != null;
  }

  private String formatTime(int durationInMillis) {
    int second = durationInMillis % 60;
    int minute = (durationInMillis / 60) % 60;

    return String.format("%02d:%02d", minute, second);
  }

  public static class PlayerTextHudWidgetConfig extends TextHudWidgetConfig {

    @SwitchSetting
    private final ConfigProperty<Boolean> showTrack = new ConfigProperty<>(true);
    @SwitchSetting
    private final ConfigProperty<Boolean> showArtists = new ConfigProperty<>(true);
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
