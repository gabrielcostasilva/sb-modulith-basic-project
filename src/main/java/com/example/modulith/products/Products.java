package com.example.modulith.products;

import org.springframework.context.event.EventListener;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.modulith.orders.OrderPlacedEvent;

// @Transactional
@Service
class Products {

    // @Async
    // @EventListener
    @ApplicationModuleListener
    void on(OrderPlacedEvent ope) throws Exception {

        System.out.println("Starting [" + ope + "]");
        Thread.sleep(10_000);
        System.out.println("Stopping [" + ope + "]");
    }
    
}
