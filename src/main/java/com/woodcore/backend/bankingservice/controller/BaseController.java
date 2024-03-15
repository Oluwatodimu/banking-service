package com.woodcore.backend.bankingservice.controller;

import com.woodcore.backend.bankingservice.dto.BaseResponse;
import com.woodcore.backend.bankingservice.utils.ResponseConstants;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class BaseController {

    private static final Integer PAGE_NUMBER = 0;
    private static final Integer PAGE_SIZE = 15;

    public static BaseResponse createBseResponse(Object data) {
        return new BaseResponse(data, ResponseConstants.SUCCESS, false);
    }

    public static Pageable createSortedPageableObject(Integer pageNumber, Integer pageSize) {
        Pageable pagingAndSortedByCreatedDateDesc;

        if (pageNumber == null || pageSize == null) {
            pagingAndSortedByCreatedDateDesc = PageRequest.of(PAGE_NUMBER, PAGE_SIZE, Sort.by("createdDate").descending());
        } else {
            pagingAndSortedByCreatedDateDesc = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate").descending());
        }
        return  pagingAndSortedByCreatedDateDesc;
    }
}
