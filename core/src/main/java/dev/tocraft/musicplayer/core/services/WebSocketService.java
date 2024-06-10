package dev.tocraft.musicplayer.core.services;

import com.google.gson.JsonElement;
import dev.tocraft.musicplayer.core.events.SongUpdateEvent;
import dev.tocraft.musicplayer.core.misc.Track;
import java.net.URI;
import net.labymod.api.LabyAPI;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;

public abstract class WebSocketService extends AbstractService {

  @NotNull
  private final URI apiURI;
  @NotNull
  private final LabyAPI labyAPI;
  private WebSocketClient client;

  public WebSocketService(@NotNull LabyAPI labyAPI, @NotNull URI apiURI) {
    this.labyAPI = labyAPI;
    this.apiURI = apiURI;
    this.client = setupClient(apiURI);
  }


  private WebSocketClient setupClient(URI apiURI) {
    return new WebSocketClient(apiURI) {
      @Override
      public void onOpen(ServerHandshake handshakeData) {
      }

      @Override
      public void onMessage(String message) {
        labyAPI.eventBus().fire(new SongUpdateEvent(getTrackFromJson(fromJson(message))));
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
    client = setupClient(apiURI);
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