package dev.tocraft.musicplayer.core.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineStart;
import kotlinx.coroutines.GlobalScope;
import kotlinx.coroutines.future.FutureKt;
import net.labymod.api.util.logging.Logging;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractService {

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  public abstract boolean isActive();

  public abstract void start();

  public abstract void stop();

  public void restart() {
    stop();
    start();
  }

  @SuppressWarnings("unchecked")
  @NotNull
  public static <T> CompletableFuture<T> suspendToFuture(
      Function2<CoroutineScope, Continuation<T>, T> consumer) {
    return FutureKt.future(GlobalScope.INSTANCE, EmptyCoroutineContext.INSTANCE,
        CoroutineStart.DEFAULT,
        ((coroutineScope, continuation) -> consumer.invoke(coroutineScope,
            (Continuation<T>) continuation)));
  }

  @SuppressWarnings("unused")
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
    String s = getStringFromInputStream(connection.getInputStream());
    connection.disconnect();
    return s;
  }

  protected static String getStringFromInputStream(InputStream inputStream) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
    String inputLine;
    StringBuilder content = new StringBuilder();
    while ((inputLine = in.readLine()) != null) {
      content.append(inputLine);
    }
    in.close();
    return content.toString();
  }
}