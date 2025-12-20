package com.sonarshowcase.util;

import java.math.BigDecimal;

/**
 * Pricing service with magic numbers.
 * 
 * MNT-03: Magic numbers everywhere
 * 
 * @author SonarShowcase
 */
public class PricingService {
    
    /**
     * Default constructor for PricingService.
     */
    public PricingService() {
    }

    /**
     * Calculate final price with magic numbers.
     *
     * @param basePrice Base price of the item
     * @param customerType Type of customer (VIP, GOLD, SILVER, etc.)
     * @param quantity Quantity of items
     * @return Final calculated price including discounts and tax
     */
    public BigDecimal calculateFinalPrice(BigDecimal basePrice, String customerType, int quantity) {
        BigDecimal price = basePrice;
        
        // MNT: Magic numbers - should be named constants
        if (quantity > 10) {
            price = price.multiply(new BigDecimal("0.95")); // 5% bulk discount
        }
        if (quantity > 50) {
            price = price.multiply(new BigDecimal("0.90")); // Additional 10%
        }
        if (quantity > 100) {
            price = price.multiply(new BigDecimal("0.85")); // Additional 15%
        }
        
        // MNT: More magic numbers with strings
        if ("VIP".equals(customerType)) {
            price = price.multiply(new BigDecimal("0.80")); // 20% VIP discount
        } else if ("GOLD".equals(customerType)) {
            price = price.multiply(new BigDecimal("0.85")); // 15% gold discount
        } else if ("SILVER".equals(customerType)) {
            price = price.multiply(new BigDecimal("0.90")); // 10% silver discount
        }
        
        // MNT: Magic number for tax
        BigDecimal tax = price.multiply(new BigDecimal("0.0825"));
        price = price.add(tax);
        
        // MNT: Magic number for minimum
        if (price.compareTo(new BigDecimal("0.99")) < 0) {
            price = new BigDecimal("0.99");
        }
        
        return price;
    }
    
    /**
     * Calculate shipping with magic numbers.
     *
     * @param weight Weight of the package in pounds
     * @param distance Distance to ship in miles
     * @return Shipping cost
     */
    public double calculateShipping(double weight, double distance) {
        double cost = 0;
        
        // MNT: Magic numbers
        cost += weight * 0.50; // $0.50 per lb
        cost += distance * 0.02; // $0.02 per mile
        
        if (weight > 20) {
            cost += 5.00; // Overweight surcharge
        }
        if (distance > 500) {
            cost += 10.00; // Long distance surcharge
        }
        if (distance > 1000) {
            cost *= 1.25; // 25% extra for very long distance
        }
        
        // MNT: Magic number for minimum shipping
        if (cost < 3.99) {
            cost = 3.99;
        }
        
        // MNT: Magic number for free shipping threshold
        // This is confusing - cost is already calculated
        
        return cost;
    }
    
    /**
     * Apply coupon with magic strings and numbers.
     *
     * @param price Original price
     * @param couponCode Coupon code to apply
     * @return Price after coupon discount applied
     */
    public BigDecimal applyCoupon(BigDecimal price, String couponCode) {
        // MNT: Magic strings
        switch (couponCode) {
            case "SAVE10":
                return price.multiply(new BigDecimal("0.90"));
            case "SAVE20":
                return price.multiply(new BigDecimal("0.80"));
            case "SAVE30":
                return price.multiply(new BigDecimal("0.70"));
            case "HALF":
                return price.multiply(new BigDecimal("0.50"));
            case "SUMMER2023":
                return price.multiply(new BigDecimal("0.85"));
            case "WINTER2023":
                return price.multiply(new BigDecimal("0.75"));
            case "NEWYEAR":
                return price.subtract(new BigDecimal("25.00"));
            case "FREESHIP":
                return price; // Doesn't affect price
            default:
                return price;
        }
    }
    
    /**
     * Calculate installments with magic numbers.
     *
     * @param total Total amount to finance
     * @param months Number of months for installment plan
     * @return Array of monthly payment amounts
     */
    public double[] calculateInstallments(double total, int months) {
        double[] payments = new double[months];
        
        // MNT: Magic numbers for interest rates
        double interestRate;
        if (months <= 3) {
            interestRate = 0.0; // No interest
        } else if (months <= 6) {
            interestRate = 0.05; // 5%
        } else if (months <= 12) {
            interestRate = 0.10; // 10%
        } else {
            interestRate = 0.15; // 15%
        }
        
        double totalWithInterest = total * (1 + interestRate);
        double monthlyPayment = totalWithInterest / months;
        
        // MNT: Magic number for rounding
        monthlyPayment = Math.round(monthlyPayment * 100.0) / 100.0;
        
        for (int i = 0; i < months; i++) {
            payments[i] = monthlyPayment;
        }
        
        return payments;
    }
    
    // ==================== CODE DUPLICATION (Intentional Maintainability Issue) ====================
    
    /**
     * MNT: Duplicated method - same logic as calculateFinalPrice but with different name
     * This is intentional code duplication for SonarQube analysis
     * 
     * @param basePrice Base price of the item
     * @param customerType Type of customer (VIP, GOLD, SILVER, etc.)
     * @param quantity Quantity of items
     * @return Final calculated price including discounts and tax
     */
    public BigDecimal computeTotalPrice(BigDecimal basePrice, String customerType, int quantity) {
        BigDecimal price = basePrice;
        
        // MNT: Magic numbers - should be named constants (duplicated)
        if (quantity > 10) {
            price = price.multiply(new BigDecimal("0.95")); // 5% bulk discount
        }
        if (quantity > 50) {
            price = price.multiply(new BigDecimal("0.90")); // Additional 10%
        }
        if (quantity > 100) {
            price = price.multiply(new BigDecimal("0.85")); // Additional 15%
        }
        
        // MNT: More magic numbers with strings (duplicated)
        if ("VIP".equals(customerType)) {
            price = price.multiply(new BigDecimal("0.80")); // 20% VIP discount
        } else if ("GOLD".equals(customerType)) {
            price = price.multiply(new BigDecimal("0.85")); // 15% gold discount
        } else if ("SILVER".equals(customerType)) {
            price = price.multiply(new BigDecimal("0.90")); // 10% silver discount
        }
        
        // MNT: Magic number for tax (duplicated)
        BigDecimal tax = price.multiply(new BigDecimal("0.0825"));
        price = price.add(tax);
        
        // MNT: Magic number for minimum (duplicated)
        if (price.compareTo(new BigDecimal("0.99")) < 0) {
            price = new BigDecimal("0.99");
        }
        
        return price;
    }
    
    /**
     * MNT: Another duplicated method - same as calculateFinalPrice and computeTotalPrice
     * 
     * @param basePrice Base price of the item
     * @param customerType Type of customer (VIP, GOLD, SILVER, etc.)
     * @param quantity Quantity of items
     * @return Final calculated price including discounts and tax
     */
    public BigDecimal getFinalPriceWithTax(BigDecimal basePrice, String customerType, int quantity) {
        BigDecimal price = basePrice;
        
        // MNT: Duplicated logic
        if (quantity > 10) {
            price = price.multiply(new BigDecimal("0.95"));
        }
        if (quantity > 50) {
            price = price.multiply(new BigDecimal("0.90"));
        }
        if (quantity > 100) {
            price = price.multiply(new BigDecimal("0.85"));
        }
        
        if ("VIP".equals(customerType)) {
            price = price.multiply(new BigDecimal("0.80"));
        } else if ("GOLD".equals(customerType)) {
            price = price.multiply(new BigDecimal("0.85"));
        } else if ("SILVER".equals(customerType)) {
            price = price.multiply(new BigDecimal("0.90"));
        }
        
        BigDecimal tax = price.multiply(new BigDecimal("0.0825"));
        price = price.add(tax);
        
        if (price.compareTo(new BigDecimal("0.99")) < 0) {
            price = new BigDecimal("0.99");
        }
        
        return price;
    }
    
    /**
     * MNT: Duplicated shipping calculation - same as calculateShipping
     * 
     * @param weight Weight of the package in pounds
     * @param distance Distance to ship in miles
     * @return Shipping cost
     */
    public double computeShippingCost(double weight, double distance) {
        double cost = 0;
        
        // MNT: Duplicated magic numbers
        cost += weight * 0.50; // $0.50 per lb
        cost += distance * 0.02; // $0.02 per mile
        
        if (weight > 20) {
            cost += 5.00; // Overweight surcharge
        }
        if (distance > 500) {
            cost += 10.00; // Long distance surcharge
        }
        if (distance > 1000) {
            cost *= 1.25; // 25% extra for very long distance
        }
        
        // MNT: Duplicated magic number for minimum shipping
        if (cost < 3.99) {
            cost = 3.99;
        }
        
        return cost;
    }
    
    /**
     * MNT: Another duplicated shipping calculation
     * 
     * @param weight Weight of the package in pounds
     * @param distance Distance to ship in miles
     * @return Shipping cost
     */
    public double getShippingFee(double weight, double distance) {
        double cost = 0;
        
        cost += weight * 0.50;
        cost += distance * 0.02;
        
        if (weight > 20) {
            cost += 5.00;
        }
        if (distance > 500) {
            cost += 10.00;
        }
        if (distance > 1000) {
            cost *= 1.25;
        }
        
        if (cost < 3.99) {
            cost = 3.99;
        }
        
        return cost;
    }
}

