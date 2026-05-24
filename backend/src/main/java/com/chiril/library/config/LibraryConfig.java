package com.chiril.library.config;

import com.chiril.library.domain.UserAccount;
import com.chiril.library.facade.LibraryFacade;
import com.chiril.library.factory.LibraryAbstractFactory;
import com.chiril.library.factory.StandardLibraryFactory;
import com.chiril.library.repo.InMemoryUserAccountRepository;
import com.chiril.library.repo.UserAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.file.Path;

@Configuration
public class LibraryConfig {

    @Value("${library.data-directory:data}")
    private String dataDirectory;

    @Bean
    public LibraryAbstractFactory libraryFactory() {
        return new StandardLibraryFactory();
    }

    @Bean
    public LibraryFacade libraryFacade(LibraryAbstractFactory factory) {
        LibraryFacade facade = LibraryFacade.fileBacked(Path.of(dataDirectory), factory);
        facade.ensureDemoCatalog();
        return facade;
    }

    @Bean
    public UserAccountRepository userAccountRepository(PasswordEncoder encoder) {
        InMemoryUserAccountRepository repo = new InMemoryUserAccountRepository();
        String hash = encoder.encode("parola123");
        repo.register(new UserAccount("U1", "alex.popescu@library.ro",   hash, "Alex Popescu",       "alex.popescu@library.ro"));
        repo.register(new UserAccount("U2", "maria.ionescu@library.ro",  hash, "Maria Ionescu",      "maria.ionescu@library.ro"));
        repo.register(new UserAccount("U3", "andrei.constantin@library.ro", hash, "Andrei Constantin","andrei.constantin@library.ro"));
        repo.register(new UserAccount("U4", "elena.dumitrescu@library.ro", hash, "Elena Dumitrescu", "elena.dumitrescu@library.ro"));
        repo.register(new UserAccount("U5", "mihai.popa@library.ro",    hash, "Mihai Popa",         "mihai.popa@library.ro"));
        repo.register(new UserAccount("U6", "cristina.stan@library.ro", hash, "Cristina Stan",      "cristina.stan@library.ro"));
        return repo;
    }
}
