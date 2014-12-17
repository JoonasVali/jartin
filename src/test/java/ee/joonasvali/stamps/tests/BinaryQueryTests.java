package ee.joonasvali.stamps.tests;

import ee.joonasvali.stamps.query.BinaryQuery;
import ee.joonasvali.stamps.query.BinaryValue;
import ee.joonasvali.stamps.query.Query;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Joonas Vali
 */
public class BinaryQueryTests {
  @Test
  public void test02(){
    List<Integer> list = new ArrayList<>();
    list.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    BinaryQuery<Integer> q = new BinaryQuery<>(0.2); // Divide to [1, 2] and [3, 4, ...]
    Query<Integer> queryStart = thelist -> {
      return thelist.get(0);
    };

    Query<Integer> queryEnd = thelist -> {
      return thelist.get(thelist.size() - 1);
    };

    Integer startZero = q.get(list, BinaryValue.ZERO, queryStart);
    Assert.assertEquals(1, (int)startZero);

    Integer startOne = q.get(list, BinaryValue.ONE, queryStart);
    Assert.assertEquals(3, (int)startOne);

    Integer endZero = q.get(list, BinaryValue.ZERO, queryEnd);
    Assert.assertEquals(2, (int)endZero);

    Integer endOne = q.get(list, BinaryValue.ONE, queryEnd);
    Assert.assertEquals(10, (int)endOne);



  }

  @Test
  public void test03(){
    List<Integer> list = new ArrayList<>();
    list.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    BinaryQuery<Integer> q = new BinaryQuery<>(0.3); // Divide to [1, 2, 3] and [4, ...]
    Query<Integer> queryStart = thelist -> {
      return thelist.get(0);
    };

    Query<Integer> queryEnd = thelist -> {
      return thelist.get(thelist.size() - 1);
    };

    Integer startZero = q.get(list, BinaryValue.ZERO, queryStart);
    Assert.assertEquals(1, (int)startZero);

    Integer startOne = q.get(list, BinaryValue.ONE, queryStart);
    Assert.assertEquals(4, (int)startOne);

    Integer endZero = q.get(list, BinaryValue.ZERO, queryEnd);
    Assert.assertEquals(3, (int)endZero);

    Integer endOne = q.get(list, BinaryValue.ONE, queryEnd);
    Assert.assertEquals(10, (int)endOne);
  }

  @Test
  public void testSmall(){
    List<Integer> list = new ArrayList<>();
    list.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    BinaryQuery<Integer> q = new BinaryQuery<>(0.05); // Divide to [1] and [2, 3, ...]
    Query<Integer> queryStart = thelist -> {
      return thelist.get(0);
    };

    Query<Integer> queryEnd = thelist -> {
      return thelist.get(thelist.size() - 1);
    };

    Integer startZero = q.get(list, BinaryValue.ZERO, queryStart);
    Assert.assertEquals(1, (int)startZero);

    Integer startOne = q.get(list, BinaryValue.ONE, queryStart);
    Assert.assertEquals(2, (int)startOne);

    Integer endZero = q.get(list, BinaryValue.ZERO, queryEnd);
    Assert.assertEquals(1, (int)endZero);

    Integer endOne = q.get(list, BinaryValue.ONE, queryEnd);
    Assert.assertEquals(10, (int)endOne);
  }

  @Test
  public void testBig(){
    List<Integer> list = new ArrayList<>();
    list.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    BinaryQuery<Integer> q = new BinaryQuery<>(0.99); // Divide to [1, 2, 3 ...] and [10]
    Query<Integer> queryStart = thelist -> {
      return thelist.get(0);
    };

    Query<Integer> queryEnd = thelist -> {
      return thelist.get(thelist.size() - 1);
    };

    Integer startZero = q.get(list, BinaryValue.ZERO, queryStart);
    Assert.assertEquals(1, (int)startZero);

    Integer startOne = q.get(list, BinaryValue.ONE, queryStart);
    Assert.assertEquals(10, (int)startOne);

    Integer endZero = q.get(list, BinaryValue.ZERO, queryEnd);
    Assert.assertEquals(9, (int)endZero);

    Integer endOne = q.get(list, BinaryValue.ONE, queryEnd);
    Assert.assertEquals(10, (int)endOne);
  }
}

