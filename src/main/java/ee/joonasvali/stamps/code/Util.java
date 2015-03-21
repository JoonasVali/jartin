package ee.joonasvali.stamps.code;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * @author Joonas Vali
 */
public class Util {
  public static final Logger log = LoggerFactory.getLogger(Util.class);
  public static void assertEDT() {
    if (!SwingUtilities.isEventDispatchThread()) {
      log.error("Expected EDT", new RuntimeException("Wrong thread, expected EDT"));
      System.exit(-1);
    }
  }

  public static String getUserDir() {
    return System.getProperty("user.dir");
  }
}
