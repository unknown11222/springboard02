package com.example.springboard02.service;

import com.example.springboard02.dao.*;
import com.example.springboard02.dto.*;
import com.example.springboard02.util.PagingUtil;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;

@Service
@Slf4j
public class BoardService {
	@Autowired
	private BoardDao bDao;
	@Autowired
	MemberDao mDao;

	//트랜젝션 관련 객체 선언
	@Autowired
	private PlatformTransactionManager manager;
	@Autowired
	private TransactionDefinition definition;

	private int lcnt = 5;//페이지 기본값

	//게시글 가져 오기
	public ModelAndView getBoard(int b_num) {
		log.info("getBoard");
		ModelAndView mv = new ModelAndView();

		//게시글 번호에 해당하는 글을 볼때마다 조회수 1 상승
		bDao.riseView(b_num);

		//게시글 번호로 선택한 게시물 가져오기
		BoardDto bDto = bDao.selectBoard(b_num);
		mv.addObject("board", bDto);

		//게시글의 파일목록 가져오기
		List<BoardFileDto> bfList = bDao.selectFileList(b_num);
		mv.addObject("bfList", bfList);

		//게시글의 댓글목록 가져오기
		List<ReplyDto> rList = bDao.selectReplyList(b_num);
		mv.addObject("rList", rList);

		mv.setViewName("boardDetail");
		return mv;
	}

	//글 목록 가져오기
	public ModelAndView getBoardList(SearchDto sDto, HttpSession session) {
		log.info("getBoardList()");
		ModelAndView mv = new ModelAndView();
		//DB 에서 게시글 가져오기
		int num = sDto.getPageNum();

		//출력할 게시물의 수가 설정되지 않으면 기본값 5로 설정
		if (sDto.getListCnt() == 0) sDto.setListCnt(lcnt);

		//pageNum 을 limit 시작 번호로 변경
		sDto.setPageNum((num - 1) * sDto.getListCnt());

		//Dao로 게시글 목록 가져오기
		List<BoardDto> bList = bDao.selectBoardList(sDto);

		//DB에서 가져온 데이터를 mv에 넣기
		mv.addObject("bList", bList);

		//페이징 처리
		sDto.setPageNum(num);//원래 페이지 번호로 환원
		String pageHtml = getPaging(sDto);
		mv.addObject("paging", pageHtml);

		//페이지 번호와 검색 관련 내용을 세션에 저장
		if (sDto.getColname() != null) session.setAttribute("sDto", sDto);
		else session.removeAttribute("sDto");//검색이 아닐 때 제거

		//세션에 SearchDto 를 저장. 글쓰기, 상세보기 화면에서 목록으로
		session.setAttribute("pageNum", num);

		mv.setViewName("boardList");
		return mv;
	}

	private String getPaging(SearchDto sDto) {
		String pageHtml = null;
		int maxNum = bDao.selectBoardCnt(sDto);//전체 글 개수 구하기
		int pageCnt = 10;//페이지 번호 개수
		//링크용 url:기본 - boardList?
		//검색 - "boardList?colname=b_title&keyword=4&"
		String listname = "boardList?";//링크 url

		//검색 시의 url
		if (sDto.getColname() != null)
			listname += "colname" + sDto.getColname() + "&keyword=" + sDto.getKeyword() + "&";

		//페이징 처리용 객체 생성
		PagingUtil paging = new PagingUtil(maxNum, sDto.getPageNum(), sDto.getListCnt(), pageCnt, listname);
		pageHtml = paging.makePaging();

		return pageHtml;
	}

	//글 작성
	public String boardWrite(List<MultipartFile> files, BoardDto bDto, HttpSession session, RedirectAttributes rttr) {
		log.info("boardWrite()");
		//트랜젝션
		TransactionStatus status = manager.getTransaction(definition);
		String view = null, msg = null;
		try {
			bDao.insertBoard(bDto);//글 내용 저장
			//log.info("게시글 번호:"+bDto.getB_num());

			//파일 저장(파일 정보 저장)
			fileUpload(files, session, bDto.getB_num());

			//작성자 point 설정
			MemberDto mDto;
			mDto = (MemberDto) session.getAttribute("member");
			int point = mDto.getM_point() + 10;
			if (point > 100) point = 100;//point의 상한선이 100이기 때문에, 100을 넘지 않도록 필터링.
			// 안하면 100을 초과할 시 등급이 표시가 안됨(오버 플로)
			mDto.setM_point(point);
			mDao.updateMemberPoint(mDto);

			//세션에 새 정보 저장
			mDto = mDao.selectMember(mDto.getM_id());
			session.setAttribute("member", mDto);
			manager.commit(status);

			//세션에 같은 이름으로 덮어쓰기
			view = "redirect:boardList?pageNum=1";//첫번쩨 페이지로 돌아감
			msg = "작성 성공";
		} catch (Exception e) {
			e.printStackTrace();
			manager.rollback(status);//취소
			view = "redirect:writeForm";
			msg = "작성 실패";
		}
		rttr.addFlashAttribute("msg", msg);
		return view;
	}//boardWrite end

	private void fileUpload(List<MultipartFile> files, HttpSession session, int bNum) throws Exception {
		//이 메소드의 예외 처리(파일 저장 실패, 파일 정보 저장 실패)를 호출한 메소드에 넘겨서 처리하도록 throws 사용
		log.info("fileUpload()");

		//파일 저장 위치 처리:세션에서 위치 정보를 구함
		String realPath = session.getServletContext().getRealPath("/");
		log.info("realPath");
		realPath += "upload/";//파일 업로드용 폴더. 업로드용 폴더가 없으면 자동 생성
		File folder = new File(realPath);//폴더를 메모리에 만듬

		//isDirectory()=폴더의 존재유무를 확인하는 메소드. 있으면 true, 없거나 폴더가 아니면 false
		if (folder.isDirectory() == false) folder.mkdir();//MaKe DIRectory(폴더)
		for (MultipartFile mf : files) {
			//파일명(원래 이름) 추출
			String oriname = mf.getOriginalFilename();
			if (oriname.equals("")) return;//업로드할 파일 없음. 파일 저장 종료

			BoardFileDto bfDto = new BoardFileDto();
			bfDto.setBf_bnum(bNum);//게시글 번호 저장
			bfDto.setBf_oriname(oriname);//원래 파일명 저장
			String sysname = System.currentTimeMillis() + oriname.substring(oriname.lastIndexOf("."));
			//file.jpg->1234231423.jpg .jpg를 잘라내고 변경한 이름에 .jpg를 붙임
			bfDto.setBf_sysname(sysname);

			//파일 저장(upload 폴더)
			File file = new File(realPath + sysname);
			//......./.../.../webapp/upload/1234231423.jpg
			mf.transferTo(file);//하드디스크에 저장

			//파일 정보 저장(DB)
			bDao.insertFile(bfDto);
		}
	}

	public ResponseEntity<Resource> fileDownload(BoardFileDto bfDto, HttpSession session) throws IOException {
		log.info("fileDownload()");
		//파일 저장 경로 및 파일명 조합
		String realPath = session.getServletContext().getRealPath("/");
		realPath += "upload/" + bfDto.getBf_sysname();

		//실제 파일이 저장된 하드디스크 까지의 경로 realPath=연결할 파일
		InputStreamResource fResource = new InputStreamResource(new FileInputStream(realPath));

		//파일명이 한글일 경우에만 처리(utf-8로 인코딩)
		String fileName = URLEncoder.encode(bfDto.getBf_oriname(), "UTF-8");

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
				.cacheControl(CacheControl.noCache())
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
				.body(fResource);
	}

	//게시글 삭제(파일목록, 댓글목록 함께 삭제)
	public String deleteBoard(int b_num, RedirectAttributes rttr, HttpSession session) {
		log.info("deleteBoard()");
		TransactionStatus status = manager.getTransaction(definition);//트렌젝션 처리
		String view = null, msg = null;
		try {
			//0.파일 삭제 목록 구하기
			List<String> fList = bDao.selectFnameList(b_num);
			//1.파일 목록 삭제
			bDao.deleteFiles(b_num);
			//2.댓글 목록 삭제
			bDao.deleteReplys(b_num);
			//3.게시글 삭제(외래키로 참조되고 있기 때문에 마지막에 지움)
			bDao.deleteBoard(b_num);
			//파일 삭제(첨부된 파일이 있을때만)
			if (fList.size() != 0) deleteFiles(fList, session);
			manager.commit(status);//트랜젝션 최종 승인
			view = "redirect:boardList?pageNum=1";
			msg = "삭제 성공";
		} catch (Exception e) {
			e.printStackTrace();
			manager.rollback(status);//예외 발생 시 되돌림
			view = "redirect:boardDetail?b_num=" + b_num;
			msg = "삭제 실패";
		}
		rttr.addFlashAttribute("msg", msg);
		return view;
	}

	//파일 삭제
	private void deleteFiles(List<String> fList, HttpSession session) throws Exception {
		log.info("deleteFiles()");
		//파일 위치
		String realPath = session.getServletContext().getRealPath("/");
		realPath += "upload/";

		for (String sn : fList) {
			File file = new File(realPath + sn);
			if (file.exists() == true)//file.exists()=파일 존재 유뮤
				file.delete();//파일 삭제
		}
	}

	//글 수정 처리(mv)
	public ModelAndView updateBoard(int b_num) {
		log.info("updateBoard()");
		ModelAndView mv = new ModelAndView();
		//게시글 내용 가져오기
		BoardDto bDto = bDao.selectBoard(b_num);
		//파일 목록 담아오기
		List<BoardFileDto> fList = bDao.selectFileList(b_num);
		//mv에 담기
		mv.addObject("board", bDto);
		mv.addObject("fList", fList);
		//탬플릿 지정
		mv.setViewName("updateForm");
		return mv;
	}

	//글 수정 처리
	public String updateBoard(List<MultipartFile> files, BoardDto bDto, HttpSession session, RedirectAttributes rttr) {
		log.info("updateBoard()");
		TransactionStatus status = manager.getTransaction(definition);
		String view = null, msg = null;

		try {
			bDao.updateBoard(bDto);
			fileUpload(files, session, bDto.getB_num());
			manager.commit(status);
			view = "redirect:boardDetail?b_num=" + bDto.getB_num();
			msg = "수정 성공";
		} catch (Exception e) {
			e.printStackTrace();
			manager.rollback(status);
			view = "redirect:updateForm?b_num=" + bDto.getB_num();
			msg = "수정 실패";
		}
		rttr.addFlashAttribute("msg", msg);
		return view;
	}

	public List<BoardFileDto> delFile(BoardFileDto bfDto, HttpSession session) {
		log.info("delFile");
		List<BoardFileDto> fList = null;

		//파일 경로 설정
		String realPath = session.getServletContext().getRealPath("/");
		realPath += "upload/" + bfDto.getBf_sysname();

		try {
			//파일 삭제
			File file = new File(realPath);
			if (file.exists()) {
				if (file.delete()) {
					//해당 파일 정보 삭제(DB)
					bDao.deleteFile(bfDto.getBf_sysname());
					//나머지 파일 목록 다시 가져오기
					fList = bDao.selectFileList(bfDto.getBf_bnum());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fList;
	}

	//댓글 작성
	public ReplyDto replyInsert(ReplyDto rDto) {
		log.info("replyInsert()");
		try {
			bDao.insertReply(rDto);
			rDto = bDao.selectLastReply(rDto.getR_num());
		} catch (Exception e) {
			e.printStackTrace();
			rDto = null;
		}
		return rDto;
	}
}//class end