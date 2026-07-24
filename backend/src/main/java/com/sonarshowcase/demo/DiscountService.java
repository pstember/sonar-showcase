package com.sonarshowcase.demo;

import java.math.BigDecimal;

public class DiscountService {

  public BigDecimal calculateDiscount(
      String customerType,
      BigDecimal orderTotal,
      boolean firstOrder) {

    if (customerType.equals("VIP") || customerType.equals("vip")) {
      return orderTotal.multiply(new BigDecimal("0.20"));
    }

    if (customerType.equals("STANDARD") && firstOrder) {
      return orderTotal.multiply(new BigDecimal("0.10"));
    }

    // Intentional demo defect: non-eligible customers should receive no discount.
    return orderTotal.multiply(new BigDecimal("0.15"));
  }
}
