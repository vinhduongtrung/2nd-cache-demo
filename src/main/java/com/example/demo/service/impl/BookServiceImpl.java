package com.example.demo.service.impl;

import com.example.demo.dto.BookListResponse;
import com.example.demo.dto.BookRequest;
import com.example.demo.dto.BookResponse;
import com.example.demo.entity.Book;
import com.example.demo.repository.BookRepository;
import com.example.demo.service.BookService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    @Cacheable(value = "bookList", key = "#keyword + '-' + #pageRequest.pageNumber + '-' + #pageRequest.pageSize")
    public BookListResponse getAll(String keyword, PageRequest pageRequest) {
        Page<Book> booksPage = null;
        for(int i = 0; i < 100; i++) {
            booksPage = bookRepository.findByNameContaining(keyword, pageRequest);
        }

        List<BookResponse> bookResponses = booksPage.getContent().stream()
                .map(this::convertToBookResponse)
                .collect(Collectors.toList());

        Page<BookResponse> bookResponsePage =  new PageImpl<>(bookResponses, pageRequest, booksPage.getTotalElements());
        int totalPages = bookResponsePage.getTotalPages();
        return BookListResponse.builder()
                .books(bookResponses)
                .totalPages(totalPages)
                .build();
    }

    @Override
    @Transactional
    @CachePut(value = "bookList")
    // When update book, the data between mysql and ehcache is inconsistent
    // So we need to update data in ehcache as well
    public Book updateBook(Long id, BookRequest bookRequest) {
        Book updatedBook = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
        updatedBook.setName(bookRequest.getName());
        updatedBook.setPrice(bookRequest.getPrice());

        // Update the cached array of books
        Book[] books = getAllBooks();
        for (int i = 0; i < books.length; i++) {
            if (books[i].getId().equals(id)) {
                books[i] = updatedBook;
                break;
            }
        }
        return bookRepository.save(updatedBook);
    }

//    @Override
//    @Transactional
//    @CacheEvict(value = "bookList", allEntries = true)
//    //or we just simply clear cache, no need to update in the 2nd cache
//    // by default, cache will be clear after update data in mysql
//    public Book updateBook(Long id, BookRequest bookRequest) {
//        Book updatedBook = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
//        updatedBook.setName(bookRequest.getName());
//        updatedBook.setPrice(bookRequest.getPrice());
//        return bookRepository.save(updatedBook);
//    }

    @Cacheable(value = "bookList")
    public Book[] getAllBooks() {
        return bookRepository.findAll().toArray(new Book[0]);
    }

    @Override
    public BookResponse getBookByName(String name) {
        Optional<Book> bookOptional = Optional.empty();
        for(int i = 0; i < 100; i++) {
            bookOptional = bookRepository.findByName(name);
        }
        return bookOptional.map(this::convertToBookResponse).orElse(null);
    }

    @Override
    @Cacheable(value = "book", key="#id")
    public BookResponse getBookById(Long id) {
        Optional<Book> bookOptional = Optional.empty();
        for(int i = 0; i < 100; i++) {
            bookOptional = bookRepository.findById(id);
        }
        return bookOptional.map(this::convertToBookResponse).orElse(null);
    }

    private BookResponse convertToBookResponse(Book book) {
        BookResponse bookResponse = new BookResponse();
        bookResponse.setId(book.getId());
        bookResponse.setName(book.getName());
        bookResponse.setPrice(book.getPrice());
        return bookResponse;
    }


}

