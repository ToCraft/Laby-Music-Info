package dev.tocraft.musicplayer.core.services.impl.spotify;

import de.labystudio.spotifyapi.SpotifyAPI;
import de.labystudio.spotifyapi.SpotifyAPIFactory;
import de.labystudio.spotifyapi.SpotifyListener;
import de.labystudio.spotifyapi.model.Track;
import de.labystudio.spotifyapi.open.model.track.OpenTrack;
import dev.tocraft.musicplayer.core.events.ServiceEndEvent;
import dev.tocraft.musicplayer.core.events.SongUpdateEvent;
import dev.tocraft.musicplayer.core.services.AbstractService;
import java.io.IOException;
import java.util.List;
import net.labymod.api.LabyAPI;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.util.logging.Logging;

public class SpotifyService extends AbstractService implements SpotifyListener {

  private final LabyAPI labyAPI;
  private final SpotifyAPI spotifyAPI;
  private final Logging logger;

  public SpotifyService(LabyAPI labyAPI, Logging logger) {
    this.labyAPI = labyAPI;
    this.logger = logger;
    this.spotifyAPI = SpotifyAPIFactory.create();
    this.spotifyAPI.registerListener(this);
  }

  @Override
  public boolean isActive() {
    return spotifyAPI.isConnected();
  }

  @Override
  public void start() {
    this.spotifyAPI.initialize();
  }

  @Override
  public void stop() {
    this.spotifyAPI.stop();
  }

  @Override
  public void restart() {
    stop();
    start();
  }

  @Override
  public void onConnect() {
  }

  @Override
  public void onTrackChanged(Track track) {
    OpenTrack tempOpenTrack = null;
    try {
      tempOpenTrack = spotifyAPI.getOpenAPI().requestOpenTrack(track);
    } catch (IOException e) {
      logger.error("Can't convert Spotify Track to Open Track, Caught an exception: {}", e);
    }
    OpenTrack openTrack = tempOpenTrack;
    labyAPI.eventBus().fire(new SongUpdateEvent(new dev.tocraft.musicplayer.core.misc.Track() {
      @Override
      public String name() {
        return track.getName();
      }

      @Override
      public String album() {
        if (openTrack != null) {
          return openTrack.album.name;
        } else {
          return "";
        }
      }

      @Override
      public int duration() {
        return track.getLength() / 1000;
      }

      @Override
      public int playTime() {
        return -1;
      }

      @Override
      public List<String> artists() {
        if (openTrack != null) {
          return openTrack.artists.stream().map(artist -> artist.name).toList();
        } else {
          return List.of(track.getArtist());
        }
      }

      @Override
      public Icon cover() {
        if (openTrack != null) {
          return Icon.url(
              openTrack.album.images.getFirst().url);
        } else {
          return null;
        }
      }
    }));
  }

  @Override
  public void onPositionChanged(int Override) {

  }

  @Override
  public void onPlayBackChanged(boolean isPlaying) {

  }

  @Override
  public void onSync() {

  }

  @Override
  public void onDisconnect(Exception e) {
    labyAPI.eventBus().fire(new ServiceEndEvent(e.getMessage()));
  }
}
