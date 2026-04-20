package com.fisioterapiakinevid.kinevid.rest.response;

import lombok.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePage<T> {
    private List<T> content;
    private Pageable pageable;
    private boolean last;
    private Integer totalPages;
    private Long totalElements;
    private Sort sort;
    private boolean first;
    private Integer numberOfElements;
    private Integer size;
    private Integer number;
    private boolean empty;
}
