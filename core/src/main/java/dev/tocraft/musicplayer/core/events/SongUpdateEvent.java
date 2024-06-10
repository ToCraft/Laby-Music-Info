package dev.tocraft.musicplayer.core.events;

import dev.tocraft.musicplayer.core.misc.Track;
import net.labymod.api.event.Event;
import org.jetbrains.annotations.Nullable;

public record SongUpdateEvent(@Nullable Track track, boolean isPlaying) implements Event {

}
