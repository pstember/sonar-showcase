package com.sonarshowcase.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Order processor with extremely high cognitive complexity.
 * 
 * MNT-07: Cognitive complexity > 50
 * 
 * @author SonarShowcase
 */
public class OrderProcessor {
    
    /**
     * Default constructor for OrderProcessor.
     */
    public OrderProcessor() {
    }

    /**
     * MNT-07: Extremely high cognitive complexity
     * This method has nested conditions, loops, and complex logic
     * SonarQube S3776 will flag this
     *
     * @param orderData Order data map
     * @param customerType Type of customer
     * @param isPriority Whether order is priority
     * @param hasDiscount Whether order has discount
     * @param region Shipping region
     * @param coupons List of coupon codes
     * @param orderDate Order date
     * @return Processing result string
     */
    public String processOrder(Map<String, Object> orderData, String customerType, 
                               boolean isPriority, boolean hasDiscount, String region,
                               List<String> coupons, Date orderDate) {
        
        String result = "";
        BigDecimal total = BigDecimal.ZERO;
        boolean isValid = true;
        int itemCount = 0;
        
        // Nested complexity starts here
        if (orderData != null) {
            if (orderData.containsKey("items")) {
                Object items = orderData.get("items");
                if (items instanceof List) {
                    List<?> itemList = (List<?>) items;
                    if (!itemList.isEmpty()) {
                        for (Object item : itemList) {
                            if (item instanceof Map) {
                                Map<?, ?> itemMap = (Map<?, ?>) item;
                                if (itemMap.containsKey("price")) {
                                    Object priceObj = itemMap.get("price");
                                    if (priceObj instanceof Number) {
                                        BigDecimal price = new BigDecimal(priceObj.toString());
                                        if (price.compareTo(BigDecimal.ZERO) > 0) {
                                            if (itemMap.containsKey("quantity")) {
                                                Object qtyObj = itemMap.get("quantity");
                                                if (qtyObj instanceof Number) {
                                                    int qty = ((Number) qtyObj).intValue();
                                                    if (qty > 0) {
                                                        total = total.add(price.multiply(new BigDecimal(qty)));
                                                        itemCount += qty;
                                                    } else {
                                                        isValid = false;
                                                    }
                                                } else {
                                                    isValid = false;
                                                }
                                            } else {
                                                total = total.add(price);
                                                itemCount++;
                                            }
                                        } else {
                                            isValid = false;
                                        }
                                    } else {
                                        isValid = false;
                                    }
                                } else {
                                    isValid = false;
                                }
                            } else {
                                isValid = false;
                            }
                        }
                    } else {
                        isValid = false;
                    }
                } else {
                    isValid = false;
                }
            } else {
                isValid = false;
            }
        } else {
            isValid = false;
        }
        
        // More nested complexity for discounts
        if (isValid) {
            if (hasDiscount) {
                if (customerType != null) {
                    if (customerType.equals("VIP")) {
                        if (isPriority) {
                            total = total.multiply(new BigDecimal("0.70"));
                        } else {
                            total = total.multiply(new BigDecimal("0.80"));
                        }
                    } else if (customerType.equals("GOLD")) {
                        if (isPriority) {
                            total = total.multiply(new BigDecimal("0.80"));
                        } else {
                            total = total.multiply(new BigDecimal("0.85"));
                        }
                    } else if (customerType.equals("SILVER")) {
                        if (isPriority) {
                            total = total.multiply(new BigDecimal("0.85"));
                        } else {
                            total = total.multiply(new BigDecimal("0.90"));
                        }
                    } else {
                        if (isPriority) {
                            total = total.multiply(new BigDecimal("0.95"));
                        }
                    }
                }
            }
            
            // Region-based complexity
            if (region != null) {
                if (region.equals("US")) {
                    if (total.compareTo(new BigDecimal("100")) > 0) {
                        // Free shipping
                    } else {
                        total = total.add(new BigDecimal("9.99"));
                    }
                } else if (region.equals("EU")) {
                    if (total.compareTo(new BigDecimal("150")) > 0) {
                        total = total.add(new BigDecimal("9.99"));
                    } else {
                        total = total.add(new BigDecimal("24.99"));
                    }
                } else if (region.equals("ASIA")) {
                    if (total.compareTo(new BigDecimal("200")) > 0) {
                        total = total.add(new BigDecimal("14.99"));
                    } else {
                        total = total.add(new BigDecimal("34.99"));
                    }
                } else {
                    total = total.add(new BigDecimal("49.99"));
                }
            }
            
            // Coupon processing complexity
            if (coupons != null && !coupons.isEmpty()) {
                for (String coupon : coupons) {
                    if (coupon != null) {
                        if (coupon.startsWith("PERCENT")) {
                            try {
                                int percent = Integer.parseInt(coupon.substring(7));
                                if (percent > 0 && percent <= 50) {
                                    BigDecimal discount = new BigDecimal(percent).divide(new BigDecimal("100"));
                                    total = total.multiply(BigDecimal.ONE.subtract(discount));
                                }
                            } catch (NumberFormatException e) {
                                // Invalid coupon
                            }
                        } else if (coupon.startsWith("FIXED")) {
                            try {
                                BigDecimal fixed = new BigDecimal(coupon.substring(5));
                                if (fixed.compareTo(total) < 0) {
                                    total = total.subtract(fixed);
                                }
                            } catch (NumberFormatException e) {
                                // Invalid coupon
                            }
                        } else if (coupon.equals("FREESHIP")) {
                            // Already handled
                        }
                    }
                }
            }
            
            // Date-based logic
            if (orderDate != null) {
                Date now = new Date();
                if (orderDate.before(now)) {
                    if (orderDate.getDay() == 0 || orderDate.getDay() == 6) {
                        // Weekend order
                        if (isPriority) {
                            total = total.add(new BigDecimal("4.99"));
                        }
                    }
                }
            }
            
            result = "Order processed. Total: $" + total.setScale(2, BigDecimal.ROUND_HALF_UP) + 
                     ", Items: " + itemCount;
        } else {
            result = "Invalid order data";
        }
        
        return result;
    }
    
    /**
     * MNT: Another complex method
     */
    /**
     * Validates an order
     *
     * @param order Order data map to validate
     * @return true if order is valid, false otherwise
     */
    public boolean validateOrder(Map<String, Object> order) {
        if (order == null) return false;
        if (!order.containsKey("customer")) return false;
        if (!order.containsKey("items")) return false;
        if (!order.containsKey("payment")) return false;
        if (!order.containsKey("shipping")) return false;
        
        Object customer = order.get("customer");
        if (customer == null) return false;
        if (!(customer instanceof Map)) return false;
        
        Map<?, ?> customerMap = (Map<?, ?>) customer;
        if (!customerMap.containsKey("id")) return false;
        if (!customerMap.containsKey("email")) return false;
        
        Object items = order.get("items");
        if (items == null) return false;
        if (!(items instanceof List)) return false;
        if (((List<?>) items).isEmpty()) return false;
        
        return true;
    }
    
    // ==================== CODE DUPLICATION (Intentional Maintainability Issue) ====================
    
    /**
     * MNT: Duplicated method - same logic as validateOrder but with different name
     * 
     * @param order Order data map to validate
     * @return true if order is valid, false otherwise
     */
    public boolean checkOrderValidity(Map<String, Object> order) {
        if (order == null) return false;
        if (!order.containsKey("customer")) return false;
        if (!order.containsKey("items")) return false;
        if (!order.containsKey("payment")) return false;
        if (!order.containsKey("shipping")) return false;
        
        Object customer = order.get("customer");
        if (customer == null) return false;
        if (!(customer instanceof Map)) return false;
        
        Map<?, ?> customerMap = (Map<?, ?>) customer;
        if (!customerMap.containsKey("id")) return false;
        if (!customerMap.containsKey("email")) return false;
        
        Object items = order.get("items");
        if (items == null) return false;
        if (!(items instanceof List)) return false;
        if (((List<?>) items).isEmpty()) return false;
        
        return true;
    }
    
    /**
     * MNT: Another duplicated validation method
     * 
     * @param orderData Order data map to validate
     * @return true if order is valid, false otherwise
     */
    public boolean isValidOrder(Map<String, Object> orderData) {
        if (orderData == null) return false;
        if (!orderData.containsKey("customer")) return false;
        if (!orderData.containsKey("items")) return false;
        if (!orderData.containsKey("payment")) return false;
        if (!orderData.containsKey("shipping")) return false;
        
        Object customer = orderData.get("customer");
        if (customer == null) return false;
        if (!(customer instanceof Map)) return false;
        
        Map<?, ?> customerMap = (Map<?, ?>) customer;
        if (!customerMap.containsKey("id")) return false;
        if (!customerMap.containsKey("email")) return false;
        
        Object items = orderData.get("items");
        if (items == null) return false;
        if (!(items instanceof List)) return false;
        if (((List<?>) items).isEmpty()) return false;
        
        return true;
    }
    
    /**
     * MNT: Duplicated complex processing logic - similar to processOrder
     * 
     * @param orderData Order data map
     * @param customerType Type of customer
     * @param isPriority Whether order is priority
     * @return Processing result string
     */
    public String handleOrder(Map<String, Object> orderData, String customerType, boolean isPriority) {
        String result = "";
        BigDecimal total = BigDecimal.ZERO;
        boolean isValid = true;
        int itemCount = 0;
        
        // Duplicated nested complexity
        if (orderData != null) {
            if (orderData.containsKey("items")) {
                Object items = orderData.get("items");
                if (items instanceof List) {
                    List<?> itemList = (List<?>) items;
                    if (!itemList.isEmpty()) {
                        for (Object item : itemList) {
                            if (item instanceof Map) {
                                Map<?, ?> itemMap = (Map<?, ?>) item;
                                if (itemMap.containsKey("price")) {
                                    Object priceObj = itemMap.get("price");
                                    if (priceObj instanceof Number) {
                                        BigDecimal price = new BigDecimal(priceObj.toString());
                                        if (price.compareTo(BigDecimal.ZERO) > 0) {
                                            if (itemMap.containsKey("quantity")) {
                                                Object qtyObj = itemMap.get("quantity");
                                                if (qtyObj instanceof Number) {
                                                    int qty = ((Number) qtyObj).intValue();
                                                    if (qty > 0) {
                                                        total = total.add(price.multiply(new BigDecimal(qty)));
                                                        itemCount += qty;
                                                    } else {
                                                        isValid = false;
                                                    }
                                                } else {
                                                    isValid = false;
                                                }
                                            } else {
                                                total = total.add(price);
                                                itemCount++;
                                            }
                                        } else {
                                            isValid = false;
                                        }
                                    } else {
                                        isValid = false;
                                    }
                                } else {
                                    isValid = false;
                                }
                            } else {
                                isValid = false;
                            }
                        }
                    } else {
                        isValid = false;
                    }
                } else {
                    isValid = false;
                }
            } else {
                isValid = false;
            }
        } else {
            isValid = false;
        }
        
        // Duplicated discount logic
        if (isValid) {
            if (customerType != null) {
                if (customerType.equals("VIP")) {
                    if (isPriority) {
                        total = total.multiply(new BigDecimal("0.70"));
                    } else {
                        total = total.multiply(new BigDecimal("0.80"));
                    }
                } else if (customerType.equals("GOLD")) {
                    if (isPriority) {
                        total = total.multiply(new BigDecimal("0.80"));
                    } else {
                        total = total.multiply(new BigDecimal("0.85"));
                    }
                } else if (customerType.equals("SILVER")) {
                    if (isPriority) {
                        total = total.multiply(new BigDecimal("0.85"));
                    } else {
                        total = total.multiply(new BigDecimal("0.90"));
                    }
                }
            }
            
            result = "Order handled. Total: $" + total.setScale(2, BigDecimal.ROUND_HALF_UP) + 
                     ", Items: " + itemCount;
        } else {
            result = "Invalid order data";
        }
        
        return result;
    }
}

