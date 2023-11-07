package com.programmers.gccoffee.service;

import com.programmers.gccoffee.model.Email;
import com.programmers.gccoffee.model.Order;
import com.programmers.gccoffee.model.OrderItem;

import java.util.List;

public interface OrderService {
    Order createOrder(Email email, String address, String postcode, List<OrderItem> orderItems);
}
