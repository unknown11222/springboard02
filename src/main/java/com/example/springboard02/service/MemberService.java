package com.example.springboard02.service;

import com.example.springboard02.dao.*;
import com.example.springboard02.dto.*;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
@Slf4j
public class MemberService {
	@Autowired
	private MemberDao mDao;//클래스 안에서만 사용
	private BCryptPasswordEncoder pEncoder = new BCryptPasswordEncoder();//비밀번호 암호화 인코더.

	//아이디 중복 체크
	public String idCheck(String mid) {//public은 패키지 전체에서 사용하기 위한 용도
		log.info("idCheck()");
		String result = "";
		int mcnt = mDao.selectId(mid);
		if (mcnt == 0) result = "ok";//중복되지 않으면 사용 가능
		else result = "fail";//중복되면 사용 불가
		return result;
	}

	//회원 가입 처리
	public String memberJoin(MemberDto mDto, RedirectAttributes rttr) {
		log.info("memberJoin()");
		//view=가입 성공 시 첫페이지로, 실패 시 가입 페이지로 이동
		String view = null, msg = null, encpwd = pEncoder.encode(mDto.getM_pwd());//msg=경고창으로 출력할 메세지 저장 변수, encpwd=비밀번호 암호화 처리
		mDto.setM_pwd(encpwd);//암호화된 비밀번호를 다시 dto 객체에 저장

		//insert 작업
		try {
			mDao.insertMember(mDto);
			msg = "가입 성공";
			view = "redirect:/";//list 페이지 작성 시 변경 예정
		} catch (Exception e) {
			e.printStackTrace();
			msg = "가입 실패";
			view = "redirect:joinForm";
		}
		rttr.addFlashAttribute("msg", msg);
		return view;
	}

	//로그인 처리
	public String loginProc(MemberDto mDto, HttpSession session, RedirectAttributes rttr) {
		log.info("loginProc()");
		String view = null, msg = null, encPwd = mDao.selectPwd(mDto.getM_id());

		//DB 에서 해당 id의 pwd를 가져옴(암호문). MemberDto에 담겨있음.
		//encPwd가 null(비밀번호 안들어옴)이거나, 암호화된 비밀번호가 들어올 경우(해당 아이디가 이미 있음)
		if (encPwd != null) {
			//matches(입력한 비밀번호와 DB에서 가져온 암호화된 비밀번호를 비교)
			if (pEncoder.matches(mDto.getM_pwd(), encPwd)) {
				//비밀번호 가 맞는 경우 회원 정보(아이디, 이름, 포인트, 등급이름 가져오기)
				mDto = mDao.selectMember(mDto.getM_id());//회색으로 뜨는 경우 그 내용물이 다르다는 뜻

				//회원 정보를 세션에 저장. index 애서 지정한 member. index 의 맴버를 바꿀 경우 똑같이 다 뱌꿔야 함
				session.setAttribute("member", mDto);

				//로그인 성공 후 게시판 목록 페이지 이동.
				view = "redirect:boardList?pageNum=1";//첫페이지로 이동
				msg = "로그인 성공";
			} else {//로그인 실패 - 비밀번호를 틀린 경우
				view = "redirect:loginForm";//로그인 화면으로 돌아감
				msg = "비밀번호가 틀립니다";
			}
		} else {//로그인 실패 - 회원이 아닐 경우
			view = "redirect:loginForm";
			msg = "존재하지 않는 아이디입니다";
		}
		rttr.addFlashAttribute("msg", msg);//화면으로 메세지 보내기
		return view;
	}

	//로그아웃 처리
	public String logout(HttpSession session) {
		log.info("logout()");
		session.invalidate();//세션 무효화
		return "redirect:/";//첫페이지로 이동
	}
}