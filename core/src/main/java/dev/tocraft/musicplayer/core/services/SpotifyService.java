package dev.tocraft.musicplayer.core.services;

import de.labystudio.spotifyapi.SpotifyAPI;
import de.labystudio.spotifyapi.SpotifyListener;
import de.labystudio.spotifyapi.model.Track;
import dev.tocraft.musicplayer.core.events.SongUpdateEvent;
import java.util.List;
import net.labymod.api.LabyAPI;
import net.labymod.api.client.gui.icon.Icon;
import org.jetbrains.annotations.NotNull;
import tech.thatgravyboat.jukebox.api.service.BaseService;
import tech.thatgravyboat.jukebox.api.service.ServicePhase;
import tech.thatgravyboat.jukebox.api.state.RepeatState;

public class SpotifyService extends BaseService implements SpotifyListener {

  private ServicePhase phase = ServicePhase.STOPPED;
  private final LabyAPI labyAPI;

  public SpotifyService(LabyAPI labyAPI, SpotifyAPI spotifyAPI) {
    this.labyAPI = labyAPI;
    spotifyAPI.registerListener(this);
    spotifyAPI.initialize();
  }

  @NotNull
  @Override
  public ServicePhase getPhase() {
    return phase;
  }

  @Override
  public void start() {

  }

  @Override
  public boolean stop() {
    return false;
  }

  @Override
  public void restart() {

  }

  @Override
  public boolean setPaused(boolean b) {
    return false;
  }

  @Override
  public boolean setShuffle(boolean b) {
    return false;
  }

  @Override
  public boolean setRepeat(@NotNull RepeatState repeatState) {
    return false;
  }

  @Override
  public boolean setVolume(int i, boolean b) {
    return false;
  }

  @Override
  public boolean move(boolean b) {
    return false;
  }

  @Override
  public void onConnect() {
    this.phase = ServicePhase.RUNNING;
  }

  @Override
  public void onTrackChanged(Track track) {
    labyAPI.eventBus().fire(new SongUpdateEvent(new dev.tocraft.musicplayer.core.misc.Track() {
      @Override
      public String name() {
        return track.getName();
      }

      @Override
      public int duration() {
        return track.getLength();
      }

      @Override
      public int playTime() {
        return 0;
      }

      @Override
      public List<String> artists() {
        return List.of(track.getArtist());
      }

      @Override
      public Icon cover() {
        return null;
      }
    }, true));
  }

  @Override
  public void onPositionChanged(int Override) {

  }

  @Override
  public void onPlayBackChanged(boolean isPlaying) {
    labyAPI.eventBus().fire(new SongUpdateEvent(null, false));
  }

  @Override
  public void onSync() {

  }

  @Override
  public void onDisconnect(Exception e) {
    this.phase = ServicePhase.STOPPED;
  }
}
