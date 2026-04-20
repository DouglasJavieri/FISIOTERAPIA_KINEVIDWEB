package com.fisioterapiakinevid.kinevid.rest.util;


import com.fisioterapiakinevid.kinevid.rest.constants.ApiConstants;
import com.fisioterapiakinevid.kinevid.rest.response.ResponseBody;
import com.fisioterapiakinevid.kinevid.rest.response.ResponsePage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import java.util.List;

public class ApiUtil {

    public static <T> ResponseBody<T> buildResponseWithDefaults(T data) {
        return ResponseBody.<T>builder()
                .code(ApiConstants.OK_CODE)
                .message(ApiConstants.OK_MESSAGE)
                .data(data)
                .build();
    }

    public static <T> ResponseBody<T> buildResponseWithoutBody() {
        return ResponseBody.<T>builder()
                .code(ApiConstants.OK_CODE)
                .message(ApiConstants.OK_MESSAGE)
                .build();
    }

    public static <T> ResponseBody<T> buildSuccessResponse(T data, String message) {
        return ResponseBody.<T>builder()
                .code(ApiConstants.OK_CODE)
                .message(message)
                .data(data)
                .build();
    }


    public static <T> ResponseBody<T> buildResponse(T data, String code, String message) {
        return ResponseBody.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

    public static <T, P> ResponsePage<T> buildResponsePaged(Page<P> page, List<T> content) {
        return ResponsePage.<T>builder()
                .content(content)
                .pageable(page.getPageable())
                .last(page.isLast())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .sort(page.getSort())
                .first(page.isFirst())
                .numberOfElements(page.getNumberOfElements())
                .size(page.getSize())
                .number(page.getNumber())
                .empty(page.isEmpty())
                .build();
    }

    public static Pageable buildPageableWithSort(int page, int size, String sortBy, Sort.Direction direction) {
        Sort sort;
        if (StringUtil.isEmptyOrNull(sortBy)) sortBy = "id";
        if (direction != null) sort = Sort.by(direction,  sortBy);
        else sort = Sort.by(sortBy);
        return PageRequest.of(page, size, sort);
    }
}

