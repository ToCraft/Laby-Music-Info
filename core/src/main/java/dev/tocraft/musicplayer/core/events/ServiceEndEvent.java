package dev.tocraft.musicplayer.core.events;

import net.labymod.api.event.Event;

public record ServiceEndEvent(String error) implements Event {

}
