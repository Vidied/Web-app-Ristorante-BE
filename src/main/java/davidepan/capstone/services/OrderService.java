package davidepan.capstone.services;

import davidepan.capstone.entities.Order;
import davidepan.capstone.entities.OrderItem;
import davidepan.capstone.entities.Product;
import davidepan.capstone.enums.OrderStatus;
import davidepan.capstone.exceptions.BadRequestException;
import davidepan.capstone.exceptions.NotFoundException;
import davidepan.capstone.payloads.*;
import davidepan.capstone.repositories.OrderRepository;
import davidepan.capstone.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Value("${restaurant.cover-price}")
    private BigDecimal defaultCoverPrice;

    public Order findEntityById(Long id) {
        return orderRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new NotFoundException("Ordine con ID " + id + " non trovato"));
    }

    public List<OrderResponseDTO> findAll() {
        return orderRepository.findAllWithDetails().stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    public OrderResponseDTO findById(Long id) {
        Order order = this.findEntityById(id);
        return convertToResponseDto(order);
    }

    public List<OrderResponseDTO> findByStatus(OrderStatus status) {
        return orderRepository.findByOrderStatusWithDetails(status).stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    @Transactional
    public OrderResponseDTO save(OrderRequestDTO body) {
        Order newOrder = new Order();

        newOrder.setTableNumber(body.tableNumber());
        newOrder.setOrderType(body.orderType());
        newOrder.setCoverPrice(defaultCoverPrice);
        newOrder.setNotes(body.notes());
        newOrder.setOrderStatus(OrderStatus.PENDING);
        newOrder.setCreatedAt(LocalDateTime.now());

        this.processOrderItemsAndTotal(newOrder, body.items(), body.coverCount());

        Order savedOrder = orderRepository.save(newOrder);
        return convertToResponseDto(savedOrder);
    }

    @Transactional
    public OrderResponseDTO update(OrderRequestDTO body, Long id) {
        Order found = this.findEntityById(id);

        found.setTableNumber(body.tableNumber());
        found.setOrderType(body.orderType());
        found.setNotes(body.notes());

        found.getItems().clear();

        this.processOrderItemsAndTotal(found, body.items(), body.coverCount());

        Order updatedOrder = orderRepository.save(found);
        return convertToResponseDto(updatedOrder);
    }

    // Aggiunta di nuovi prodotti a un ordine esistente
    @Transactional
    public OrderResponseDTO appendItems(Long orderId, List<OrderItemRequestDTO> newItemsDTO) {
        Order order = this.findEntityById(orderId);

        if (order.getOrderStatus() == OrderStatus.COMPLETED || order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Impossibile modificare un ordine già chiuso o annullato.");
        }

        BigDecimal addedProductsTotal = BigDecimal.ZERO;

        for (OrderItemRequestDTO itemDTO : newItemsDTO) {
            Product product = productRepository.findById(itemDTO.productId())
                    .orElseThrow(() -> new NotFoundException("Prodotto non trovato con ID: " + itemDTO.productId()));

            if (Boolean.FALSE.equals(product.getIsAvailable())) {
                throw new BadRequestException("Il prodotto " + product.getName() + " non è disponibile.");
            }

            BigDecimal unitPrice = product.getPrice();
            OrderItem orderItem = new OrderItem(order, product, itemDTO.quantity(), unitPrice, itemDTO.notes());

            order.getItems().add(orderItem);

            BigDecimal itemSubtotal = unitPrice.multiply(BigDecimal.valueOf(itemDTO.quantity()));
            addedProductsTotal = addedProductsTotal.add(itemSubtotal);
        }

        order.setTotalAmount(order.getTotalAmount().add(addedProductsTotal));

        Order updatedOrder = orderRepository.save(order);
        return convertToResponseDto(updatedOrder);
    }

    public OrderResponseDTO updateStatus(Long id, OrderStatusUpdateDTO body) {
        Order found = this.findEntityById(id);
        found.setOrderStatus(body.orderStatus());
        Order updatedOrder = orderRepository.save(found);
        return convertToResponseDto(updatedOrder);
    }

    @Transactional
    public void deleteAllCompletedOrders() {
        List<Order> completedOrders = orderRepository.findByOrderStatusWithDetails(OrderStatus.COMPLETED);
        orderRepository.deleteAll(completedOrders);
    }

    @Transactional
    public void deleteSelectedCompletedOrders(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            List<Order> ordersToDelete = orderRepository.findAllById(ids).stream()
                    .filter(o -> o.getOrderStatus() == OrderStatus.COMPLETED)
                    .toList();
            orderRepository.deleteAll(ordersToDelete);
        }
    }

    @Transactional
    public void delete(Long id) {
        Order found = this.findEntityById(id);
        orderRepository.delete(found);
    }

    public OrderResponseDTO convertToResponseDto(Order order) {
        List<OrderItemResponseDTO> items = order.getItems() != null
                ? order.getItems().stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getId(),
                        item.getProduct() != null ? item.getProduct().getName() : null,
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getNotes(),
                        item.getProduct() != null ? item.getProduct().getDestinationArea() : null
                )).toList()
                : List.of();

        return new OrderResponseDTO(
                order.getId(),
                order.getTableNumber(),
                order.getCoverCount(),
                order.getOrderType(),
                order.getCreatedAt(),
                order.getOrderStatus(),
                order.getNotes(),
                order.getTotalAmount(),
                items
        );
    }

    private void processOrderItemsAndTotal(Order order, List<OrderItemRequestDTO> itemDTOs, Integer coverCount) {
        List<OrderItem> items = new ArrayList<>();
        BigDecimal productsTotal = BigDecimal.ZERO;

        if (itemDTOs != null) {
            for (OrderItemRequestDTO itemDTO : itemDTOs) {
                Product product = productRepository.findById(itemDTO.productId())
                        .orElseThrow(() -> new NotFoundException("Prodotto con ID " + itemDTO.productId() + " non trovato"));

                if (Boolean.FALSE.equals(product.getIsAvailable())) {
                    throw new BadRequestException("Il prodotto " + product.getName() + " non è attualmente disponibile");
                }

                BigDecimal unitPrice = product.getPrice();
                OrderItem orderItem = new OrderItem(order, product, itemDTO.quantity(), unitPrice, itemDTO.notes());
                items.add(orderItem);

                BigDecimal itemSubtotal = unitPrice.multiply(BigDecimal.valueOf(itemDTO.quantity()));
                productsTotal = productsTotal.add(itemSubtotal);
            }
        }

        order.getItems().addAll(items);

        int actualCoverCount = (coverCount != null && coverCount > 0) ? coverCount : 0;
        BigDecimal totalCoverAmount = defaultCoverPrice.multiply(BigDecimal.valueOf(actualCoverCount));

        order.setCoverCount(actualCoverCount);
        order.setTotalAmount(productsTotal.add(totalCoverAmount));
    }
}