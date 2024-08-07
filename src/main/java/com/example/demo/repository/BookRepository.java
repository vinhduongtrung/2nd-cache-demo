package com.example.demo.repository;

import com.example.demo.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // JPQL
    Page<Book> findByNameContaining(String keyword, PageRequest pageRequest);


    // HQL
    @Query("SELECT b FROM Book b WHERE b.name = :name")
    Optional<Book> findByName(String name);
}
