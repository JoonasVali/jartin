package ee.joonasvali.stamps.stamp;

/**
 * @author Joonas Vali
 */
public class CompositeStampCreationException extends Exception {
  public CompositeStampCreationException(String message) {
    super(message);
  }

  public CompositeStampCreationException(IllegalArgumentException e) {
    super(e);
  }
}
