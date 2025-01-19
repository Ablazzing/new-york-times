package com.javacademy.new_york_times.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javacademy.new_york_times.dto.NewsDto;
import com.javacademy.new_york_times.dto.PageDto;
import com.javacademy.new_york_times.mapper.NewsMapper;
import com.javacademy.new_york_times.repository.NewsRepository;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class NewsControllerTest {
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private NewsMapper newsMapper;

    private final RequestSpecification reqSpec = new RequestSpecBuilder()
            .log(LogDetail.ALL)
            .setBasePath("api/news")
            .setContentType(ContentType.JSON)
            .build();
    private final ResponseSpecification resSpec =  new ResponseSpecBuilder()
            .log(LogDetail.ALL)
            .build();

    @Test
    @DisplayName("Создание новости - Успешно")
    public void createSuccess() {
        assertFalse(newsRepository.findAll().stream().anyMatch(
                e -> "text".equals(e.getText())
                     && "author".equals(e.getAuthor())
                     && "title".equals(e.getTitle())
        ));

        NewsDto dto = NewsDto.builder()
                .text("text")
                .author("author")
                .title("title")
                .build();
        RestAssured.given()
                .spec(reqSpec)
                .body(dto)
                .post("")
                .then()
                .spec(resSpec)
                .statusCode(201);

        assertTrue(newsRepository.findAll().stream().anyMatch(
                e -> "text".equals(e.getText())
                    && "author".equals(e.getAuthor())
                    && "title".equals(e.getTitle())
        ));
    }

    @Test
    @DisplayName("Удаление новости - Успешно")
    public void deleteSuccess() {
        RestAssured.given()
                .spec(reqSpec)
                .delete("1")
                .then()
                .spec(resSpec)
                .statusCode(202);

        assertTrue(newsRepository.findByNumber(1).isEmpty());
    }

    @Test
    @DisplayName("400 ошибка если номер страницы -1")
    public void findAll_incorrectPageNumber() {
        RestAssured.given()
                .spec(reqSpec)
                .param("pageNumber", -1)
                .get()
                .then()
                .spec(resSpec)
                .statusCode(400);
    }

    @Test
    @DisplayName("Получение 1 страницы с новостями")
    @SneakyThrows
    public void getFirstPage() {
        String result = RestAssured.given()
                .spec(reqSpec)
                .param("pageNumber", 1)
                .get()
                .then()
                .spec(resSpec)
                .statusCode(200)
                .extract()
                .asString();

        List<NewsDto> content = newsRepository.findAll().stream().limit(10)
                .map(e -> newsMapper.toDto(e))
                .toList();
        String expected = objectMapper.writeValueAsString(new PageDto<>(content, 10, 100, 1, 10));

        assertEquals(expected, result);
    }

}
