package com.example.springboard02.dao;

import com.example.springboard02.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.*;

@Mapper
public interface BoardDao {
	List<BoardDto> selectBoardList(SearchDto sDto);

	int selectBoardCnt(SearchDto sDto);//전체 게시글 개수 구하는 메소드

	void insertBoard(BoardDto bDto);//게시글 저장 메소드

	void insertFile(BoardFileDto bfDto);//파일 정보 저장 메소드

	BoardDto selectBoard(int b_num);//게시글 하나만 가져오는 메소드

	List<BoardFileDto> selectFileList(int b_num);//게시글 번호에 해당 하는 파일 목록을 가져오는 메소드

	List<ReplyDto> selectReplyList(int b_num);//게시글 번호에 해당 하는 댓글 목록을 가져오는 메소드

	void deleteBoard(int b_num);//게시글 번호에 해당 하는 게시글 삭제 메소드

	void deleteFiles(int b_num);//게시글 번호에 해당 하는 파일목록 삭제

	void deleteReplys(int b_num);//게시글 번호에 해당 하는 댓글목록 삭제

	void riseView(int b_num);//게시글 번호에 해당 하는 조회수 1 상승 메소드

	List<String> selectFnameList(int b_num);//파일의 저장된 이름 목록을 구하는 메소드

	void deleteFile(String sysname);//수정 시 파일 하나만 삭제

	void updateBoard(BoardDto bDto);//게시글 수정 메소드

	void insertReply(ReplyDto rDto);//댓글 저장 메소드

	ReplyDto selectLastReply(int r_num);//저장 시 생성된 댓글 번호로 댓글 정보를 가져오는 메소드
}