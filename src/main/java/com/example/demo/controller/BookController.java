package com.example.demo.controller;

import com.example.demo.dto.BookListResponse;
import com.example.demo.dto.BookRequest;
import com.example.demo.dto.BookResponse;
import com.example.demo.entity.Book;
import com.example.demo.service.BookService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/books")
@AllArgsConstructor
public class BookController {

    private final BookService bookService;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/getAll")
    public ResponseEntity<BookListResponse> getAllBooks(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").ascending());
        BookListResponse bookListResponse = bookService.getAll(keyword, pageRequest);
        return ResponseEntity.ok(bookListResponse);
    }
    @GetMapping("/getById/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable("id") Long id){
        BookResponse book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @PostMapping("/getByName")
    public ResponseEntity<BookResponse> getBookByName(@RequestBody Map<String, Object> requestBody){
        String name = (String) requestBody.get("name");
        BookResponse book = bookService.getBookByName(name);
        return ResponseEntity.ok(book);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateBook(@PathVariable("id") Long id, @RequestBody BookRequest bookRequest) {
        Book book;
        try {
            book = bookService.updateBook(id, bookRequest);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("err when update product");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }
}
