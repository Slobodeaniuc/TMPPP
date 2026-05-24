package com.chiril.library.controller;

import com.chiril.library.dto.AddBookRequest;
import com.chiril.library.dto.AddDvdRequest;
import com.chiril.library.dto.AddGroupRequest;
import com.chiril.library.dto.AddMagazineRequest;
import com.chiril.library.dto.LibraryItemDto;
import com.chiril.library.facade.LibraryFacade;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final LibraryFacade facade;

    public CatalogController(LibraryFacade facade) {
        this.facade = facade;
    }

    @GetMapping
    public List<LibraryItemDto> getAll() {
        return facade.listCatalogItems().stream()
                .map(LibraryItemDto::from)
                .toList();
    }

    @GetMapping("/{id}")
    public LibraryItemDto getById(@PathVariable String id) {
        return facade.findItemById(id)
                .map(LibraryItemDto::from)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + id));
    }

    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    public LibraryItemDto addBook(@RequestBody AddBookRequest req) {
        facade.addBook(req.id(), req.title(), req.author(), req.isbn());
        return facade.findItemById(req.id()).map(LibraryItemDto::from).orElseThrow();
    }

    @PostMapping("/magazines")
    @ResponseStatus(HttpStatus.CREATED)
    public LibraryItemDto addMagazine(@RequestBody AddMagazineRequest req) {
        facade.addMagazine(req.id(), req.title(), req.issueNumber());
        return facade.findItemById(req.id()).map(LibraryItemDto::from).orElseThrow();
    }

    @PostMapping("/dvds")
    @ResponseStatus(HttpStatus.CREATED)
    public LibraryItemDto addDvd(@RequestBody AddDvdRequest req) {
        facade.addDvd(req.id(), req.title(), req.durationMinutes());
        return facade.findItemById(req.id()).map(LibraryItemDto::from).orElseThrow();
    }

    @PostMapping("/groups")
    @ResponseStatus(HttpStatus.CREATED)
    public LibraryItemDto addGroup(@RequestBody AddGroupRequest req) {
        facade.addGroupByItemIds(req.id(), req.title(), req.childItemIds());
        return facade.findItemById(req.id()).map(LibraryItemDto::from).orElseThrow();
    }
}
