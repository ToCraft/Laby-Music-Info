package dev.tocraft.musicplayer.core.events;

import dev.tocraft.musicplayer.core.misc.Track;
import net.labymod.api.event.Event;
import org.jetbrains.annotations.NotNull;

public record SongUpdateEvent(@NotNull Track track, boolean isPlaying) implements Event {

}
