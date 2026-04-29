package com.chiril.library.adapter;

import com.chiril.library.adapter.codec.LoanRecordCodec;
import com.chiril.library.adapter.storage.FileStorage;
import com.chiril.library.domain.Loan;
import com.chiril.library.repo.LoanRepository;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class FileLoanRepositoryAdapter implements LoanRepository {
    private final FileStorage storage;
    private final LoanRecordCodec codec;

    public FileLoanRepositoryAdapter(FileStorage storage) {
        this(storage, new LoanRecordCodec());
    }

    FileLoanRepositoryAdapter(FileStorage storage, LoanRecordCodec codec) {
        if (storage == null) {
            throw new IllegalArgumentException("storage null");
        }
        if (codec == null) {
            throw new IllegalArgumentException("codec null");
        }
        this.storage = storage;
        this.codec = codec;
    }

    @Override
    public synchronized void save(Loan loan) {
        if (loan == null) {
            throw new IllegalArgumentException("loan null");
        }
        Map<String, Loan> loans = loadLoans();
        loans.put(loan.getLoanId(), loan);
        persistLoans(loans);
    }

    @Override
    public synchronized Optional<Loan> findById(String loanId) {
        if (loanId == null || loanId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(loadLoans().get(loanId));
    }

    @Override
    public synchronized Optional<Loan> findActiveLoanByItemId(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return Optional.empty();
        }

        return loadLoans().values().stream()
                .filter(loan -> itemId.equals(loan.getItemId()) && loan.isActive())
                .sorted(Comparator.comparing(Loan::getLoanDate).thenComparing(Loan::getLoanId))
                .findFirst();
    }

    @Override
    public synchronized List<Loan> findByMemberId(String memberId) {
        if (memberId == null || memberId.isBlank()) {
            return List.of();
        }

        return loadLoans().values().stream()
                .filter(loan -> memberId.equals(loan.getMemberId()))
                .sorted(Comparator.comparing(Loan::getLoanDate).thenComparing(Loan::getLoanId))
                .collect(Collectors.toList());
    }

    private Map<String, Loan> loadLoans() {
        Map<String, Loan> loans = new LinkedHashMap<>();
        for (String line : storage.readLines()) {
            if (line.isBlank()) {
                continue;
            }
            Loan loan = codec.decode(line);
            loans.put(loan.getLoanId(), loan);
        }
        return loans;
    }

    private void persistLoans(Map<String, Loan> loans) {
        List<String> lines = loans.values().stream()
                .sorted(Comparator.comparing(Loan::getLoanId))
                .map(codec::encode)
                .toList();
        storage.writeLines(lines);
    }
}
