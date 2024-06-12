package dev.tocraft.musicplayer.core.listener;

import dev.tocraft.musicplayer.core.MusicPlayer;
import dev.tocraft.musicplayer.core.services.ServiceProvider;
import net.labymod.api.event.Phase;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.lifecycle.GameTickEvent;

public class GameTickHandler {

  private final MusicPlayer addon;

  public GameTickHandler(MusicPlayer addon) {
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
