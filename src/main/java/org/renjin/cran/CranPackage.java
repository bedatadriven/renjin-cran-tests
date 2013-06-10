package org.renjin.cran;


import java.net.MalformedURLException;
import java.net.URL;

public class CranPackage {

  private String url;
  private String name;
  private String fileName;
  private String archiveName;
  private String version;

  public CranPackage(String url) {
    this.url = url;
    this.fileName = url.substring(url.lastIndexOf('/')+1);
    this.archiveName = fileName.substring(0, fileName.length() - ".tar.gz".length());

    String nameVersion[] = this.archiveName.split("_");
    this.name = nameVersion[0];
    this.version = nameVersion[1];
  }

  public CranPackage(String name, String fileName, String archiveName, String version) {
    this.url = CRAN.CRAN_MIRROR;
    this.name =  name;
    this.fileName = fileName;
    this.archiveName = archiveName;
    this.version = version;
  }

  public String getName() {
    return name;
  }

  public String getFileName() {
    return fileName;
  }

  public String getArchiveName() {
    return archiveName;
  }

  public String getVersion() {
    return version;
  }

  @Override
	public String toString() {
		return getName();
	}

  public URL getUrl() throws MalformedURLException {
    return new URL(url);
  }
}