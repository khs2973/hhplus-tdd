package io.hhplus.tdd.point;

import org.springframework.stereotype.Service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointService {
	
	private UserPointTable userPointTable;
	
	private PointHistoryTable pointHistoryTable;
	
	// 사용자 포인트 조회
	public UserPoint getUserPoint(Long userId) {
		return userPointTable.selectById(userId);
	}
	
	// 사용자 포인트 충전
	public UserPoint charge(long id, long amount) {
		
		long maxPoint = 5000;
		UserPoint userPoint = userPointTable.selectById(id);
		
		if(userPoint.point() + amount > maxPoint) {
			throw new IllegalArgumentException("포인트 최대 충전량을 초과하셨습니다.");
		}
		
		UserPoint chargeUserPoint = userPointTable.insertOrUpdate(id, userPoint.point() + amount);
		
		pointHistoryTable.insert(id, userPoint.point() + amount, TransactionType.CHARGE, System.currentTimeMillis());
		
		return chargeUserPoint;
		
	}
	
}
