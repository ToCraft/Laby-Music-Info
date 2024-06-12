package dev.tocraft.musicplayer.core.services;

import com.google.gson.JsonElement;
import dev.tocraft.musicplayer.core.MusicPlayer;
import dev.tocraft.musicplayer.core.events.SongUpdateEvent;
import dev.tocraft.musicplayer.core.misc.Track;
import java.net.URI;
import java.util.Map;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;

public abstract class WebSocketService extends AbstractService {

  @NotNull
  private final URI apiURI;
  private WebSocketClient client;
  private final Map<String, String> httpHeaders;

  public WebSocketService(@NotNull URI apiURI,
      Map<String, String> httpHeaders) {
    this.apiURI = apiURI;
    this.client = setupClient(apiURI, httpHeaders);
    this.httpHeaders = httpHeaders;
  }


  private WebSocketClient setupClient(URI apiURI, Map<String, String> httpHeaders) {
    return new WebSocketClient(apiURI, httpHeaders) {
      @Override
      public void onOpen(ServerHandshake handshakeData) {
      }

      @Override
      public void onMessage(String message) {
        if (MusicPlayer.getLabyAPI() != null) {
          MusicPlayer.getLabyAPI().eventBus()
              .fire(new SongUpdateEvent(getTrackFromJson(fromJson(message))));
        }
      }

      @Override
      public void onClose(int code, String reason, boolean remote) {
        System.out.println("Websocket closed.");
      }

      @Override
      public void onError(Exception e) {
        System.out.println("Got an error: " + e.getMessage());
      }
    };
  }

  @Override
  public boolean isActive() {
    return client != null && client.isOpen() && !client.isClosing();
  }

  @Override
  public void start() {
    client = setupClient(apiURI, httpHeaders);
    client.connect();
  }

  @Override
  public void stop() {
    if (!client.isClosed() && !client.isClosing()) {
      client.close();
    }
  }

  public abstract Track getTrackFromJson(JsonElement json);
}