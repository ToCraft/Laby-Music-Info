package dev.tocraft.musicplayer.core.services;

import dev.tocraft.musicplayer.core.MusicPlayer;
import dev.tocraft.musicplayer.core.services.impl.cider.CiderClassicService;
import dev.tocraft.musicplayer.core.services.impl.jellyfin.JellyfinService;
import dev.tocraft.musicplayer.core.services.impl.jukebox.JukeboxService;
import dev.tocraft.musicplayer.core.services.impl.spotify.SpotifyService;
import org.jetbrains.annotations.Nullable;
import tech.thatgravyboat.jukebox.impl.cider.CiderService;
import tech.thatgravyboat.jukebox.impl.foobar.FoobarService;
import tech.thatgravyboat.jukebox.impl.tidal.TidalService;

public class ServiceProvider {

  @Nullable
  private static AbstractService currentService = null;

  public enum ServiceType {
    CIDER_CLASSIC, CIDER, JELLYFIN, SPOTIFY, BEEFWEB, TIDAL;

    public AbstractService getService(MusicPlayer addon) {
      return switch (this) {
        case CIDER_CLASSIC -> new CiderClassicService();
        case CIDER -> new JukeboxService(new CiderService());
        case JELLYFIN -> new JellyfinService(addon.configuration().jellyfin());
        case SPOTIFY -> new SpotifyService(addon.logger());
        case BEEFWEB -> new JukeboxService(new FoobarService());
        case TIDAL -> new JukeboxService(new TidalService());
      };
    }
  }

  public static void updateCurrentService(MusicPlayer addon) {
    if (currentService != null) {
      currentService.stop();
    }
    ServiceType currentServiceType = addon.configuration().serviceType().get();
    if (currentServiceType == null) {
      currentServiceType = ServiceType.CIDER_CLASSIC;
    }
    currentService = currentServiceType.getService(addon);
  }

  public static void connect() {
    if (currentService != null) {
      currentService.restart();
    }
  }

  @Nullable
  public static AbstractService getCurrentService() {
    return currentService;
  }
}
