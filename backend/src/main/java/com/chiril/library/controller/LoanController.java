package com.chiril.library.controller;

import com.chiril.library.dto.BorrowRequest;
import com.chiril.library.dto.LoanDto;
import com.chiril.library.dto.PenaltyDto;
import com.chiril.library.dto.ReturnReceiptDto;
import com.chiril.library.facade.LibraryFacade;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LibraryFacade facade;

    public LoanController(LibraryFacade facade) {
        this.facade = facade;
    }

    @PostMapping("/borrow")
    @ResponseStatus(HttpStatus.CREATED)
    public LoanDto borrow(@RequestBody BorrowRequest req) {
        return LoanDto.from(facade.borrowItem(req.memberId(), req.itemId()));
    }

    @PostMapping("/{itemId}/return")
    public ReturnReceiptDto returnItem(@PathVariable String itemId) {
        return ReturnReceiptDto.from(facade.returnItemWithPenalty(itemId));
    }

    @GetMapping("/member/{memberId}")
    public List<LoanDto> loansForMember(@PathVariable String memberId) {
        return facade.listLoansForMember(memberId).stream()
                .map(LoanDto::from)
                .toList();
    }

    @GetMapping("/{loanId}/penalty")
    public PenaltyDto penalty(
            @PathVariable String loanId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String penalty = facade.calculatePenaltyForLoan(loanId, date).toPlainString();
        return new PenaltyDto(loanId, penalty);
    }
}
