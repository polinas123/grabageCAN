package com.sillyv.garbagecan;

import java.io.File;

/**
 * a class used to raise and listen/catch saving files events using EventBus
 *
 * <p>Created by Itai on 01/12/2016.
 */
public class SavedSourceFileMessage {

  private final String filePath;

  private final File file;




  public SavedSourceFileMessage(File file) {
    this.file = file;
    this.filePath = file.getPath();
  }


  public String getFilePath() {
    return filePath;
  }

  public File getFile() {
    return file;
  }

}
