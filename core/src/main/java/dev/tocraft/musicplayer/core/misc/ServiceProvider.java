package dev.tocraft.musicplayer.core.misc;

import dev.tocraft.musicplayer.core.MusicPlayerConfig;
import dev.tocraft.musicplayer.core.events.SongUpdateEvent;
import java.util.List;
import net.labymod.api.LabyAPI;
import net.labymod.api.client.gui.icon.Icon;
import org.jetbrains.annotations.Nullable;
import tech.thatgravyboat.jukebox.api.events.EventType;
import tech.thatgravyboat.jukebox.api.events.callbacks.UpdateEvent;
import tech.thatgravyboat.jukebox.api.service.BaseService;
import tech.thatgravyboat.jukebox.impl.apple.AppleService;
import tech.thatgravyboat.jukebox.impl.cider.CiderService;
import tech.thatgravyboat.jukebox.impl.foobar.FoobarService;
import tech.thatgravyboat.jukebox.impl.spotify.SpotifyService;
import tech.thatgravyboat.jukebox.impl.tidal.TidalService;
import tech.thatgravyboat.jukebox.impl.youtube.YoutubeService;
import tech.thatgravyboat.jukebox.impl.youtubev2.YoutubeServiceV2;

public class ServiceProvider {

  @Nullable
  private static BaseService currentService = null;

  public enum ServiceType {
    CIDER, CIDER2, YOUTUBE, YOUTUBE2, SPOTIFY, BEEFWEB, TIDAL;

    public BaseService getService(MusicPlayerConfig config) {
      return switch (this) {
        case CIDER -> new AppleService();
        case CIDER2 -> new CiderService();
        case YOUTUBE -> new YoutubeService(config.youtubePassword().get());
        case YOUTUBE2 -> new YoutubeServiceV2(config.youtubePassword().get());
        case SPOTIFY -> new SpotifyService(config.spotifyToken().get());
        case BEEFWEB -> new FoobarService();
        case TIDAL -> new TidalService();
      };
    }
  }

  private static LabyAPI labyAPI = null;

  public static void initialize(LabyAPI labyAPI) {
    ServiceProvider.labyAPI = labyAPI;
  }

  private static final JukeboxEventHandler<UpdateEvent> onSongUpdate = event -> {
    if (labyAPI != null) {
      labyAPI.eventBus().fire(new SongUpdateEvent(new Track() {
        @Override
        public String name() {
          return event.getState().getSong().getTitle();
        }

        @Override
        public int duration() {
          return event.getState().getSongState().getDuration();
        }

        @Override
        public int playTime() {
          return event.getState().getSongState().getProgress();
        }

        @Override
        public List<String> artists() {
          return event.getState().getSong().getArtists();
        }

        @Override
        public Icon cover() {
          return Icon.url(event.getState().getSong().getCover());
        }
      }, event.getState().isPlaying()));
    }
  };

  public static void updateCurrentService(MusicPlayerConfig config) {
    if (currentService != null) {
      currentService.unregisterListener(EventType.Companion.getUPDATE(), onSongUpdate);
      currentService.stop();
    }
    currentService = config.serviceType().get().getService(config);
    currentService.registerListener(EventType.Companion.getUPDATE(), onSongUpdate);
    currentService.start();
  }

  @Nullable
  public static BaseService getCurrentService() {
    return currentService;
  }
}
