package io.hhplus.tdd.point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
public class PointServiceTest {

	/**
	 * 1. 총 4가지 기능 ( 포인트 조회 / 포인트 충전 / 포인트 사용 / 포인트 내역 조회 ) 작성 여부
	 * 2. 각 기능들에 대해 예외케이스를 검증하는 단위 테스트 작성 여부 ( 최소 스펙 - 정책 관련한 실패 케이스 )
	 * 3. 각 기능별 통합테스트 작성 여부 ( 최소 스펙 - 성공하는지 )
	 * 4. 포인트 관련 기능에 대한 동시성 이슈 제어 및 검증하는 통합 테스트 작성 여부
	 * **/

	@Mock
	private UserPointTable userPointTable;
	
	@Mock
	private PointHistoryTable pointHistoryTable;
	
	@Test
	@DisplayName("사용자 포인트 조회")
	public void searchUser() {
		
		// given
		long id = 1L;
		UserPoint userPoint = new UserPoint(id, 1000, System.currentTimeMillis());
		when(userPointTable.selectById(id)).thenReturn(userPoint);
		
		// when
		UserPoint result = userPointTable.selectById(id);
		
		// then
		assertEquals(1000, result.point());
		
	}
	
	
}
