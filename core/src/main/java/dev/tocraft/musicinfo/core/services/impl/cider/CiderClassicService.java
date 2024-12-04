package dev.tocraft.musicinfo.core.services.impl.cider;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.tocraft.musicinfo.core.misc.Track;
import dev.tocraft.musicinfo.core.services.WebSocketService;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import net.labymod.api.client.gui.icon.Icon;
import org.jetbrains.annotations.NotNull;

public class CiderClassicService extends WebSocketService {

  private static final URI API_URL;

  static {
    try {
      API_URL = new URI("ws://localhost:26369");
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public CiderClassicService() {
    super(API_URL, new HashMap<>());
  }

  public Track getTrackFromJson(@NotNull JsonElement json) {
    JsonObject data = json.getAsJsonObject().get("data").getAsJsonObject();
    return new Track() {
      @Override
      public String name() {
        JsonElement j1 = data.get("name");
        if (j1 != null && j1.isJsonPrimitive()) {
          JsonPrimitive p1 = j1.getAsJsonPrimitive();
          if (p1.isString()) {
            return p1.getAsString();
          }
        }
        return "";
      }

      @Override
      public String album() {
        JsonElement j1 = data.get("albumName");
        if (j1 != null && j1.isJsonPrimitive()) {
          return j1.getAsJsonPrimitive().getAsString();
        }
        return "";
      }

      @Override
      public int duration() {
        JsonElement j1 = data.get("durationInMillis");
        if (j1 != null && j1.isJsonPrimitive()) {
          return j1.getAsInt() / 1000;
        }
        return -1;
      }

      @Override
      public int playTime() {
        JsonElement j1 = data.get("currentPlaybackTime");
        if (j1 != null && j1.isJsonPrimitive()) {
          return j1.getAsInt();
        }
        return -1;
      }

      @Override
      public List<String> artists() {
        JsonElement j1 = data.get("artistName");
        if (j1 != null && j1.isJsonPrimitive()) {
          return List.of(j1.getAsString());
        }
        return List.of();
      }

      @Override
      public Icon cover() {
        JsonElement j1 = data.get("artwork");
        if (j1 != null && j1.isJsonObject()) {
          JsonElement j2 = j1.getAsJsonObject().get("url");
          if (j2 != null && j2.isJsonPrimitive()) {
            return Icon.url(j2.getAsString().replace("{w}", "32")
                .replace("{h}", "32"));
          }
        }
        return null;
      }
    };
  }
}