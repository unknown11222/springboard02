package com.example.springboard02.controller;

import com.example.springboard02.dto.MemberDto;
import com.example.springboard02.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
public class MemberController {
	@Autowired
	private MemberService mServ;

	//첫 페이지
	@GetMapping("/")
	public String home() {
		log.info("home()");
		return "index";//html 이름
	}

	//회원가입 화면
	@GetMapping("joinForm")
	public String joinForm() {
		log.info("joinForm()");
		return "joinForm";//html 이름
	}

	//회원 가입 처리(DB에 저장)
	@PostMapping("joinProc")
	public String joinProc(MemberDto mDto, RedirectAttributes rttr) {
		log.info("joinProc()");
		String view = mServ.memberJoin(mDto, rttr);
		return view;
	}

	//로그인 화면
	@GetMapping("loginForm")
	public String loginForm() {
		log.info("loginForm()");
		return "loginForm";//html 이름
	}

	//로그인 처리
	@PostMapping("loginProc")
	public String loginProc(MemberDto mDto, HttpSession session, RedirectAttributes rttr) {
		log.info("loginProc()");
		String view = mServ.loginProc(mDto, session, rttr);
		return view;
	}

	//로그아웃 시 첫 페이지로 이동
	@GetMapping("logout")
	public String logout(HttpSession session) {
		log.info("logout()");
		String view = mServ.logout(session);
		return view;//첫 페이지 경로
	}
}