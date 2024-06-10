package dev.tocraft.musicplayer.core.commands;

import dev.tocraft.musicplayer.core.MusicPlayer;
import dev.tocraft.musicplayer.core.MusicPlayerConfig;
import dev.tocraft.musicplayer.core.misc.ServiceProvider;
import net.labymod.api.client.chat.command.Command;

public class ReconnectCommand extends Command {
  private final MusicPlayer addon;

  public ReconnectCommand(MusicPlayer addon) {
    super("reconnect", "musicplayer:reconnect");
    this.addon = addon;
  }

  @Override
  public boolean execute(String prefix, String[] arguments) {
    ServiceProvider.updateCurrentService(addon);
    ServiceProvider.connect();
    this.displayMessage("Trying to reconnect now!");
    return true;
  }
}
