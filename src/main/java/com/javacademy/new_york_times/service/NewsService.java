package com.javacademy.new_york_times.service;

import com.javacademy.new_york_times.dto.NewsDto;
import com.javacademy.new_york_times.dto.PageDto;
import com.javacademy.new_york_times.entity.NewsEntity;
import com.javacademy.new_york_times.mapper.NewsMapper;
import com.javacademy.new_york_times.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;
    private final NewsMapper newsMapper;

    public NewsDto save(NewsDto dto) {
        return newsMapper.toDto(newsRepository.save(newsMapper.toEntity(dto)));
    }

    /**
     * Переписать этот метод
     */
    public PageDto<NewsDto> findAll(int currentPage) {
        List<NewsEntity> allEntities = newsRepository.findAll();
        int allElementsCount = allEntities.size();
        List<NewsDto> content = allEntities.stream()
                .skip((currentPage - 1) * PageDto.DEFAULT_PAGE_SIZE)
                .limit(PageDto.DEFAULT_PAGE_SIZE)
                .map(newsMapper::toDto)
                .toList();
        return new PageDto<>(
                    content,
                    content.size(),
                    allElementsCount / PageDto.DEFAULT_PAGE_SIZE,
                    currentPage,
                    PageDto.DEFAULT_PAGE_SIZE
                );
    }

    public NewsDto findByNumber(Integer number) {
        return newsMapper.toDto(newsRepository.findByNumber(number).orElseThrow());
    }

    public boolean deleteByNumber(Integer number) {
        return newsRepository.deleteByNumber(number);
    }

    public void update(NewsDto dto) {
        newsRepository.update(newsMapper.toEntity(dto));
    }

    public String getNewsText(Integer newsNumber) {
        return newsRepository.findByNumber(newsNumber).map(NewsEntity::getText).orElseThrow();
    }

    public String getNewsAuthor(Integer newsNumber) {
        return newsRepository.findByNumber(newsNumber).map(NewsEntity::getAuthor).orElseThrow();
    }
}
