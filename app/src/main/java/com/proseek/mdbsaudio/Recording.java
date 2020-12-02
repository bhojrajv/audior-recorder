package com.proseek.mdbsaudio;

import java.io.File;

public  class Recording {
    String Uri, fileName,elapsetime,remaintime,dateTime;
    File[]files;
    boolean isPlaying = false;
    public Recording() {
    }
    public Recording(String uri, String fileName, boolean isPlaying,String elapsetime,String remaintime
    ) {
        this. Uri = uri;
        this.fileName = fileName;
        this.isPlaying = isPlaying;
        this.elapsetime=elapsetime;
        this.remaintime=remaintime;
        this.dateTime=dateTime;
        this.files=files;
    }
    public String getUri() {
        return Uri;
    }

    public String getFileName() {
        return fileName;
    }
    public  void setElapsetime(String elapse){
        this.remaintime=elapse;
    }
    public  String getElapsetime()
    {
        return remaintime;
    }
    public  void setRemaintime(String remain)
    {
        this.remaintime=remain;
    }
    public  String getRemaintime()
    {
        return  remaintime;
    }
    public boolean isPlaying() {
        return isPlaying;
    }
    public  String getDateTime()
    {
        return  dateTime;
    }
    public void setDateTime(String dateTime)
    {
        this.dateTime=dateTime;
    }
public File[] setFiles()
{
    return files;
}
public  void  getFile(File[] files)
{
    this.files=files;
}

    public void setPlaying(boolean playing){
        this.isPlaying = playing;
    }
}
