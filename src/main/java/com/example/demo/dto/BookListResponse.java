package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@ToString
public class BookListResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<BookResponse> books;
    private int totalPages;
}
