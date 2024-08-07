package com.example.demo.service;

import com.example.demo.dto.BookListResponse;
import com.example.demo.dto.BookRequest;
import com.example.demo.dto.BookResponse;
import com.example.demo.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface BookService {

    BookListResponse getAll(String keyword, PageRequest pageRequest);

    Book updateBook(Long id, BookRequest bookRequest);

    BookResponse getBookById(Long id);

    BookResponse getBookByName(String name);
}
