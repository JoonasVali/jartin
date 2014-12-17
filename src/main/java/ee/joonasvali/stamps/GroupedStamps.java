package ee.joonasvali.stamps;

import ee.joonasvali.stamps.properties.MetadataReader;
import ee.joonasvali.stamps.query.Query;
import ee.joonasvali.stamps.query.RandomQuery;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Joonas Vali
 */
public class GroupedStamps {
  public static final String STAMPS_PROPERTIES = "stamps.properties";

  private File mainfolder;
  private ArrayList<Stamps> stampsGroups;

  public GroupedStamps(File mainfolder) {
    if (!mainfolder.exists() || !mainfolder.isDirectory()) throw new IllegalArgumentException("Folder " + mainfolder + " must be dir");
    this.mainfolder = mainfolder;
    loadStamps();
  }

  private void loadStamps() {
    MetadataReader reader = new MetadataReader();
    File[] files = mainfolder.listFiles((dir, name) -> dir.isDirectory());
    stampsGroups = new ArrayList<>(files.length);
    for (File file : files) {
      Stamps stamps = new Stamps(file);
      stampsGroups.add(stamps);

      File props = new File(file, STAMPS_PROPERTIES);
      if (props.isFile()) {
        try {
          StampGroupMetadata metadata = reader.loadMetadata(props);
          stamps.setMetadata(metadata);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

    }
  }

  /**
   * @param groups
   * @param stampsPerGroup
   * @param groupQuery
   * @param stampQuery
   * @param fillGroups
   * @return
   */
  public Stamps getStamps(int groups, int stampsPerGroup, Query<Stamps> groupQuery, Query<Stamp> stampQuery, boolean fillGroups) {
    boolean remove = true;
    if (groupQuery instanceof RandomQuery) {
      groupQuery = getRandomRemovingQuery();
      remove = false;
    }
    LinkedList<Stamps> copy = new LinkedList<>(stampsGroups);
    List<Stamps> picked = new ArrayList<>(groups);
    for (int i = 0; i < groups; i++) {
      if (copy.isEmpty()) {
        System.err.println("Ran out of stamp groups at index " + (i + 1) + ", before could pick " + groups);
        return flatten(picked, stampsPerGroup, stampQuery, fillGroups);
      }

      Stamps s = groupQuery.get(copy);
      if (remove) {
        copy.remove(s);
      }
      picked.add(s);
    }
    return flatten(picked, stampsPerGroup, stampQuery, fillGroups);
  }

  private Stamps flatten(List<Stamps> picked, int stampsPerGroup, Query<Stamp> stampQuery, boolean fillGroups) {
    List<Stamp> flat = new ArrayList<>(100);
    picked.stream().forEach(s -> flat.addAll(getStamps(s.getStamps(), stampsPerGroup, stampQuery, fillGroups)));
    return new Stamps(flat);
  }

  private Collection<? extends Stamp> getStamps(List<Stamp> stamps, int stampsPerGroup, Query<Stamp> stampQuery, boolean fillGroups) {
    if (!fillGroups) {
      if (stampsPerGroup >= stamps.size()) {
        return stamps;
      }
    }
    List<Stamp> result = new ArrayList<>(stampsPerGroup);
    for (int i = 0; i < stampsPerGroup; i++) {
      result.add(stampQuery.get(stamps));
    }
    return result;
  }


  private Query<Stamps> getRandomRemovingQuery() {
    return list -> {
      int i = (int) (Math.random() * list.size());
      return list.remove(i);
    };
  }

  public void clearCaches() {
    stampsGroups.forEach(s -> s.getStamps().forEach(Stamp::clearRenderCache));
  }
}
