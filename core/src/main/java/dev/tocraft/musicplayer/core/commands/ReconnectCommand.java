package dev.tocraft.musicplayer.core.commands;

import dev.tocraft.musicplayer.core.MusicPlayer;
import dev.tocraft.musicplayer.core.services.ServiceProvider;
import net.labymod.api.client.chat.command.Command;

public class ReconnectCommand extends Command {
  private final MusicPlayer addon;

  public ReconnectCommand(MusicPlayer addon) {
    super("musicplayer:reconnect", "reconnect");
    this.addon = addon;
  }

  @Override
  public boolean execute(String prefix, String[] arguments) {
    ServiceProvider.reload();
    this.displayMessage("Trying to reconnect now!");
    return true;
  }
}
