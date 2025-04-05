package com.mild.andyou.utils;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PageRq {

    @Schema(description = "페이지 번호", defaultValue = "0")
    private Integer page = 0;

    @Schema(description = "페이지 크기", defaultValue = "10")
    private Integer size = 10;

    @Schema(description = "direction", defaultValue = "DESC")
    private Sort.Direction direction = Sort.Direction.DESC;

    @Schema(description = "property", defaultValue = "id")
    private String property = "id";


    public Pageable toPageable() {
        if (direction != null && property != null) {
            Sort sort = Sort.by(new Sort.Order(this.direction, this.property));
            return PageRequest.of(this.page, this.size, sort);
        }

        return PageRequest.of(this.page, this.size);
    }

    public PageRq(Integer page, Integer size, Sort.Direction direction, String property) {
        this.page = page == null ? 0 : page;
        this.size = size == null ? 10 : size;
        this.direction = direction == null ? Sort.Direction.DESC : direction;
        this.property = property == null ? "id" : property;
    }

}