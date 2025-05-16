package dev.tocraft.musicinfo.core.services;

import com.google.gson.JsonElement;
import dev.tocraft.musicinfo.core.MusicInfo;
import dev.tocraft.musicinfo.core.events.SongUpdateEvent;
import dev.tocraft.musicinfo.core.misc.Track;
import java.net.URI;
import java.util.Map;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.Contract;
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


  @Contract("_, _ -> new")
  private @NotNull WebSocketClient setupClient(URI apiURI, Map<String, String> httpHeaders) {
    return new WebSocketClient(apiURI, httpHeaders) {
      @Override
      public void onOpen(ServerHandshake handshakeData) {
      }

      @Override
      public void onMessage(String message) {
        if (MusicInfo.getLabyAPI() != null) {
          Track track = getTrackFromJson(fromJson(message));
          if (track != null) {
            MusicInfo.getLabyAPI().eventBus().fire(new SongUpdateEvent(track));
          }
        }
      }

      @Override
      public void onClose(int code, String reason, boolean remote) {
        if (MusicInfo.getInstance() != null) {
          MusicInfo.getInstance().logger().debug("Websocket closed.");
        }
      }

      @Override
      public void onError(Exception e) {
        if (MusicInfo.getInstance() != null) {
          MusicInfo.getInstance().logger().error("Error on WebSocket!", e.getMessage());
        } else {
          //noinspection CallToPrintStackTrace
          e.printStackTrace();
        }
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