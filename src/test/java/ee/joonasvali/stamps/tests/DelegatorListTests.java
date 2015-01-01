package ee.joonasvali.stamps.tests;

import ee.joonasvali.stamps.query.DelegatorList;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Joonas Vali
 */
public class DelegatorListTests {
  @Test
  public void testListEnd() {

    List<Integer> orig = new ArrayList<>();
    orig.add(1);
    orig.add(2);
    orig.add(3);
    List<Integer> list = new DelegatorList<>(orig, 1, orig.size());
    Assert.assertEquals(2, list.size());
    Assert.assertEquals(2, (int)list.get(0));
    Assert.assertEquals(3, (int)list.get(1));
    try {
      Assert.assertEquals(4, (int)list.get(2));
      throw new RuntimeException("Assertion wrong");
    } catch (IndexOutOfBoundsException e) {
      //OK
    }
  }

  @Test
  public void testListStart() {
    List<Integer> orig = new ArrayList<>();
    orig.add(1);
    orig.add(2);
    orig.add(3);
    List<Integer> list = new DelegatorList<>(orig, 0, orig.size() - 1);
    Assert.assertEquals(2, list.size());
    Assert.assertEquals(1, (int)list.get(0));
    Assert.assertEquals(2, (int)list.get(1));
    try {
      Assert.assertEquals(3, (int)list.get(2));
      throw new RuntimeException("Assertion wrong list.get(2) shouldn't be available '" + list.get(2) + "'");
    } catch (IndexOutOfBoundsException e) {
      //OK
    }
  }

  @Test
  public void testSubList() {
    List<Integer> orig = new ArrayList<>();
    orig.add(1);
    orig.add(2);
    orig.add(3);
    orig.add(4);
    orig.add(5);
    orig.add(6);
    List<Integer> list = new DelegatorList<>(orig, 1, orig.size() - 1);

    Assert.assertEquals(4, list.size());
    Assert.assertEquals(2, (int)list.get(0));
    Assert.assertEquals(3, (int)list.get(1));
    Assert.assertEquals(4, (int)list.get(2));
    Assert.assertEquals(5, (int)list.get(3));

    list = list.subList(0, 3);
    Assert.assertEquals(2, (int)list.get(0));
    Assert.assertEquals(3, (int)list.get(1));
    Assert.assertEquals(4, (int)list.get(2));

    try {
      Assert.assertEquals(5, (int)list.get(3));
      throw new RuntimeException("Assertion wrong list.get(3) shouldn't be available '" + list.get(3) + "'");
    } catch (IndexOutOfBoundsException e) {
      //OK
    }

    list = list.subList(1, 3);
    Assert.assertEquals(3, (int)list.get(0));
    Assert.assertEquals(4, (int)list.get(1));
  }
}

