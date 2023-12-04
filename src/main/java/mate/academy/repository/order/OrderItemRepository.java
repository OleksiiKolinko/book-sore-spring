package mate.academy.repository.order;

import java.util.Optional;
import mate.academy.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("FROM OrderItem oi LEFT JOIN oi.order o LEFT JOIN o.user u "
            + "WHERE oi.id = :id AND o.id = :orderId AND u.id = :userId")
    Optional<OrderItem> findByIdAndOrderIdAndUserId(Long id, Long orderId, Long userId);
}
