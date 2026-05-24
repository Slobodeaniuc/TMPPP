package com.chiril.library.controller;

import com.chiril.library.dto.AddToCartRequest;
import com.chiril.library.dto.LoanDto;
import com.chiril.library.facade.LibraryFacade;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final LibraryFacade facade;

    public CartController(LibraryFacade facade) {
        this.facade = facade;
    }

    @GetMapping("/{memberId}")
    public Map<String, List<String>> getCart(@PathVariable String memberId) {
        return Map.of("items", facade.viewBorrowCart(memberId));
    }

    @PostMapping("/{memberId}/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, List<String>> addToCart(
            @PathVariable String memberId,
            @RequestBody AddToCartRequest req) {
        facade.addItemToBorrowCart(memberId, req.itemId());
        return Map.of("items", facade.viewBorrowCart(memberId));
    }

    @DeleteMapping("/{memberId}/remove/{itemId}")
    public Map<String, Object> removeFromCart(
            @PathVariable String memberId,
            @PathVariable String itemId) {
        boolean removed = facade.removeItemFromBorrowCart(memberId, itemId);
        return Map.of("removed", removed, "items", facade.viewBorrowCart(memberId));
    }

    @PostMapping("/{memberId}/undo")
    public Map<String, Object> undo(@PathVariable String memberId) {
        boolean undone = facade.undoLastCartChange(memberId);
        return Map.of("undone", undone, "items", facade.viewBorrowCart(memberId));
    }

    @PostMapping("/{memberId}/checkout")
    public List<LoanDto> checkout(@PathVariable String memberId) {
        return facade.checkoutBorrowCart(memberId).stream()
                .map(LoanDto::from)
                .toList();
    }

    @DeleteMapping("/{memberId}/clear")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(@PathVariable String memberId) {
        facade.clearBorrowCart(memberId);
    }
}
