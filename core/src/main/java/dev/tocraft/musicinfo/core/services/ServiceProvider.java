package dev.tocraft.musicinfo.core.services;

import dev.tocraft.musicinfo.core.MusicInfo;
import dev.tocraft.musicinfo.core.services.impl.cider.CiderClassicService;
import dev.tocraft.musicinfo.core.services.impl.cider.CiderShService;
import dev.tocraft.musicinfo.core.services.impl.jellyfin.JellyfinService;
import dev.tocraft.musicinfo.core.services.impl.jukebox.JukeboxService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.thatgravyboat.jukebox.impl.foobar.FoobarService;
import tech.thatgravyboat.jukebox.impl.tidal.TidalService;

public class ServiceProvider {

  @Nullable
  private static AbstractService currentService = null;

  public enum ServiceType {
    CIDER_CLASSIC, CIDER, JELLYFIN, BEEFWEB, TIDAL;

    public @NotNull AbstractService getService(MusicInfo addon) {
      return switch (this) {
        case CIDER_CLASSIC -> new CiderClassicService();
        case CIDER -> new CiderShService();
        case JELLYFIN -> new JellyfinService(addon.configuration().jellyfin());
        case BEEFWEB -> new JukeboxService(new FoobarService());
        case TIDAL -> new JukeboxService(new TidalService());
      };
    }
  }

  public static void updateCurrentService(MusicInfo addon) {
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

  public static void reload() {
    MusicInfo addon = MusicInfo.getInstance();
    if (addon != null) {
      ServiceProvider.updateCurrentService(addon);
    }
    ServiceProvider.connect();
  }

  @Nullable
  public static AbstractService getCurrentService() {
    return currentService;
  }
}
