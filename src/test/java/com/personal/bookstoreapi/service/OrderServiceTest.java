package com.personal.bookstoreapi.service;

import com.personal.bookstoreapi.domain.entity.Book;
import com.personal.bookstoreapi.domain.entity.DigitalBookItem;
import com.personal.bookstoreapi.domain.entity.Order;
import com.personal.bookstoreapi.domain.entity.OrderItem;
import com.personal.bookstoreapi.domain.entity.PhysicalBookItem;
import com.personal.bookstoreapi.domain.entity.User;
import com.personal.bookstoreapi.dto.request.CreateOrderItemDTO;
import com.personal.bookstoreapi.dto.request.CreateOrderRequestDTO;
import com.personal.bookstoreapi.dto.response.OrderDetailsResponseDTO;
import com.personal.bookstoreapi.dto.response.OrderResponseDTO;
import com.personal.bookstoreapi.repository.BookItemRepository;
import com.personal.bookstoreapi.repository.OrderRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    BookItemRepository bookItemRepository;

    @Mock
    UserService userService;

    @InjectMocks
    OrderService orderService;

    private static User user(long id, String email) {
        return User.builder().id(id).email(email).fullName("U").passwordHash("p").build();
    }

    private static PhysicalBookItem physical(long id, BigDecimal price, int stock) {
        PhysicalBookItem p = PhysicalBookItem.builder().id(id).price(price).stockQuantity(stock).availableForRent(false).build();
        Book b = new Book(); b.setId(100L); b.setTitle("T"); p.setBook(b); return p;
    }

    private static DigitalBookItem digital(long id, BigDecimal price) {
        DigitalBookItem d = DigitalBookItem.builder().id(id).price(price).availableForRent(false).build();
        Book b = new Book(); b.setId(200L); b.setTitle("DT"); d.setBook(b); return d;
    }

    @Nested
    class CreateOrder {

        @Test
        void happy_savesOrder_returnsOrderResponse() {
            var itemDto = new CreateOrderItemDTO(1L,2);
            var req = new CreateOrderRequestDTO(List.of(itemDto));

            when(userService.getCurrentUser()).thenReturn(user(3L,"a@b.com"));
            PhysicalBookItem p = physical(1L,new BigDecimal("5.00"),10);
            when(bookItemRepository.findById(1L)).thenReturn(Optional.of(p));

            Order saved = new Order(); saved.setId(9L); saved.setTotalAmount(new BigDecimal("10.00")); saved.setStatus(com.personal.bookstoreapi.domain.enums.OrderStatus.PAID);
            when(orderRepository.save(any())).thenReturn(saved);

            OrderResponseDTO res = orderService.createOrder(req);
            assertThat(res.orderId()).isEqualTo(9L);
            assertThat(res.totalAmount()).isEqualByComparingTo(new BigDecimal("10.00"));

            ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
            verify(orderRepository).save(captor.capture());
            Order toSave = captor.getValue();
            assertThat(toSave.getItems()).hasSize(1);
        }

        @Test
        void unhappy_emptyItems_throwsIllegalArgumentException() {
            var req = new CreateOrderRequestDTO(List.of());
            assertThatThrownBy(() -> orderService.createOrder(req)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("at least one item");
        }

        @Test
        void unhappy_bookItemNotFound_throwsIllegalArgumentException() {
            var itemDto = new CreateOrderItemDTO(55L,1);
            var req = new CreateOrderRequestDTO(List.of(itemDto));
            when(userService.getCurrentUser()).thenReturn(user(1L,"x"));
            when(bookItemRepository.findById(55L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> orderService.createOrder(req)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("BookItem not found");
        }

        @Test
        void unhappy_quantityZero_throwsIllegalArgumentException() {
            var itemDto = new CreateOrderItemDTO(1L,0);
            var req = new CreateOrderRequestDTO(List.of(itemDto));
            when(userService.getCurrentUser()).thenReturn(user(1L,"x"));
            when(bookItemRepository.findById(1L)).thenReturn(Optional.of(digital(1L,new BigDecimal("1.00"))));
            assertThatThrownBy(() -> orderService.createOrder(req)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Quantity must be greater than 0");
        }

        @Test
        void unhappy_physicalNotEnoughStock_throwsIllegalStateException() {
            var itemDto = new CreateOrderItemDTO(2L,5);
            var req = new CreateOrderRequestDTO(List.of(itemDto));
            when(userService.getCurrentUser()).thenReturn(user(1L,"x"));
            when(bookItemRepository.findById(2L)).thenReturn(Optional.of(physical(2L,new BigDecimal("3.00"),2)));
            assertThatThrownBy(() -> orderService.createOrder(req)).isInstanceOf(IllegalStateException.class).hasMessageContaining("Not enough stock");
        }

        @Test
        void unhappy_digitalQuantityGreaterThanOne_throwsIllegalArgumentException() {
            var itemDto = new CreateOrderItemDTO(3L,2);
            var req = new CreateOrderRequestDTO(List.of(itemDto));
            when(userService.getCurrentUser()).thenReturn(user(1L,"x"));
            when(bookItemRepository.findById(3L)).thenReturn(Optional.of(digital(3L,new BigDecimal("3.00"))));
            assertThatThrownBy(() -> orderService.createOrder(req)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Digital book can only be purchased once");
        }

        @Test
        void happy_physical_exactStock_decrementsAndSaves() {
            var itemDto = new CreateOrderItemDTO(10L,2);
            var req = new CreateOrderRequestDTO(List.of(itemDto));

            when(userService.getCurrentUser()).thenReturn(user(4L,"a@b.com"));
            PhysicalBookItem p = physical(10L,new BigDecimal("7.50"),2); // stock == quantity
            when(bookItemRepository.findById(10L)).thenReturn(Optional.of(p));

            Order saved = new Order(); saved.setId(11L); saved.setTotalAmount(new BigDecimal("15.00")); saved.setStatus(com.personal.bookstoreapi.domain.enums.OrderStatus.PAID);
            when(orderRepository.save(any())).thenReturn(saved);

            OrderResponseDTO res = orderService.createOrder(req);
            assertThat(res.orderId()).isEqualTo(11L);
            // original physical item should have been decremented to zero
            assertThat(p.getStockQuantity()).isEqualTo(0);
        }

        @Test
        void happy_digital_singleQuantity_passes() {
            var itemDto = new CreateOrderItemDTO(20L,1);
            var req = new CreateOrderRequestDTO(List.of(itemDto));
            when(userService.getCurrentUser()).thenReturn(user(5L,"d@d"));
            DigitalBookItem d = digital(20L,new BigDecimal("4.00"));
            when(bookItemRepository.findById(20L)).thenReturn(Optional.of(d));
            Order saved = new Order(); saved.setId(21L); saved.setTotalAmount(new BigDecimal("4.00")); saved.setStatus(com.personal.bookstoreapi.domain.enums.OrderStatus.PAID);
            when(orderRepository.save(any())).thenReturn(saved);

            OrderResponseDTO res = orderService.createOrder(req);
            assertThat(res.orderId()).isEqualTo(21L);
            assertThat(res.totalAmount()).isEqualByComparingTo(new BigDecimal("4.00"));
        }

        @Test
        void happy_multipleItems_totalAmountSum() {
            var items = List.of(new CreateOrderItemDTO(30L,1), new CreateOrderItemDTO(31L,3));
            var req = new CreateOrderRequestDTO(items);
            when(userService.getCurrentUser()).thenReturn(user(6L,"multi@x"));
            DigitalBookItem d = digital(30L,new BigDecimal("2.00"));
            PhysicalBookItem p = physical(31L,new BigDecimal("3.00"),5);
            when(bookItemRepository.findById(30L)).thenReturn(Optional.of(d));
            when(bookItemRepository.findById(31L)).thenReturn(Optional.of(p));
            Order saved = new Order(); saved.setId(32L); saved.setTotalAmount(new BigDecimal("11.00")); saved.setStatus(com.personal.bookstoreapi.domain.enums.OrderStatus.PAID);
            when(orderRepository.save(any())).thenReturn(saved);

            OrderResponseDTO res = orderService.createOrder(req);
            assertThat(res.orderId()).isEqualTo(32L);
            assertThat(res.totalAmount()).isEqualByComparingTo(new BigDecimal("11.00"));
            // physical stock decremented
            assertThat(p.getStockQuantity()).isEqualTo(2);
        }
    }

    @Nested
    class GetMyOrders {
        @Test
        void happy_returnsOrderResponseList() {
            when(userService.getCurrentUser()).thenReturn(user(2L,"x@x"));
            Order o = new Order(); o.setId(20L); o.setTotalAmount(new BigDecimal("2.00")); o.setStatus(com.personal.bookstoreapi.domain.enums.OrderStatus.PAID);
            when(orderRepository.findAllByUserId(2L)).thenReturn(List.of(o));

            var res = orderService.getMyOrders();
            assertThat(res).hasSize(1);
            assertThat(res.get(0).orderId()).isEqualTo(20L);
        }
    }

    @Nested
    class GetOrderDetails {
        @Test
        void happy_returnsOrderDetailsForOwner() {
            User u = user(7L,"a@b");
            when(userService.getCurrentUser()).thenReturn(u);
            Order o = new Order(); o.setId(50L); o.setUser(u); o.setTotalAmount(new BigDecimal("5.00")); o.setStatus(com.personal.bookstoreapi.domain.enums.OrderStatus.PAID);
            OrderItem it = new OrderItem(); it.setBookItem(digital(9L,new BigDecimal("5.00"))); it.setQuantity(1); o.setItems(List.of(it));
            when(orderRepository.findById(50L)).thenReturn(Optional.of(o));

            OrderDetailsResponseDTO dto = orderService.getOrderDetails(50L);
            assertThat(dto.orderId()).isEqualTo(50L);
            assertThat(dto.items()).hasSize(1);
        }

        @Test
        void unhappy_orderNotFound_throwsIllegalArgumentException() {
            when(userService.getCurrentUser()).thenReturn(user(1L,"x"));
            when(orderRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> orderService.getOrderDetails(99L)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Order not found");
        }

        @Test
        void unhappy_notOwner_throwsSecurityException() {
            User owner = user(10L,"o");
            User other = user(11L,"other");
            when(userService.getCurrentUser()).thenReturn(other);
            Order o = new Order(); o.setId(88L); o.setUser(owner);
            when(orderRepository.findById(88L)).thenReturn(Optional.of(o));

            assertThatThrownBy(() -> orderService.getOrderDetails(88L)).isInstanceOf(SecurityException.class).hasMessageContaining("not allowed");
        }
    }
}
