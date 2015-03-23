package ee.joonasvali.stamps.stamp;

import ee.joonasvali.stamps.properties.MetadataReader;
import ee.joonasvali.stamps.query.Query;
import ee.joonasvali.stamps.query.RandomQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Joonas Vali
 */
public class GroupedStamps {
  public static final Logger log = LoggerFactory.getLogger(GroupedStamps.class);
  public static final String STAMPS_PROPERTIES = "stamps.properties";
  private final ExecutorService bootExecutor = Executors.newSingleThreadExecutor();

  private final File mainfolder;
  private final ArrayList<Stamps> stampsGroups;
  private volatile boolean loaded = false;

  public GroupedStamps(File mainfolder) {
    if (!mainfolder.exists() || !mainfolder.isDirectory()) throw new IllegalArgumentException("Folder " + mainfolder + " must be dir");
    this.mainfolder = mainfolder;
    this.stampsGroups = new ArrayList<>();
  }

  public void loadStampsConcurrently() {
    bootExecutor.execute(() -> {
      loadStamps();
      bootExecutor.shutdown();
    });
  }

  private synchronized void loadStamps() {
    if (loaded) {
      return;
    }
    long begin = System.currentTimeMillis();
    log.info("Starting to load stamps.");
    MetadataReader reader = new MetadataReader();
    File[] files = mainfolder.listFiles((dir, name) -> dir.isDirectory());
    stampsGroups.ensureCapacity(files.length);
    for (File file : files) {
      Stamps stamps = new Stamps(file);
      stampsGroups.add(stamps);

      File props = new File(file, STAMPS_PROPERTIES);
      if (props.isFile()) {
        try {
          StampGroupMetadata metadata = reader.loadMetadata(props);
          stamps.setMetadata(metadata);
        } catch (IOException e) {
          log.error(e.getMessage(), e);
        }
      }
    }
    loaded = true;
    long time = System.currentTimeMillis() - begin;
    log.info("Stamps loaded in " + time + "ms.");
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
    init();
    boolean remove = true;
    if (groupQuery instanceof RandomQuery) {
      groupQuery = getRandomRemovingQuery();
      remove = false;
    }
    LinkedList<Stamps> copy = new LinkedList<>(stampsGroups);
    List<Stamps> picked = new ArrayList<>(groups);
    for (int i = 0; i < groups; i++) {
      if (copy.isEmpty()) {
        log.error("Ran out of stamp groups at index " + (i + 1) + ", before could pick " + groups);
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

  private void init() {
    if (!loaded) {
      loadStamps();
    }
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
    init();
    stampsGroups.forEach(s -> s.getStamps().forEach(Stamp::clearRenderCache));
  }
}
