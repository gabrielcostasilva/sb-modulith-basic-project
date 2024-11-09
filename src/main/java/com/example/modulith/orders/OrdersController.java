package com.example.modulith.orders;

import java.util.Set;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
class OrdersController {

    private final Orders orders;

    OrdersController(final Orders orders) {
        this.orders = orders;
    }

    @PostMapping
    void place (@RequestBody Order order) {
        this.orders.place(order);
    }
}

@Service
@Transactional
class Orders {

    private final OrderRepository repository;
    private final ApplicationEventPublisher publisher;

    Orders(final OrderRepository repository, 
    final ApplicationEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    void place (Order order) {
        var saved = this.repository.save(order);
        System.out.println("Saved [ " + saved + "]");

        this.publisher.publishEvent(new OrderPlacedEvent(saved.id()));
    }

}

interface OrderRepository extends ListCrudRepository<Order, Integer> {
}

@Table("orders")
record Order(@Id Integer id, Set<LineItem> lineItems) {
}

@Table("order_line_items")
record LineItem(@Id Integer id, int product, int quantity) {

}

@Configuration
class AmqpIntegrationConfiguration {

    @Bean
    Binding binding(Queue queue, Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ORDERS_Q).noargs();

    }

    @Bean
    Exchange exchange() {
        return ExchangeBuilder.directExchange(ORDERS_Q).build();
    }

    @Bean
    Queue queue() {
        return QueueBuilder.durable(ORDERS_Q).build();
    }

    static final String ORDERS_Q = "orders";
}