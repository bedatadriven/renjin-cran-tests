package org.renjin.cran;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.google.common.io.Resources;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;


public class CRAN {

  public static final String CRAN_MIRROR = "http://cran.xl-mirror.nl/";

  public static void fetchPackageIndex(File indexFile) throws IOException {
    URL indexUrl = new URL(CRAN_MIRROR + "src/contrib");
    Files.copy(Resources.newInputStreamSupplier(indexUrl), indexFile);
  }


  public static List<CranPackage> parsePackageList(InputSupplier<? extends InputStream> source) throws IOException {
    Document dom = fetchAsDom(source);

    List<CranPackage> packages = Lists.newArrayList();

    NodeList rows = dom.getElementsByTagName("a");
    for(int i=0;i!=rows.getLength();++i) {
      Element link = (Element)rows.item(i);
      String url = link.getAttribute("href");

      // http://cran.xl-mirror.nl/src/contrib/FactMixtAnalysis_1.0.tar.gz

      if(url.endsWith(".tar.gz")) {
        packages.add(new CranPackage(CRAN_MIRROR + "src/contrib/" + url));
      }
    }
    return packages;
  }

  private static Document fetchAsDom(InputSupplier<? extends InputStream> source) throws IOException {
    Tidy tidy = new Tidy();
    tidy.setXHTML(false);
    tidy.setQuiet(true);
    tidy.setShowWarnings(false);

    InputStream in = source.getInput();
    Document dom = tidy.parseDOM(in, null);
    in.close();
    return dom;
  }

  public static void downloadSrc(CranPackage pkg, File destFolder) throws IOException {

    File sourceZip = new File( destFolder, pkg.getFileName());
    if(sourceZip.exists()) {
      System.out.println(sourceZip + ": already exists.");

    } else {
      System.out.println(sourceZip + ": downloading...");
      URL url = pkg.getUrl();
      try {
        InputStream in = url.openStream();
        FileOutputStream out = new FileOutputStream(sourceZip);
        ByteStreams.copy(in, out);
        in.close();
        out.close();
      } catch(Exception e) {
        sourceZip.delete();
        e.printStackTrace();
      }
    }
  }

  public static File downloadSourceArchive(CranPackage pkg) throws IOException {

    File userHome = new File(System.getProperty("user.home"));
    File sourcesDir = new File(userHome, "cranSources");
    sourcesDir.mkdir();


    File archiveFile = new File(sourcesDir, pkg.getFileName());
    if(archiveFile.exists()) {
      return archiveFile;
    }

    downloadSrc(pkg, sourcesDir);

    return archiveFile;
  }

  public static void unpackSources(CranPackage pkg, File baseDir) throws IOException {
    FileInputStream in = new FileInputStream(downloadSourceArchive(pkg));
    GZIPInputStream gzipIn = new GZIPInputStream(in);
    TarArchiveInputStream tarIn = new TarArchiveInputStream(gzipIn);

    TarArchiveEntry entry;
    while((entry=tarIn.getNextTarEntry()) != null) {

      if(entry.getSize() != 0) {
        String name = entry.getName();
        int lastSlash = name.lastIndexOf('/');

        File targetDir;
        if(lastSlash > pkg.getName().length() + 1) {
          targetDir = new File(baseDir, name.substring(pkg.getName().length() + 1, lastSlash));
        } else {
          targetDir = baseDir;
        }
        targetDir.mkdirs();
        File targetFile = new File(targetDir, name.substring(lastSlash+1));
        FileOutputStream fos = new FileOutputStream(targetFile);
        ByteStreams.copy(tarIn, fos);
        fos.close();
      }
    }
  }

}