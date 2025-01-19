package com.javacademy.new_york_times.controller;

import com.javacademy.new_york_times.dto.NewsDto;
import com.javacademy.new_york_times.dto.PageDto;
import com.javacademy.new_york_times.exception.PageNumberLessZeroException;
import com.javacademy.new_york_times.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Сделать 7 операций внутри контроллера.
 * 1. Создание новости. Должно чистить кэш.
 * 2. Удаление новости по id. Должно чистить кэш.
 * 3. Получение новости по id. Должно быть закэшировано.
 * 4. Получение всех новостей (новости должны отдаваться порциями по 10 штук). Должно быть закэшировано.
 * 5. Обновление новости по id. Должно чистить кэш.
 * 6. Получение текста конкретной новости.
 * 7. Получение автора конкретной новости.
 *
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
public class NewsController {
    private final NewsService newsService;
    private final CacheManager cacheManager;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(value = "news", allEntries = true)
    public void create(@RequestBody NewsDto dto) {
        newsService.save(dto);
    }

    @DeleteMapping("{number}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Caching(evict = {
            @CacheEvict(value = "news", allEntries = true),
            @CacheEvict(value = "news_by_id", key = "#number")
    })
    public boolean deleteByNumber(@PathVariable Integer number) {
//        if (cacheManager.getCache("news") != null) {
//            cacheManager.getCache("news").clear();
//        }
//        if (cacheManager.getCache("news_by_id") != null) {
//            cacheManager.getCache("news_by_id").evict(number);
//        }
        return newsService.deleteByNumber(number);
    }

    @GetMapping("{number}")
    @Cacheable(value = "news_by_id")
    public NewsDto getByNumber(@PathVariable Integer number) {
        return newsService.findByNumber(number);
    }

    @GetMapping
    @Cacheable(value = "news")
    public PageDto<NewsDto> getAll(@RequestParam(required = false) Integer pageNumber) {
        if (pageNumber == null) {
            return newsService.findAll(1);
        } else if (pageNumber < 1) {
            throw new PageNumberLessZeroException("Количество страниц должно быть больше 0");
        }
        return newsService.findAll(pageNumber);
    }

    @PutMapping("{number}")
    @Caching(evict = {
            @CacheEvict(value = "news", allEntries = true),
            @CacheEvict(value = "news_by_id", key = "#number")
    })
    public void update(@RequestBody NewsDto newsDto, @PathVariable Integer number) {
        newsDto.setNumber(number);
        newsService.update(newsDto);
    }

    @GetMapping("{number}/text")
    public String getText(@PathVariable Integer number) {
        return newsService.getNewsText(number);
    }

    @GetMapping("{number}/author")
    public String getAuthor(@PathVariable Integer number) {
        return newsService.getNewsAuthor(number);
    }

}
