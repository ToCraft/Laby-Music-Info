package dev.tocraft.musicplayer.core.widgets;

import dev.tocraft.musicplayer.core.MusicPlayer;
import dev.tocraft.musicplayer.core.misc.Track;
import dev.tocraft.musicplayer.core.services.cider.CiderService;
import java.util.Random;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.hudwidget.HudWidget.Updatable;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;

@AutoWidget
@Link("player-widget.lss")
public class PlayerWidget extends FlexibleContentWidget implements Updatable {

  private final MusicPlayer addon;
  private final CiderService ciderService;

  public PlayerWidget(MusicPlayer addon) {
    this.addon = addon;
    this.ciderService = new CiderService();
    this.ciderService.connect();
  }

  private ComponentWidget trackWidget;

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    // Text
    VerticalListWidget<ComponentWidget> text = new VerticalListWidget<>();
    text.addId("text");

    this.trackWidget = ComponentWidget.text(trackString());
    text.addChild(this.trackWidget);

    this.addFlexibleContent(text);
  }

  @Override
  public void tick() {
    super.tick();

    this.trackWidget.setComponent(Component.text(trackString()));
  }

  private String trackString() {
    Track track = ciderService.getCurrentTrack(addon);
    if (track != null) {
      return track.name() + " by " + track.artist() + " in " + track.album() + ". ETA: " + (
          track.duration() - track.playTime());
    } else {
      return "Nothing playing yet";
    }
  }

  @Override
  public void update(String reason) {
    this.trackWidget.setComponent(Component.text(trackString()));
  }
}
