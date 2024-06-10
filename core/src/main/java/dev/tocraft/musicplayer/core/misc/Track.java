package dev.tocraft.musicplayer.core.misc;

import net.labymod.api.client.gui.icon.Icon;
import java.util.List;

@SuppressWarnings("unused")
public interface Track {

  String name();

  int duration();

  float playTime();

  List<String> artists();

  Icon cover();
}
