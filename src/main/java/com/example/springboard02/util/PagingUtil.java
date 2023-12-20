package com.example.springboard02.util;

import lombok.AllArgsConstructor;

@AllArgsConstructor//모든 멤버 변수의 생성자 자동 생성. 아래의 코드를 자동 생성
//public PagingUtil(int maxNum...){
//  this.maxNum=maxNum;
//  ....}
public class PagingUtil {
	//maxNum=전체 게시글 개수
	//pageNum=페이지 번호
	//listCnt=한 페이지에서 보여질 게시글의 개수
	//pageCnt=보여질 페이지의 개수
	//listName=게시판이 여러개일 경우 페이징을 처리할 게시판의 이름 및 검색에 따라 달라 지는 url을 저장
	private int maxNum, pageNum, listCnt, pageCnt;
	private String listName;

	//페이징 용 url와 링크 <a>로 구성된 html 태그 문장을 작성하는 메소드
	public String makePaging() {
		String page = null;//html 태그 문장을 저장하는 변수
		StringBuffer sb = new StringBuffer();//StringBuffer=문자열 가공에 유용한 도구 객체
		//1.전체 페이지 개수 구하기
		int totalPage = (maxNum % listCnt) > 0 ? maxNum / listCnt + 1 : maxNum / listCnt;
		if (totalPage == 0) totalPage = 1;//게시글이 0일 때는 페이지 번호를 1로 출력

		//2.현재 페이지가 속한 페이지 번호 그룹 구하기
		int curGroup = (pageNum % pageCnt) > 0 ? pageNum / pageCnt + 1 : pageNum / pageCnt;

		//3.현재 보이는 번호 그룹의 시작 번호 구하기
		int start = (curGroup * pageCnt) - (pageCnt - 1);

		//4.현재 보이는 번호 그룹의 마지막 번호 구하기
		int end = (curGroup % pageCnt) >= totalPage ? totalPage : curGroup * pageCnt;

		//5.'이전' 버튼 처리. start가 1인 경우 불필요. 그 외엔 활성화
		if (start != 1) sb.append("<a class='pno' href=' /" + listName + "pageNum=" + (start - 1) + " '>◀</a>");
		//2번 그룹이고 start가 4이고 pageCnt가 3일 경우
		//<a class='pno' href='/boardList?pageNum=3'>이전</a>

		//중간 번호 버튼. 현재 보이는 페이지에는 링크를 붙이지 않음
		for (int i = start; i <= end; i++) {
			//현재 보이는 페이지가 아닌 번호 버튼
			if (pageNum != i) sb.append("<a class='pno' href=' /" + listName + "pageNum=" + i + " '>" + i + "</a>");
				//현재 보이는 페이지 번호
			else sb.append("<font class='cno' style='color: red'>" + i + "</font>");
			//start가 1, pageCnt가 3, i는 1~3, 현재 페이지 2.
			//<a class='pno' href='/boardList?pageNum=1'>1</a>
			//<font class='pno' style='color: red'>2</a><!--현재 페이지엔 링크를 걸지 않고 글자만 표시-->
			//<a class='pno' href='/boardList?pageNum=3'>3</a>
		}
		//'다음' 버튼 처리. end 값이 totalPage와 같으면 생성 X
		if (end != totalPage) sb.append("<a class='pno' href=' /" + listName + "pageNum=" + (end + 1) + " '>▶</a>");
		//pageCnt 3, end 6 -> 다음 번호 그룹의 시작번호 7
		//<a class='pno' href='/boardList?pageNum=7'>다음</a>

		page = sb.toString();//StringBuffer -> String 전환
		return page;
	}//makePaging() end
}//class end