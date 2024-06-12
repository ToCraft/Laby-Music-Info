package dev.tocraft.musicinfo.core.events;

import dev.tocraft.musicinfo.core.misc.Track;
import net.labymod.api.event.Event;
import org.jetbrains.annotations.NotNull;

public record SongUpdateEvent(@NotNull Track track) implements Event {

}
