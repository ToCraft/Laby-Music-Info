package dev.tocraft.musicplayer.core.services.impl.jellyfin;

import dev.tocraft.musicplayer.core.MusicPlayer;
import dev.tocraft.musicplayer.core.config.JellyfinSettings;
import dev.tocraft.musicplayer.core.events.ServiceEndEvent;
import dev.tocraft.musicplayer.core.events.SongUpdateEvent;
import dev.tocraft.musicplayer.core.misc.Track;
import dev.tocraft.musicplayer.core.services.AbstractService;
import java.util.Calendar;
import java.util.List;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.JvmClassMappingKt;
import kotlinx.coroutines.flow.FlowCollector;
import net.labymod.api.client.gui.icon.Icon;
import org.jellyfin.sdk.Jellyfin;
import org.jellyfin.sdk.JellyfinKt;
import org.jellyfin.sdk.api.client.ApiClient;
import org.jellyfin.sdk.api.client.Response;
import org.jellyfin.sdk.api.operations.UserApi;
import org.jellyfin.sdk.model.ClientInfo;
import org.jellyfin.sdk.model.DeviceInfo;
import org.jellyfin.sdk.model.api.AuthenticateUserByName;
import org.jellyfin.sdk.model.api.AuthenticationResult;
import org.jellyfin.sdk.model.api.SessionsMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO: Fix crash with wrong server url
public class JellyfinService extends AbstractService {

  // TODO: Replace with unique id
  private static final DeviceInfo deviceInfo = new DeviceInfo("LABY", "user");

  private static final Jellyfin jellyfin = JellyfinKt.createJellyfin(builder -> {
    builder.setClientInfo(new ClientInfo("Laby-Music-Player", "1.0"));
    builder.setDeviceInfo(deviceInfo);
    return Unit.INSTANCE;
  });

  @SuppressWarnings("unchecked")
  public static String getAccessToken(String url, String username, String password) {
    // setup APIs
    ApiClient api = jellyfin.createApi(url);
    UserApi userApi = api.getOrCreateApi(JvmClassMappingKt.getKotlinClass(UserApi.class),
        UserApi::new);

    // Authenticate
    // TODO: allow direct access token to be specified
    return ((Response<AuthenticationResult>) suspendToFuture(
        (coroutineScope, continuation) -> userApi.authenticateUserByName(
            new AuthenticateUserByName(username, password), continuation)).join()).getContent()
        .getAccessToken();
  }

  private final ApiClient apiClient;
  @Nullable
  private Thread watchSessions = null;

  public JellyfinService(JellyfinSettings settings) {
    // setup API
    this.apiClient = jellyfin.createApi(settings.serverURL().get(), settings.accessToken().get());
  }

  private void resetWatchSessions() {
    watchSessions = new Thread(() -> suspendToFuture(
        (coroutineScope, continuation) -> apiClient.getWebSocket()
            .subscribe(JvmClassMappingKt.getKotlinClass(
                SessionsMessage.class)).collect(new FlowCollector<>() {
              @Nullable
              @Override
              public Object emit(SessionsMessage sessionsMessage,
                  @NotNull Continuation<? super Unit> continuation) {
                // TODO: Find currently playing song and send it to event
                if (MusicPlayer.getLabyAPI() != null) {
                  MusicPlayer.getLabyAPI().eventBus().fire(new SongUpdateEvent(new Track() {
                    @Override
                    public String name() {
                      return "";
                    }

                    @Override
                    public String album() {
                      return "";
                    }

                    @Override
                    public int duration() {
                      Calendar calendar = Calendar.getInstance();
                      return calendar.get(Calendar.SECOND) * 60 + calendar.get(Calendar.SECOND);
                    }

                    @Override
                    public int playTime() {
                      return -1;
                    }

                    @Override
                    public List<String> artists() {
                      return List.of();
                    }

                    @Override
                    public @Nullable Icon cover() {
                      return null;
                    }
                  }));
                }
                return null;
              }
            }, continuation)).join(), "watch jellyfin sessions");
    // send exception when thread crashes
    watchSessions.setUncaughtExceptionHandler((t, e) -> {
      if (MusicPlayer.getLabyAPI() != null) {
        MusicPlayer.getLabyAPI().eventBus()
            .fire(new ServiceEndEvent(e.getLocalizedMessage()));
      }
    });
  }

  @Override
  public boolean isActive() {
    return watchSessions != null && watchSessions.isAlive();
  }

  @Override
  public void start() {
    resetWatchSessions();
    if (watchSessions != null) {
      watchSessions.start();
    }
  }

  @Override
  public void stop() {
    if (watchSessions != null) {
      watchSessions.interrupt();
    }
  }
}
