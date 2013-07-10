package org.renjin.cran;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import com.google.common.io.Closeables;

public class OutputCollector extends Thread {
  private InputStream is;
  private File logFile;
  
  
  public OutputCollector(InputStream is, File logFile) {
    this.is = is;
    this.logFile = logFile;
  }

  public void run() {
    
    // make sure the log file has a dir
    logFile.getParentFile().mkdirs();
    
    PrintStream out;
    try {
      out = new PrintStream(logFile);
    } catch (FileNotFoundException e) {
      System.err.println("EEK: can't write to log file: " + logFile.getAbsolutePath());
      return;
    }
    try {
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);
      String line;
      while ( (line = br.readLine()) != null)
        out.println(line); 
    } catch (IOException ioe) {
      ioe.printStackTrace(out);
    } finally {
      Closeables.closeQuietly(out);
   }
  }
  
}
