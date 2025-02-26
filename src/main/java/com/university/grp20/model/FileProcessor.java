package com.university.grp20.model;

import java.io.File;

public class FileProcessor
{
  private File impressionLog ;
  private File clickLog ;
  private File serverLog ;



  public void setImpressionLog(File newImpressionLog) {
    this.impressionLog = newImpressionLog;
  }

  public void setClickLog(File newClickLog) {
    this.clickLog = newClickLog;
  }

  public void setServerLog(File newServerLog) {
    this.serverLog = newServerLog;
  }

  public boolean isReady() {
    if (impressionLog != null && clickLog != null && serverLog != null) {
      return (true);
    }
    else {
      return (false);
    }
  }
}
