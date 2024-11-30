package com.moviefix.dto;

import java.util.List;

public record MoviePageResponse(
        List<MovieDto> movieDto,
        Integer pageNumber,
        Integer pageSize,
        long totalElements,
        int totalSize,
        boolean isLast
) {

}
