package com.campusdual.lituraliaopen.repositories;

import com.campusdual.lituraliaopen.domain.Book;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    Optional<Book> findByTitle(String title);

    Page<Book> findByTitleContainingIgnoreCase(String searchTerm, Pageable pageable);

}
