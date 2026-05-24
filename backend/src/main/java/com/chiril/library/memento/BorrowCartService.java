package com.chiril.library.memento;

import com.chiril.library.domain.LibraryItem;
import com.chiril.library.domain.LibraryItemGroup;
import com.chiril.library.repo.Catalog;
import com.chiril.library.service.exceptions.ItemNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BorrowCartService {
    private final Catalog catalog;
    private final Map<String, BorrowCart> carts = new HashMap<>();
    private final Map<String, BorrowCartCaretaker> caretakers = new HashMap<>();

    public BorrowCartService(Catalog catalog) {
        if (catalog == null) {
            throw new IllegalArgumentException("catalog null");
        }
        this.catalog = catalog;
    }

    public void addItem(String memberId, String itemId) {
        BorrowCart cart = cartFor(memberId);
        saveSnapshot(memberId, cart);
        cart.addItem(requireBorrowableItem(itemId).getId());
    }

    public boolean removeItem(String memberId, String itemId) {
        BorrowCart cart = cartFor(memberId);
        if (!cart.getItemIds().contains(itemId)) {
            return false;
        }
        saveSnapshot(memberId, cart);
        return cart.removeItem(itemId);
    }

    public void clearCart(String memberId) {
        BorrowCart cart = cartFor(memberId);
        if (cart.getItemIds().isEmpty()) {
            return;
        }
        saveSnapshot(memberId, cart);
        cart.clear();
    }

    public boolean undoLastChange(String memberId) {
        BorrowCart cart = cartFor(memberId);
        return caretakerFor(memberId).pop()
                .map(memento -> {
                    cart.restore(memento);
                    return true;
                })
                .orElse(false);
    }

    public List<String> getCartItems(String memberId) {
        return cartFor(memberId).getItemIds();
    }

    public void resetCart(String memberId) {
        cartFor(memberId).clear();
        caretakerFor(memberId).clear();
    }

    private LibraryItem requireBorrowableItem(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException("itemId invalid");
        }
        LibraryItem item = catalog.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item inexistent: " + itemId));
        if (item instanceof LibraryItemGroup) {
            throw new IllegalArgumentException("Composite items cannot be added to cart: " + itemId);
        }
        return item;
    }

    private void saveSnapshot(String memberId, BorrowCart cart) {
        caretakerFor(memberId).push(cart.save());
    }

    private BorrowCart cartFor(String memberId) {
        validateMemberId(memberId);
        return carts.computeIfAbsent(memberId, key -> new BorrowCart());
    }

    private BorrowCartCaretaker caretakerFor(String memberId) {
        validateMemberId(memberId);
        return caretakers.computeIfAbsent(memberId, key -> new BorrowCartCaretaker());
    }

    private static void validateMemberId(String memberId) {
        if (memberId == null || memberId.isBlank()) {
            throw new IllegalArgumentException("memberId invalid");
        }
    }
}
