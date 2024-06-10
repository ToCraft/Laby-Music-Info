package dev.tocraft.musicplayer.core.services.impl.jukebox;

import dev.tocraft.musicplayer.core.events.ServiceEndEvent;
import dev.tocraft.musicplayer.core.events.SongUpdateEvent;
import dev.tocraft.musicplayer.core.misc.Track;
import dev.tocraft.musicplayer.core.services.AbstractService;
import java.util.List;
import kotlin.Unit;
import net.labymod.api.LabyAPI;
import net.labymod.api.client.gui.icon.Icon;
import tech.thatgravyboat.jukebox.api.events.EventType;
import tech.thatgravyboat.jukebox.api.service.BaseService;
import tech.thatgravyboat.jukebox.api.service.ServicePhase;

public class JukeboxService extends AbstractService {

  private final BaseService delegate;

  public JukeboxService(LabyAPI labyAPI, BaseService delegate) {
    this.delegate = delegate;
    this.delegate.registerListener(EventType.Companion.getUPDATE(), event -> {
      if (labyAPI != null) {
        labyAPI.eventBus().fire(new SongUpdateEvent(new Track() {
          @Override
          public String name() {
            return event.getState().getSong().getTitle();
          }

          @Override
          public String album() {
            return "";
          }

          @Override
          public int duration() {
            return event.getState().getSongState().getDuration();
          }

          @Override
          public int playTime() {
            return event.getState().getSongState().getProgress();
          }

          @Override
          public List<String> artists() {
            return event.getState().getSong().getArtists();
          }

          @Override
          public Icon cover() {
            return Icon.url(event.getState().getSong().getCover());
          }
        }));
      }
      return Unit.INSTANCE;
    });
    delegate.registerListener(EventType.Companion.getSERVICE_ENDED(), event -> {
      if (labyAPI != null) {
        restart();
        labyAPI.eventBus().fire(new ServiceEndEvent(""));
      }
      return Unit.INSTANCE;
    });
    delegate.registerListener(EventType.Companion.getSERVICE_ERROR(), event -> {
      if (labyAPI != null) {
        restart();
        labyAPI.eventBus().fire(new ServiceEndEvent(event.getError()));
      }
      return Unit.INSTANCE;
    });
    delegate.registerListener(EventType.Companion.getSONG_CHANGE(), event -> {
      if (labyAPI != null) {
        labyAPI.eventBus().fire(new SongUpdateEvent(new Track() {
          @Override
          public String name() {
            return event.getState().getSong().getTitle();
          }

          @Override
          public String album() {
            return "";
          }

          @Override
          public int duration() {
            return event.getState().getSongState().getDuration();
          }

          @Override
          public int playTime() {
            return event.getState().getSongState().getProgress();
          }

          @Override
          public List<String> artists() {
            return event.getState().getSong().getArtists();
          }

          @Override
          public Icon cover() {
            return Icon.url(event.getState().getSong().getCover());
          }
        }));
      }
      return Unit.INSTANCE;
    });
    delegate.registerListener(EventType.Companion.getSERVICE_UNAUTHORIZED(), event -> {
      if (labyAPI != null) {
        restart();
        labyAPI.eventBus().fire(new ServiceEndEvent("unauthorized"));
      }
      return Unit.INSTANCE;
    });
  }

  @Override
  public boolean isActive() {
    return delegate.getPhase() == ServicePhase.RUNNING;
  }

  @Override
  public void start() {
    delegate.start();
  }

  @Override
  public void stop() {
    delegate.stop();
  }

  @Override
  public void restart() {
    delegate.restart();
  }
}
