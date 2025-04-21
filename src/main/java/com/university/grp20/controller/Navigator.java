package com.university.grp20.controller;

import javafx.scene.layout.Pane;

public abstract class Navigator {
  protected Pane parentPane;

  public void init(Pane parentPane) {
    this.parentPane = parentPane;
  }
}
