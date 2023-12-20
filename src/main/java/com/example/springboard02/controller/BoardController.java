package com.example.springboard02.controller;

import com.example.springboard02.dto.*;
import com.example.springboard02.service.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.util.*;

@Controller
@Slf4j
public class BoardController {
	private ModelAndView mv;
	@Autowired
	private BoardService bServ;

	//게시글 화면
	@GetMapping("boardList")
	public ModelAndView boardList(SearchDto sDto, HttpSession session) {
		log.info("boardList()");
		mv = bServ.getBoardList(sDto, session);
		mv.setViewName("boardList");
		return mv;
	}

	//글쓰기 화면
	@GetMapping("writeForm")
	public String writeForm() {
		log.info("writeForm()");
		return "writeForm";
	}

	//글쓰기 처리
	@PostMapping("writeProc")
	public String writeProc(@RequestPart List<MultipartFile> files, BoardDto bDto, HttpSession session, RedirectAttributes rttr) {
		log.info("writeProc()");
		String view = bServ.boardWrite(files, bDto, session, rttr);//서비스로 넘겨서 처리
		return view;
	}

	//글 상세 보기
	@GetMapping("boardDetail")
	public ModelAndView boardDetail(int b_num) {
		log.info("boardDetail():{}", b_num);
		mv = bServ.getBoard(b_num);
		return mv;
	}

	//파일 다운로드
	@GetMapping("download")
	public ResponseEntity<Resource> fileDownload(BoardFileDto bfDto, HttpSession session) throws IOException {
		log.info("fileDownload()");
		ResponseEntity<Resource> resp = bServ.fileDownload(bfDto, session);
		return resp;
	}

	//게시글 수정 mv
	@GetMapping("updateForm")
	public ModelAndView updateForm(int b_num) {
		log.info("updateForm()");
		mv = bServ.updateBoard(b_num);
		return mv;
	}

	//게시글 수정
	@PostMapping("updateProc")
	public String updateProc(List<MultipartFile> files, BoardDto bDto, HttpSession session, RedirectAttributes rttr) {
		log.info("updateProc()");
		String view = bServ.updateBoard(files, bDto, session, rttr);
		return view;
	}

	//게시글 삭제
	@GetMapping("boardDelete")
	public String boardDelete(int b_num, RedirectAttributes rttr, HttpSession session) {
		log.info("boardDelete()");
		String view = bServ.deleteBoard(b_num, rttr, session);
		return view;
	}
}//class end