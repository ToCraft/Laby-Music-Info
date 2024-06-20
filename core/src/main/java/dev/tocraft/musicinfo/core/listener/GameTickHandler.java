package dev.tocraft.musicinfo.core.listener;

import dev.tocraft.musicinfo.core.MusicInfo;
import dev.tocraft.musicinfo.core.services.ServiceProvider;
import net.labymod.api.event.Phase;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.lifecycle.GameTickEvent;

@SuppressWarnings("unused")
public class GameTickHandler {

  private final MusicInfo addon;

  public GameTickHandler(MusicInfo addon) {
    this.addon = addon;
  }

  @Subscribe
  public void onGameTick(GameTickEvent event) {
    if (event.phase() == Phase.PRE) {
      // handle autoReconnect feature
      if (addon.configuration().autoReconnect().get() && ServiceProvider.getCurrentService() != null
          && !ServiceProvider.getCurrentService().isActive()) {
        ServiceProvider.reload();
      }
    }
  }
}
