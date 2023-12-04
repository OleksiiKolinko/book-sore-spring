package mate.academy.repository.order;

import java.util.Optional;
import mate.academy.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByUserId(Long userId, Pageable pageable);

    Optional<Order> findByIdAndUserId(Long orderId, Long userId);
}
