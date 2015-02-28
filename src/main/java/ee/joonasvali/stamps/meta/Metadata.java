package ee.joonasvali.stamps.meta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @author Joonas Vali
 */
public class Metadata {
  private static final String VERSION_KEY = "project.version";
  private static final String NAME_KEY = "project.name";
  private static final String AUTHOR_KEY = "project.author";
  private static final String TIMESTAMP_KEY = "project.timestamp";
  private static final String TIMESTAMP_PATTERN_KEY = "project.timestamp.pattern";

  private static final String META_PROPERTIES = "/meta/meta.properties";
  public static final Metadata INSTANCE = new Metadata();
  public final Logger log = LoggerFactory.getLogger(Metadata.class);

  public final String VERSION;
  public final String NAME;
  public final String AUTHOR;
  public final Date BUILD_TIMESTAMP;


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
    String author = properties.getProperty(AUTHOR_KEY);
    String timeString = properties.getProperty(TIMESTAMP_KEY);
    String timePattern = properties.getProperty(TIMESTAMP_PATTERN_KEY);

    if(version == null || name == null) {
      log.error("name or version == null | name = " + name + ", version = " + version);
      System.exit(-1);
    }

    AUTHOR = author;
    VERSION = version;
    NAME = name;
    BUILD_TIMESTAMP = parseDate(timeString, timePattern);
  }

  private Date parseDate(String timeString, String timePattern) {
    DateFormat parser = new SimpleDateFormat(timePattern);
    Date date = null;
    try {
      date = parser.parse(timeString);
    } catch (ParseException e) {
      log.error("Parsing date failed.", e);
      log.error("Date: " + date);
      log.error("Pattern used: " + timePattern);
      System.exit(-1);
    }

    return date;
  }

  public String getVersion() {
    return VERSION;
  }

  public String getName() {
    return NAME;
  }

  public String getAuthor() {
    return AUTHOR;
  }

  public String getYear() {
    DateFormat yearFormatter = new SimpleDateFormat("yyyy");
    return yearFormatter.format(BUILD_TIMESTAMP);
  }
}
