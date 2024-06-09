package dev.tocraft.musicplayer.core.listener;

import dev.tocraft.musicplayer.core.MusicPlayer;
import net.labymod.api.event.Phase;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.lifecycle.GameTickEvent;

public class MusicPlayerEventListener {

  private final MusicPlayer addon;

  public MusicPlayerEventListener(MusicPlayer addon) {
    this.addon = addon;
  }

  @Subscribe
  public void onGameTick(GameTickEvent event) {
    if (event.phase() == Phase.PRE) {
      /*if (addon.configuration().supportCiderSh().get() && !CiderService.isConnected()) {
        CiderService.connect();
      } else if (CiderService.isConnected()) {
        CiderService.disconnect();
      }*/
    }
  }
}
