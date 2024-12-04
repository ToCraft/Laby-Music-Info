package dev.tocraft.musicinfo.core.services.impl.jellyfin;

import dev.tocraft.musicinfo.core.MusicInfo;
import dev.tocraft.musicinfo.core.config.JellyfinSettings;
import dev.tocraft.musicinfo.core.events.ServiceEndEvent;
import dev.tocraft.musicinfo.core.events.SongUpdateEvent;
import dev.tocraft.musicinfo.core.misc.Track;
import dev.tocraft.musicinfo.core.services.AbstractService;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;
import java.util.Random;
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
import org.jellyfin.sdk.model.api.BaseItemDto;
import org.jellyfin.sdk.model.api.MediaType;
import org.jellyfin.sdk.model.api.PlayerStateInfo;
import org.jellyfin.sdk.model.api.SessionInfoDto;
import org.jellyfin.sdk.model.api.SessionsMessage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JellyfinService extends AbstractService {

  // just in case something goes wrong, we need a unique device id
  private static final String deviceId;

  static {
    StringBuilder tempDeviceId = new StringBuilder();
    try {
      InetAddress localhost = InetAddress.getLocalHost();
      NetworkInterface ni = NetworkInterface.getByInetAddress(localhost);
      byte[] hardwareAddress = ni.getHardwareAddress();
      String[] hexadecimalFormat = new String[hardwareAddress.length];
      for (int i = 0; i < hardwareAddress.length; i++) {
        hexadecimalFormat[i] = String.format("%02X", hardwareAddress[i]);
      }
      tempDeviceId.append(String.join("-", hexadecimalFormat));
    } catch (UnknownHostException | SocketException e) {
      StringBuilder tempFallbackId = new StringBuilder();
      for (int i = 0; i <= 5; i++) {
        tempFallbackId.append(new Random().nextInt());
      }
      tempDeviceId.append(tempFallbackId);
    }
    deviceId = tempDeviceId.toString();
  }

  @Contract(" -> new")
  private static @NotNull DeviceInfo getDeviceInfo() {
    String deviceName;
    try {
      deviceName = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      deviceName = "LabyMod-4";
    }

    MusicInfo addon = MusicInfo.getInstance();
    if (addon != null) {
      String accessToken = addon.configuration().jellyfin().accessToken().get();
      if (accessToken.length() >= 5) {
        String tokenStart = addon.configuration().jellyfin().accessToken().get().substring(0, 5);
        if (!tokenStart.isBlank()) {
          return new DeviceInfo(tokenStart, deviceName);
        }
      }

      String playerName = addon.labyAPI().getName();
      if (!playerName.isBlank()) {
        return new DeviceInfo(playerName, deviceName);
      }
    }

    return new DeviceInfo(deviceId, deviceName);
  }

  private static final Jellyfin JELLYFIN = JellyfinKt.createJellyfin(builder -> {
    builder.setClientInfo(new ClientInfo("Laby-Music-Info",
        MusicInfo.getInstance() != null ? MusicInfo.getInstance().addonInfo().getVersion() : "1.0"));
    builder.setDeviceInfo(getDeviceInfo());
    return Unit.INSTANCE;
  });

  @SuppressWarnings("unchecked")
  public static String getAccessToken(String url, String username, String password) {
    // setup APIs
    ApiClient api = JELLYFIN.createApi(url);
    UserApi userApi = api.getOrCreateApi(JvmClassMappingKt.getKotlinClass(UserApi.class),
        UserApi::new);

    // Authenticate
    return ((Response<AuthenticationResult>) suspendToFuture(
        (coroutineScope, continuation) -> userApi.authenticateUserByName(
            new AuthenticateUserByName(username, password), continuation)).join()).getContent()
        .getAccessToken();
  }

  private final ApiClient apiClient;
  private final JellyfinSettings settings;
  @Nullable
  private Thread watchSessions = null;

  public JellyfinService(JellyfinSettings settings) {
    this.settings = settings;
    // setup API
    this.apiClient = JELLYFIN.createApi(settings.serverURL().get(), settings.accessToken().get());
  }

  private void resetWatchSessions() {
    watchSessions = new Thread(() -> suspendToFuture(
        (coroutineScope, continuation) -> apiClient.getWebSocket()
            .subscribe(JvmClassMappingKt.getKotlinClass(
                SessionsMessage.class)).collect(new FlowCollector<>() {
              @Nullable
              @Override
              public Object emit(SessionsMessage message,
                  @NotNull Continuation<? super Unit> continuation) {
                if (MusicInfo.getLabyAPI() != null) {
                  List<SessionInfoDto> infoList = message.getData();
                  if (infoList != null) {
                    Track track = null;
                    for (SessionInfoDto sessionInfo : infoList) {
                      BaseItemDto nowPlaying = sessionInfo.getNowPlayingItem();
                      // check if the session is listing to audio
                      if (Objects.equals(sessionInfo.getUserName(), settings.username().get())
                          && nowPlaying != null
                          && nowPlaying.getMediaType() == MediaType.AUDIO) {
                        track = new Track() {
                          @Override
                          public String name() {
                            return nowPlaying.getName();
                          }

                          @Override
                          public String album() {
                            return nowPlaying.getAlbum();
                          }

                          @Override
                          public int duration() {
                            Long ticks = nowPlaying.getRunTimeTicks();
                            if (ticks != null) {
                              return (int) (ticks / 10000000);
                            } else {
                              return -1;
                            }
                          }

                          @Override
                          public int playTime() {
                            PlayerStateInfo playerStateInfo = sessionInfo.getPlayState();
                            if (playerStateInfo != null) {
                              Long playTime = playerStateInfo.getPositionTicks();
                              if (playTime != null) {
                                return (int) (playTime / 10000000);
                              }
                            }
                            return -1;
                          }

                          @Override
                          public List<String> artists() {
                            return nowPlaying.getArtists();
                          }

                          // TODO: Get Song Cover
                          @Override
                          public @Nullable Icon cover() {
                            return null;
                          }
                        };

                        if (settings.clientName().get().isBlank() || Objects.equals(
                            sessionInfo.getDeviceName(),
                            settings.clientName().get())) {
                          break;
                        }
                      }
                    }

                    if (track != null) {
                      MusicInfo.getLabyAPI().eventBus().fire(new SongUpdateEvent(track));
                    }
                  }
                }
                return null;
              }
            }, continuation)).join(), "watch jellyfin sessions");
    // send exception when thread crashes
    watchSessions.setUncaughtExceptionHandler((t, e) -> {
      if (MusicInfo.getLabyAPI() != null) {
        MusicInfo.getLabyAPI().eventBus()
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
