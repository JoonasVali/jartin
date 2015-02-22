package ee.joonasvali.stamps.meta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Joonas Vali
 */
public class Metadata {
  private static final String VERSION_KEY = "project.version";
  private static final String NAME_KEY = "project.name";
  private static final String META_PROPERTIES = "/meta/meta.properties";
  public static final Metadata INSTANCE = new Metadata();
  public final Logger log = LoggerFactory.getLogger(Metadata.class);

  public final String VERSION;
  public final String NAME;

  public Metadata() {
    File file = new File(ClassLoader.class.getResource(META_PROPERTIES).getFile());
    Properties properties = new Properties();
    try (InputStream inputStream = new FileInputStream(file)) {
      properties.load(inputStream);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      System.exit(-1);
    }

    String version = properties.getProperty(VERSION_KEY);
    String name = properties.getProperty(NAME_KEY);

    if(version == null || name == null) {
      log.error("name or version == null | name = " + name + ", version = " + version);
      System.exit(-1);
    }

    VERSION = version;
    NAME = name;
  }

  public String getVersion() {
    return VERSION;
  }

  public String getName() {
    return NAME;
  }
}
