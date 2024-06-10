package dev.tocraft.musicplayer.core.hudwidgets;

import dev.tocraft.musicplayer.core.MusicPlayer;
import dev.tocraft.musicplayer.core.events.SongUpdateEvent;
import dev.tocraft.musicplayer.core.misc.Track;
import dev.tocraft.musicplayer.core.services.cider.CiderService;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.event.Subscribe;

public class PlayerTextHudWidget extends TextHudWidget<TextHudWidgetConfig> {

  private final CiderService service = new CiderService();

  private TextLine trackLine;
  private TextLine artistLine;
  private TextLine remainingTimeLine;
  private final MusicPlayer addon;

  public PlayerTextHudWidget(String id, MusicPlayer addon) {
    super(id);
    this.addon = addon;
    this.service.connect();
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
    addon.logger().warn("GOT HERE!!!");
    this.trackLine.updateAndFlush("LOL" + event.track().name());
    this.artistLine.updateAndFlush(event.track().artists());
    this.remainingTimeLine.updateAndFlush(
        event.track().duration() - event.track().playTime());
  }

  @Override
  public void onTick(boolean isEditorContext) {
    super.onTick(isEditorContext);

    /*if (ServiceProvider.getCurrentService() != null) {
      State state = ServiceProvider.getCurrentService().getState();
      if (state != null) {
        addon.logger().warn("DID GET HIERE o.O");
        this.trackLine.updateAndFlush("t" +state.getSong().getTitle());
        this.artistLine.updateAndFlush(state.getSong().getArtists());
        this.remainingTimeLine.updateAndFlush(
            state.getSongState().getDuration() - state.getSongState().getProgress());
      }
    } else {*/
    Track track = service.getCurrentTrack(addon);
    if (track != null) {
      this.trackLine.updateAndFlush(track.name());
      this.artistLine.updateAndFlush(track.artists());
      this.remainingTimeLine.updateAndFlush(
          formatTime((long) (double) (track.duration() - track.playTime())));
    }
    //}
  }

  @Override
  public boolean isVisibleInGame() {
    if (!addon.configuration().enabled().get()) {
      return false;
    } else {
      return //ServiceProvider.getCurrentService() != null
          true
          /*&& ServiceProvider.getCurrentService().getState() != null*/;
    }
  }

  private String formatTime(long durationInMillis) {
    long millis = durationInMillis % 1000;
    long second = (durationInMillis / 1000) % 60;
    long minute = (durationInMillis / (1000 * 60)) % 60;

    return String.format("%02d:%02d.%d", minute, second, millis);
  }
}
