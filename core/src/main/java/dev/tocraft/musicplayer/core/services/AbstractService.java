package dev.tocraft.musicplayer.core.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import dev.tocraft.musicplayer.core.MusicPlayer;
import dev.tocraft.musicplayer.core.misc.Track;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import net.labymod.api.util.logging.Logging;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractService {

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  @Nullable
  public abstract Track getCurrentTrack(MusicPlayer addon);

  public static JsonElement getJsonResponseFromUrl(URL url, Logging logger) {
    try {
      Map<String, String> header = new HashMap<>();
      String json = getResponse(header, url);
      return fromJson(json);
    } catch (Exception e) {
      logger.error("Couldn't parse JSON from {}, Caught: {}", url, e);
    }

    return JsonNull.INSTANCE;
  }

  public static JsonElement fromJson(String json) throws JsonParseException {
    try {
      JsonReader jsonReader = new JsonReader(new StringReader(json));
      jsonReader.setLenient(false);
      return GSON.getAdapter(JsonElement.class).read(jsonReader);
    } catch (IOException e) {
      throw new JsonParseException(e);
    }
  }

  @Internal
  @NotNull
  private static String getResponse(Map<String, String> header, URL url) throws IOException {
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    for (Map.Entry<String, String> entry : header.entrySet()) {
      connection.addRequestProperty(entry.getKey(), entry.getValue());
    }
    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    String inputLine;
    StringBuilder content = new StringBuilder();
    while ((inputLine = in.readLine()) != null) {
      content.append(inputLine);
    }
    connection.disconnect();
    in.close();
    return content.toString();
  }
}
