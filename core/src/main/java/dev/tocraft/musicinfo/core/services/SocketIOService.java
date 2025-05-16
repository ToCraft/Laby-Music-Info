package dev.tocraft.musicinfo.core.services;

import dev.tocraft.musicinfo.core.MusicInfo;
import dev.tocraft.musicinfo.core.events.SongUpdateEvent;
import dev.tocraft.musicinfo.core.misc.Track;
import io.socket.client.IO;
import io.socket.client.Socket;
import java.net.URI;
import org.jetbrains.annotations.NotNull;

public abstract class SocketIOService extends AbstractService {

  private final Socket client;

  public SocketIOService(@NotNull URI apiURI) {

    IO.Options options = new IO.Options();
    options.forceNew = true;
    options.reconnection = true;

    this.client = IO.socket(apiURI, options);

    this.client.onAnyIncoming(this::listen);
  }

  public SocketIOService(@NotNull URI apiURI, IO.Options options) {
    this.client = IO.socket(apiURI, options);

    this.client.onAnyIncoming(this::listen);
  }

  @Override
  public boolean isActive() {
    return client != null && (client.isActive() || client.connected());
  }

  @Override
  public void start() {
    client.connect();
  }

  @Override
  public void stop() {
    if (!client.connected()) {
      client.disconnect();
    }
  }

  public abstract Track getTrackFromArgs(Object... args);

  private void listen(Object... args) {
    if (MusicInfo.getLabyAPI() != null) {
      Track track = getTrackFromArgs(args);
      if (track != null) {
        MusicInfo.getLabyAPI().eventBus().fire(new SongUpdateEvent(track));
      }
    }
  }
}