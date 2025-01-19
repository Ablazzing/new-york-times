package com.javacademy.new_york_times.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageDto <T> {
    public static final int DEFAULT_PAGE_SIZE = 10;
    private List<T> content;
    private Integer size;
    private Integer pagesCount;
    private Integer currentPage;
    private Integer portionSize;
}
