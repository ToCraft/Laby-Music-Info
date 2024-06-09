package dev.tocraft.musicplayer.core.widgets;

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

  private ComponentWidget trackWidget;

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    // Text
    VerticalListWidget<ComponentWidget> text = new VerticalListWidget<>();
    text.addId("text");

    this.trackWidget = ComponentWidget.text("Hello World!");
    text.addChild(this.trackWidget);

    this.addFlexibleContent(text);
  }

  @Override
  public void update(String reason) {
    trackWidget.setComponent(Component.text("Hello there!"));
  }
}
