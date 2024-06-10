package dev.tocraft.musicplayer.core.commands;

import dev.tocraft.musicplayer.core.MusicPlayerConfig;
import dev.tocraft.musicplayer.core.misc.ServiceProvider;
import net.labymod.api.client.chat.command.Command;

public class ReconnectCommand extends Command {
  private final MusicPlayerConfig config;

  public ReconnectCommand(MusicPlayerConfig config) {
    super("reconnect", "musicplayer:reconnect");
    this.config = config;
  }

  @Override
  public boolean execute(String prefix, String[] arguments) {
    ServiceProvider.updateCurrentService(config);
    ServiceProvider.connect();
    this.displayMessage("Trying to reconnect now!");
    return true;
  }
}
