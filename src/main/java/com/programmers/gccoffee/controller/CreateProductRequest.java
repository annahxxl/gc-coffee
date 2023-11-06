package com.programmers.gccoffee.controller;

import com.programmers.gccoffee.model.Category;

public record CreateProductRequest(String productName, Category category, long price, String description) {
}
