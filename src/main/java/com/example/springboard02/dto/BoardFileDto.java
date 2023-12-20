package com.example.springboard02.dto;
import lombok.*;
@Getter
@Setter
public class BoardFileDto {
	private int bf_num, bf_bnum;//bf_num=boardfile 기본키, bf_bnum=게시글 번호
	private String bf_oriname, bf_sysname;//원래 파일명, 변경한 파일명
}