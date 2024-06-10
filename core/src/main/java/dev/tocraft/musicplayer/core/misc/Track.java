package dev.tocraft.musicplayer.core.misc;

import java.util.List;
import net.labymod.api.client.gui.icon.Icon;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public interface Track {

  String name();

  String album();

  int duration();

  int playTime();

  default int remainingTime() {
    if (duration() < 0 || playTime() < 0) {
      return -1;
    } else {
      return duration() - playTime();
    }
  }

  List<String> artists();

  @Nullable
  Icon cover();
}
