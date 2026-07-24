package com.sonarshowcase.repository;

import com.sonarshowcase.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Order entities
 * 
 * @author SonarShowcase
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    /**
     * Finds orders by user ID
     *
     * @param userId User ID
     * @return List of orders for the user
     */
    List<Order> findByUserId(Long userId);
    
    /**
     * Finds orders by status
     *
     * @param status Order status
     * @return List of orders with the specified status
     */
    List<Order> findByStatus(String status);
    
    /**
     * Finds orders by user ID and status
     *
     * @param userId User ID
     * @param status Order status
     * @return List of orders matching the criteria
     */
    @Query("SELECT o FROM Order o WHERE o.user.id = ?1 AND o.status = ?2")
    List<Order> findByUserIdAndStatus(Long userId, String status);
    
    /**
     * MNT: Duplicate method with slightly different name
     * Gets orders by user and status
     *
     * @param userId User ID
     * @param status Order status
     * @return List of orders matching the criteria
     */
    @Query("SELECT o FROM Order o WHERE o.user.id = ?1 AND o.status = ?2")
    List<Order> getOrdersByUserAndStatus(Long userId, String status);
}

