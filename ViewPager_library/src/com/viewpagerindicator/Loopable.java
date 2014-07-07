package com.viewpagerindicator;

public interface Loopable {
	/**
	 * 
	 * @return 아이템의 진짜 개수
	 */
	int getRealCount();
	
	/**
	 * 
	 * @param fakePos 가짜 위치값(루프를 위하여 굉장히 많은 페이지를 만듬, fakePos는 pos의 배수)
	 * @return 진짜 아이템 위치 계산값 
	 */
	
	int getRealPos(int fakePos);
	
	/**
	 * 
	 * @return 진짜 아이템의 현재 위치 값
	 */
	
	int getRealCurPos();
}
