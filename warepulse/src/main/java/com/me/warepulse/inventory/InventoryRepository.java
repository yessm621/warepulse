package com.me.warepulse.inventory;

import com.me.warepulse.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findBySkuIdAndLocationId(Long skuId, Long locationId);

    List<Inventory> findBySkuId(Long skuId);

    List<Inventory> findByLocationId(Long locationId);

    @Query("""
                select coalesce(sum(i.quantity), 0)
                from Inventory i
                where i.sku.id = :skuId
            """)
    int sumQuantityBySkuId(Long skuId);

    @Query("""
                select (i.quantity - i.reservedQty)
                from Inventory i
                where i.sku.id = :skuId and i.location.id = :locationId
            """)
    int availableQty(Long skuId, Long locationId);
}
