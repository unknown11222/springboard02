package com.example.springboard02.dto;
import lombok.*;
@Getter
@Setter
public class SearchDto {
	private String colname, keyword;//컬럼명, 검색 문장
	private int pageNum, listCnt;//페이지 번호, 페이지 당 출력할 게시글 개수
}