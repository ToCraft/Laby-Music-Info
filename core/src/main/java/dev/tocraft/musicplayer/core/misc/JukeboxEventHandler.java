package dev.tocraft.musicplayer.core.misc;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public interface JukeboxEventHandler<T> extends Function1<T, Unit> {

  @Override
  default Unit invoke(T arg) {
    apply(arg);
    return Unit.INSTANCE;
  }

  void apply(T arg);
}
