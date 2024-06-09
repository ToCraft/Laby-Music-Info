package dev.tocraft.musicplayer.core.misc;

import net.labymod.api.client.gui.icon.Icon;

public interface Track {

  String name();

  int duration();

  float playTime();

  String artist();

  String album();

  Icon cover();
}
