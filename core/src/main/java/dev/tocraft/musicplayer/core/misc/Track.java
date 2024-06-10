package dev.tocraft.musicplayer.core.misc;

import java.util.List;
import net.labymod.api.client.gui.icon.Icon;

@SuppressWarnings("unused")
public interface Track {

  String name();

  int duration();

  int playTime();

  default int remainingTime() {
    return duration() - playTime();
  }

  List<String> artists();

  Icon cover();
}
