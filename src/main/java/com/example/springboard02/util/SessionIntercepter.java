package com.example.springboard02.util;
import jakarta.servlet.http.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.*;
@Component
@Slf4j
public class SessionIntercepter implements AsyncHandlerInterceptor {
	@Override//로그인 시점에 처리할 내용을 작성
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		log.info("preHandle()");
		//세션에 로그인 정보가 있는 지 확인
		HttpSession session=request.getSession();

		if (session.getAttribute("member")==null){
			//로그인을 안한 상태
			log.info("인터셉트!");
			//로그인 정보가 있으면 요청 페이지로, 없으면 첫 페이지로 이동
			response.sendRedirect("/");//첫페이지로 이동
			return false;//승인 안함
		}
		return true;//승인
	}

	@Override//로그아웃 후 처리
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
	                       Object handler, ModelAndView modelAndView) throws Exception {
		log.info("postHandle()");
		//로그아웃 후 브라우저의 back 버튼으로 인터셉트 대상 페이지로 들어가는 것을 막기. 막는 방식은 사용자 컴퓨터의 캐시를 제거.
		//현재 사용하는 http 버전은 1.1과 1.0 혼용
		if (request.getProtocol().equals("HTTP/1.1")){//1.1 버전 캐시 제거
			response.setHeader("Cache-Controller", "no-cache, no-store, must-revalidate");
		}
		else {//1.0 버전 캐시 제거
			response.setHeader("Pragma", "no-cache");
		}
	}
}