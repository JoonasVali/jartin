package ee.joonasvali.stamps;

import ee.joonasvali.stamps.color.ColorModel;
import ee.joonasvali.stamps.color.Pallette;
import ee.joonasvali.stamps.color.PositionAwareColor;
import ee.joonasvali.stamps.color.PositionAwareColorModel;
import ee.joonasvali.stamps.query.RandomQuery;
import ee.joonasvali.stamps.ui.ProgressCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;

/**
 * @author Joonas Vali
 */
public class Painting {
  public static final Logger log = LoggerFactory.getLogger(Painting.class);
  private static final Projection POISON_PILL = canvas -> { /* Nothing to do */ };

  private static RandomQuery<ColorModel> colorModelChooser = RandomQuery.create();
  private static RandomQuery<Color> colorChooser = RandomQuery.create();

  private ExecutorService executor = Executors.newSingleThreadExecutor();
  private final ArrayBlockingQueue<Projection> projections;
  private volatile BufferedImage canvas;
  private final int width, height;
  private final Pallette pallette;
  private volatile boolean startPainting;
  private SynchronousQueue<BufferedImage> canvasSync;

  private volatile ProgressCounter counter;


  public void startPainting(ProgressCounter counter) {
    this.counter = counter;
    if (startPainting) {
      log.error("Illegal state in Painting, can't call startPainting");
      System.exit(-1);
    }
    startPainting = true;
    executor.execute(getAction());
  }

  public void stopPainting() {
    if (!startPainting) {
      log.error("Illegal state in Painting, can't call stopPainting");
      System.exit(-1);
    }
    startPainting = false;

    try {
      projections.put(POISON_PILL);
    } catch (InterruptedException e) {
      log.error(e.getMessage(), e);
      System.exit(-1);
    }

    executor.shutdown();
  }


  public Painting(int width, int height, Pallette pallette, int projections) {
    this.width = width;
    this.height = height;
    this.pallette = pallette;
    this.projections = new ArrayBlockingQueue<>(projections + 1);
    this.canvasSync = new SynchronousQueue<>();

  }

  public void addProjection(Projection projection) {
    try {
      projections.put(projection);
    } catch (InterruptedException e) {
      log.error(e.getMessage(), e);
      System.exit(-1);
    }
  }

  private void paintBackground() {
    canvas = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
    ColorModel colorModel = pallette.getColor(colorModelChooser);
    if (colorModel instanceof PositionAwareColorModel) {
      PositionAwareColorModel bgModel = (PositionAwareColorModel) colorModel;
      PositionAwareColor bgColor = bgModel.getColor();

      for (int i = 0; i < this.width; i++) {
        for (int j = 0; j < this.height; j++) {
          canvas.setRGB(i, j, bgColor.getColor(i, j).getRGB());
        }
      }
    } else {
      Color backgroundColor = colorModel.getColor(colorChooser);

      for (int i = 0; i < this.width; i++) {
        for (int j = 0; j < this.height; j++) {
          canvas.setRGB(i, j, backgroundColor.getRGB());
        }
      }
    }
  }


  public BufferedImage getImage() throws InterruptedException {
    return canvasSync.take();
  }

  public Runnable getAction() {
    return new Runnable() {
      @Override
      public void run() {

        paintBackground();
        while (startPainting || projections.size() > 0) {
          try {
            Projection projection = projections.take();
            if (projection == POISON_PILL) break;
            projection.paintTo(canvas);
            counter.increase();
          } catch (InterruptedException e) {
            // Nothing to do
          }
        }

        counter.clear();
        try {
          canvasSync.put(canvas);
        } catch (InterruptedException e) {
          System.exit(-1);
        }
      }
    };
  }
}
