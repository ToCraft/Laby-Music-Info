package dev.tocraft.musicplayer.core.services.cider;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.tocraft.musicplayer.core.MusicPlayer;
import dev.tocraft.musicplayer.core.misc.Track;
import dev.tocraft.musicplayer.core.services.AbstractService;
import java.net.URI;
import java.net.URISyntaxException;
import net.labymod.api.client.gui.icon.Icon;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.Nullable;

public class CiderService extends AbstractService {

  public static final URI API_URL;

  static {
    try {
      API_URL = new URI("ws://localhost:26369");
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  private final WebSocketClient client = new WebSocketClient(API_URL) {
    @Override
    public void onOpen(ServerHandshake handshakeData) {
    }

    @Override
    public void onMessage(String message) {
      currentTrack = getTrackFromJson(fromJson(message));
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

  public boolean isConnected() {
    return client.isOpen() && !client.isClosing();
  }

  public void connect() {
    client.connect();
  }

  public void disconnect() {
    if (!client.isClosed() && !client.isClosing()) {
      client.close();
    }
  }

  private @Nullable Track currentTrack = null;

  @Override
  public Track getCurrentTrack(MusicPlayer addon) {
    return currentTrack;
  }

  private static Track getTrackFromJson(JsonElement json) {
    JsonObject data = json.getAsJsonObject().get("data").getAsJsonObject();
    return new Track() {
      @Override
      public String name() {
        return data.get("name").getAsString();
      }

      @Override
      public int duration() {
        return data.get("durationInMillis").getAsInt();
      }

      @Override
      public float playTime() {
        return data.get("currentPlaybackTime").getAsFloat();
      }

      @Override
      public String artist() {
        return data.get("artistName").getAsString();
      }

      @Override
      public String album() {
        return data.get("albumName").getAsString();
      }

      @Override
      public Icon cover() {
        return Icon.url(
            data.get("artwork").getAsJsonObject().get("url").getAsString().replace("{w}", "32")
                .replace("{h}", "32"));
      }
    };
  }
}
