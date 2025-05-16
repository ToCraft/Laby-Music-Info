package dev.tocraft.musicinfo.core.services.impl.cider;

import dev.tocraft.musicinfo.core.MusicInfo;
import dev.tocraft.musicinfo.core.misc.Track;
import dev.tocraft.musicinfo.core.services.SocketIOService;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import net.labymod.api.client.gui.icon.Icon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

public class CiderShService extends SocketIOService {

  private static final URI API_URL;

  static {
    try {
      API_URL = new URI("ws://localhost:10767");
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public CiderShService() {
    super(API_URL);
  }

  private final CiderTrack currentTrack = new CiderTrack();

  public Track getTrackFromArgs(Object @NotNull ... args) {
    if (args.length > 0 && args[0] instanceof String strEvent) {

      if (strEvent.equals("API:Playback")) {
        if (args.length > 1 && args[1] instanceof JSONObject json) {
          try {
            JSONObject data = json.getJSONObject("data");

            switch (json.getString("type")) {
              case "playbackStatus.playbackTimeDidChange" -> {
                currentTrack.duration = data.getInt("currentPlaybackDuration");
                currentTrack.playTime = data.getInt("currentPlaybackTime");
              }
              case "playbackStatus.playbackStateDidChange" -> {
                JSONObject attr = data.getJSONObject("attributes");

                currentTrack.name = attr.getString("name");
                currentTrack.album = attr.getString("albumName");
                currentTrack.duration = attr.getInt("durationInMillis") / 1000; // milli to sec
                currentTrack.playTime = attr.getInt("currentPlaybackTime");
                currentTrack.artists = new ArrayList<>(List.of(attr.getString("artistName")));
              }
            }
          } catch (JSONException e) {
            if (MusicInfo.getInstance() != null) {
              MusicInfo.getInstance().logger()
                  .warn("Caught an error while handling Cider Data!", e);
            }
          }
        }
      }
    }
    return currentTrack;
  }

  static class CiderTrack implements Track {

    String name = "";
    String album = "";
    int duration = 0;
    int playTime = 0;
    List<String> artists = new ArrayList<>();

    @Override
    public String name() {
      return name;
    }

    @Override
    public String album() {
      return album;
    }

    @Override
    public int duration() {
      return duration;
    }

    @Override
    public int playTime() {
      return playTime;
    }

    @Override
    public List<String> artists() {
      return artists;
    }

    @Override
    public @Nullable Icon cover() {
      return null;
    }
  }
}