package ee.joonasvali.stamps;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Joonas Vali
 */
public class Stamps {
  private File folder;
  private ArrayList<Stamp> stamps;

  public Stamps(File folder) {
    if (!folder.exists() || !folder.isDirectory()) throw new IllegalArgumentException("Folder must be dir");
    this.folder = folder;
    loadStamps();
  }

  private void loadStamps() {
    File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith("png") || name.toLowerCase().endsWith("jpg"));
    stamps = new ArrayList<>(files.length);
    for (File file : files) {
      load(file);
    }
  }

  private void load(File file) {
    stamps.add(new Stamp(file));
  }

  public Stamp getStamp(Query<Stamp> q) {
    return q.get(stamps);
  }


}
