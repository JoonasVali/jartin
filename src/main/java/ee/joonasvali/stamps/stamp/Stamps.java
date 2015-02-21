package ee.joonasvali.stamps.stamp;

import ee.joonasvali.stamps.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Joonas Vali
 */
public class Stamps implements StampProvider{
  public static final Logger log = LoggerFactory.getLogger(Stamps.class);
  private final File folder;
  private volatile ArrayList<Stamp> stamps;

  public Stamps(File folder) {
    if (!folder.exists() || !folder.isDirectory()) throw new IllegalArgumentException("Folder must be dir");
    this.folder = folder;
    loadStamps();
  }

  public Stamps(List<Stamp> stamps) {
    this.folder = null;
    this.stamps = new ArrayList<>(stamps);
  }

  private void loadStamps() {
    File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith("png") || name.toLowerCase().endsWith("jpg"));
    stamps = new ArrayList<>(files.length);
    for (File file : files) {
      try {
        load(file);
      } catch(IllegalArgumentException e) {
        log.error("Can't load Stamp from file " + file + " cause: '" + e.getMessage() + "'", e);
      }
    }
  }

  private void load(File file) throws IllegalArgumentException {
    stamps.add(Stamp.getInstance(file));
  }

  @Override
  public Stamp getStamp(Query<Stamp> q) {
    return q.get(stamps);
  }


  public List<Stamp> getStamps() {
    return new ArrayList<>(stamps);
  }

  public void setMetadata(StampGroupMetadata metadata) {
    stamps.forEach(s -> s.setMetadata(metadata));
  }

}
