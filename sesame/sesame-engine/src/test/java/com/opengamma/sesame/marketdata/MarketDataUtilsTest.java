/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame.marketdata;

import static org.testng.AssertJUnit.assertEquals;

import java.util.List;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.opengamma.util.test.TestGroup;

@Test(groups = TestGroup.UNIT)
public class MarketDataUtilsTest {

  @Test
  public void cartesianProductNoList() {
    // this is surprising but mathematically correct for cartesian products of nothing
    List<?> expected = ImmutableList.of(ImmutableList.of());
    assertEquals(expected, MarketDataUtils.cartesianProduct());
  }

  @Test
  public void cartesianProductEmptyList() {
    List<?> list = ImmutableList.of();
    assertEquals(list, MarketDataUtils.cartesianProduct(list));
  }

  @Test
  public void cartesianProductOneItem() {
    List<Integer> list = ImmutableList.of(1);
    List<List<Integer>> expected = ImmutableList.<List<Integer>>of(ImmutableList.of(1));
    assertEquals(expected, MarketDataUtils.cartesianProduct(list));
  }

  @Test
  public void cartesianProductOneList() {
    List<Integer> list = ImmutableList.of(1, 2, 3);
    List<List<Integer>> expected =
        ImmutableList.<List<Integer>>of(ImmutableList.of(1), ImmutableList.of(2), ImmutableList.of(3));
    assertEquals(expected, MarketDataUtils.cartesianProduct(list));
  }

  @Test
  public void cartesianProductOneListEmpty() {
    List<Integer> list = ImmutableList.of(1, 2, 3);
    List<Integer> empty = ImmutableList.of();
    List<List<Integer>> expected = ImmutableList.of();
    assertEquals(expected, MarketDataUtils.cartesianProduct(list, empty));
    assertEquals(expected, MarketDataUtils.cartesianProduct(empty, list));
  }

  @Test
  public void cartesianProductTwoLists() {
    List<String> list1 = ImmutableList.of("1", "2", "3");
    List<String> list2 = ImmutableList.of("a", "b");
    List<List<String>> expected =
        ImmutableList.<List<String>>of(
            ImmutableList.of("1", "a"),
            ImmutableList.of("1", "b"),
            ImmutableList.of("2", "a"),
            ImmutableList.of("2", "b"),
            ImmutableList.of("3", "a"),
            ImmutableList.of("3", "b"));
    assertEquals(expected, MarketDataUtils.cartesianProduct(list1, list2));
  }

  @Test
  public void cartesianProductThreeLists() {
    List<String> list1 = ImmutableList.of("1", "2");
    List<String> list2 = ImmutableList.of("a", "b");
    List<String> list3 = ImmutableList.of("x", "y");
    List<List<String>> expected =
        ImmutableList.<List<String>>of(
            ImmutableList.of("1", "a", "x"),
            ImmutableList.of("1", "a", "y"),
            ImmutableList.of("1", "b", "x"),
            ImmutableList.of("1", "b", "y"),
            ImmutableList.of("2", "a", "x"),
            ImmutableList.of("2", "a", "y"),
            ImmutableList.of("2", "b", "x"),
            ImmutableList.of("2", "b", "y"));
    assertEquals(expected, MarketDataUtils.cartesianProduct(list1, list2, list3));
  }
}
