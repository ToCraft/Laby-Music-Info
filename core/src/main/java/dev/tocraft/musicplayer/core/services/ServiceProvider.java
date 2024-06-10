package dev.tocraft.musicplayer.core.services;

import dev.tocraft.musicplayer.core.MusicPlayer;
import dev.tocraft.musicplayer.core.services.impl.cider.CiderClassicService;
import dev.tocraft.musicplayer.core.services.impl.jukebox.JukeboxService;
import dev.tocraft.musicplayer.core.services.impl.spotify.SpotifyService;
import net.labymod.api.LabyAPI;
import org.jetbrains.annotations.Nullable;
import tech.thatgravyboat.jukebox.impl.cider.CiderService;
import tech.thatgravyboat.jukebox.impl.foobar.FoobarService;
import tech.thatgravyboat.jukebox.impl.tidal.TidalService;

public class ServiceProvider {

  @Nullable
  private static AbstractService currentService = null;

  public enum ServiceType {
    CIDER_CLASSIC, CIDER, /*YOUTUBE, YOUTUBE2, */SPOTIFY, BEEFWEB, TIDAL;

    public AbstractService getService(MusicPlayer addon) {
      labyAPI = addon.labyAPI();

      return switch (this) {
        case CIDER_CLASSIC -> new CiderClassicService(labyAPI);
        case CIDER -> new JukeboxService(labyAPI, new CiderService());
        /*case YOUTUBE -> new YoutubeService(addon.configuration().youtubePassword().get());
        case YOUTUBE2 -> new YoutubeServiceV2(addon.configuration().youtubeToken().get());*/
        case SPOTIFY -> new SpotifyService(addon.labyAPI(), addon.logger());
        case BEEFWEB -> new JukeboxService(labyAPI, new FoobarService());
        case TIDAL -> new JukeboxService(labyAPI, new TidalService());
      };
    }
  }

  @Nullable
  private static LabyAPI labyAPI = null;

  public static void initialize(LabyAPI labyAPI) {
    ServiceProvider.labyAPI = labyAPI;
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
