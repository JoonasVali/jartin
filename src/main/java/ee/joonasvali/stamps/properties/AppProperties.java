package ee.joonasvali.stamps.properties;

import ee.joonasvali.stamps.ui.Main;

import java.io.File;
import java.util.function.Consumer;

/**
 * @author Joonas Vali
 */
public class AppProperties {
  private String outputPath;
  private String stampsDirPath;
  private boolean lazyLoading;
  private File stampsDir;

  private static AppProperties properties = new AppProperties();

  public static AppProperties getInstance() {
    return properties;
  }

  private AppProperties() {
    loadString("jartin.stamps", this::setStampsDirPath);
    loadString("jartin.output", this::setOutput);
    loadBoolean("jartin.stamps.lazyloading", this::setLazyLoading);


    if (outputPath == null) {
      outputPath = System.getProperty("user.home") + File.separator + "jartin" + File.separator + "out";
    }

    File file = new File(outputPath);
    if (file.isFile()) {
      System.err.println("Output path '" + file + "' must be a folder!");
      System.exit(-1);
    }

    if (!file.exists()) {
      file.mkdirs();
    }


    if (stampsDirPath != null) {
      stampsDir = new File(stampsDirPath);
    } else {
      stampsDir = new File(Main.class.getResource("/stamps").getFile());
    }

    if (!stampsDir.exists()) {
      System.err.println("Stampdir '" + stampsDir + "' must be existing folder!");
      System.exit(-1);
    }

    System.out.println("Stamps loaded from " + stampsDir);
    System.out.println("Jartin output is " + outputPath);
    System.out.println("Using lazy loading for stamps: " + lazyLoading);
  }

  private void loadBoolean(String key, Consumer<Boolean> setter) {
    Boolean value = Boolean.parseBoolean(System.getProperty(key));

    setter.accept(value);
  }

  private void loadString(String key, Consumer<String> setter) {
    String value = System.getProperty(key);
    setter.accept(value);
  }

  private void setOutput(String outputPath) {
    this.outputPath = outputPath;
  }

  public String getOutput() {
    return outputPath;
  }

  public File getStampsDir() {
    return stampsDir;
  }

  private void setStampsDirPath(String stampsDirPath) {
    this.stampsDirPath = stampsDirPath;
  }

  public boolean isLazyLoading() {
    return lazyLoading;
  }

  public void setLazyLoading(boolean lazyLoading) {
    this.lazyLoading = lazyLoading;
  }
}

