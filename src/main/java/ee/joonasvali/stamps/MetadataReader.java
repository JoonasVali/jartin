package ee.joonasvali.stamps;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * @author Joonas Vali
 */
public class MetadataReader {

  public StampGroupMetadata loadMetadata(File props) throws IOException {
    Properties properties = new Properties();
    properties.load(new FileInputStream(props));
    StampGroupMetadata metadata = new StampGroupMetadata();

    loadDouble(properties, metadata, "rarity", 1, metadata::setRarity);

    return metadata;
  }

  private void loadDouble(Properties properties, StampGroupMetadata metadata, String name, double defaultValue, Consumer<Double> action) {
    double rarity;
    try {
      rarity = Double.parseDouble((String) properties.getOrDefault(name, defaultValue));
    } catch(Exception e) {
      rarity = defaultValue;
    }
    action.accept(rarity);
  }
}
