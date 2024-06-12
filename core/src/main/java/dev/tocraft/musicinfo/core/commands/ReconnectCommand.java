package dev.tocraft.musicinfo.core.commands;

import dev.tocraft.musicinfo.core.MusicInfo;
import dev.tocraft.musicinfo.core.services.ServiceProvider;
import net.labymod.api.client.chat.command.Command;

public class ReconnectCommand extends Command {
  public ReconnectCommand() {
    super("musicinfo:reconnect", "reconnect");
  }

  @Override
  public boolean execute(String prefix, String[] arguments) {
    ServiceProvider.reload();
    this.displayMessage("Trying to reconnect now!");
    return true;
  }
}
