package dev.tocraft.musicplayer.core.misc;

import de.labystudio.spotifyapi.SpotifyAPIFactory;
import dev.tocraft.musicplayer.core.MusicPlayer;
import dev.tocraft.musicplayer.core.events.ServiceEndEvent;
import dev.tocraft.musicplayer.core.events.SongUpdateEvent;
import dev.tocraft.musicplayer.core.services.SpotifyService;
import java.util.List;
import net.labymod.api.LabyAPI;
import net.labymod.api.client.gui.icon.Icon;
import org.jetbrains.annotations.Nullable;
import tech.thatgravyboat.jukebox.api.events.EventType;
import tech.thatgravyboat.jukebox.api.events.callbacks.ServiceEndedEvent;
import tech.thatgravyboat.jukebox.api.events.callbacks.ServiceErrorEvent;
import tech.thatgravyboat.jukebox.api.events.callbacks.ServiceUnauthorizedEvent;
import tech.thatgravyboat.jukebox.api.events.callbacks.SongChangeEvent;
import tech.thatgravyboat.jukebox.api.events.callbacks.UpdateEvent;
import tech.thatgravyboat.jukebox.api.service.BaseService;
import tech.thatgravyboat.jukebox.impl.apple.AppleService;
import tech.thatgravyboat.jukebox.impl.cider.CiderService;
import tech.thatgravyboat.jukebox.impl.foobar.FoobarService;
import tech.thatgravyboat.jukebox.impl.tidal.TidalService;
import tech.thatgravyboat.jukebox.impl.youtube.YoutubeService;
import tech.thatgravyboat.jukebox.impl.youtubev2.YoutubeServiceV2;

public class ServiceProvider {

  @Nullable
  private static BaseService currentService = null;

  public enum ServiceType {
    CIDER, CIDER2, YOUTUBE, YOUTUBE2, SPOTIFY1, SPOTIFY2, BEEFWEB, TIDAL;

    public BaseService getService(MusicPlayer addon) {
      return switch (this) {
        case CIDER -> new AppleService();
        case CIDER2 -> new CiderService();
        case YOUTUBE -> new YoutubeService(addon.configuration().youtubePassword().get());
        case YOUTUBE2 -> new YoutubeServiceV2(addon.configuration().youtubeToken().get());
        case SPOTIFY1 -> new tech.thatgravyboat.jukebox.impl.spotify.SpotifyService(
            addon.configuration().spotifyToken().get());
        case SPOTIFY2 -> new SpotifyService(addon.labyAPI(), SpotifyAPIFactory.create());
        case BEEFWEB -> new FoobarService();
        case TIDAL -> new TidalService();
      };
    }
  }

  @Nullable
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

  private static final JukeboxEventHandler<SongChangeEvent> onSongChange = event -> {
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

  private static final JukeboxEventHandler<ServiceEndedEvent> onConnectionEnd = event -> {
    if (labyAPI != null) {
      connect();
      labyAPI.eventBus().fire(new ServiceEndEvent(""));
    }
  };

  private static final JukeboxEventHandler<ServiceErrorEvent> onCaughtError = event -> {
    if (labyAPI != null) {
      connect();
      labyAPI.eventBus().fire(new ServiceEndEvent(event.getError()));
    }
  };

  private static final JukeboxEventHandler<ServiceUnauthorizedEvent> onUnauthorized = event -> {
    if (labyAPI != null) {
      connect();
      labyAPI.eventBus().fire(new ServiceEndEvent("unauthorized"));
    }
  };

  public static void updateCurrentService(MusicPlayer addon) {
    if (currentService != null) {
      currentService.unregisterListener(EventType.Companion.getUPDATE(), onSongUpdate);
      currentService.unregisterListener(EventType.Companion.getSERVICE_ENDED(), onConnectionEnd);
      currentService.unregisterListener(EventType.Companion.getSERVICE_ERROR(), onCaughtError);
      currentService.unregisterListener(EventType.Companion.getSONG_CHANGE(), onSongChange);
      currentService.unregisterListener(EventType.Companion.getSERVICE_UNAUTHORIZED(), onUnauthorized);
      currentService.stop();
    }
    ServiceType currentServiceType = addon.configuration().serviceType().get();
    currentService = currentServiceType.getService(addon);
    currentService.registerListener(EventType.Companion.getUPDATE(), onSongUpdate);
    currentService.registerListener(EventType.Companion.getSERVICE_ENDED(), onConnectionEnd);
    currentService.registerListener(EventType.Companion.getSERVICE_ERROR(), onCaughtError);
    currentService.registerListener(EventType.Companion.getSONG_CHANGE(), onSongChange);
    currentService.registerListener(EventType.Companion.getSERVICE_UNAUTHORIZED(), onUnauthorized);
  }

  public static void connect() {
    if (currentService != null) {
      currentService.restart();
    }
  }

  @Nullable
  public static BaseService getCurrentService() {
    return currentService;
  }
}
