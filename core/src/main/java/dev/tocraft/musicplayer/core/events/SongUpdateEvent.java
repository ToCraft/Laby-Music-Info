package dev.tocraft.musicplayer.core.events;

import dev.tocraft.musicplayer.core.misc.Track;
import net.labymod.api.event.Event;

public record SongUpdateEvent(Track track, boolean isPlaying) implements Event {

}
