package com.example.springboard02.controller;
import com.example.springboard02.dto.*;
import com.example.springboard02.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@Slf4j
public class BoardRestController {
	@Autowired
	private MemberService mServ;
	@Autowired
	private BoardService bServ;

	//아이디 체크
	@GetMapping("idCheck")
	public String idCheck(String mid) {
		log.info("idCheck()");
		String res = mServ.idCheck(mid);
		return res;
	}

	//파일 삭제
	@PostMapping("delFile")
	public List<BoardFileDto> delFile(BoardFileDto bfDto, HttpSession session) {
		log.info("delFile");
		List<BoardFileDto> fList = bServ.delFile(bfDto, session);
		return fList;
	}

	//댓글 작성 처리
	@PostMapping("replyInsert")
	public ReplyDto replyInsert(ReplyDto rDto) {
		log.info("replyInsert()");
		rDto = bServ.replyInsert(rDto);
		return rDto;
	}
}//class end