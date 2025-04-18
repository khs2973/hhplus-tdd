package io.hhplus.tdd.point;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointService {
	
	private final UserPointTable userPointTable;
	
	private final PointHistoryTable pointHistoryTable;
	
	private final ConcurrentHashMap<Long, ReentrantLock> reentrantLock = new ConcurrentHashMap<>();

	private ReentrantLock getLock(long userId) {
		// key가 존재할 경우: 아무런 작업을 하지 않고 기존에 존재하는 key의 value를 리턴
		// key가 존재하지 않는 경우: 람다식을 적용한 값을 해당 key에 저장한 후 newValue를 리턴
		return reentrantLock.computeIfAbsent(userId, id -> new ReentrantLock());
	}
	
	// 사용자 포인트 조회
	public UserPoint point(Long userId) {
		return userPointTable.selectById(userId);
	}
	
	// 사용자 포인트 충전
	public UserPoint charge(long id, long amount) {
		
		ReentrantLock lock = getLock(id);
		
		lock.lock();
		
		try {
			
			long maxPoint = 5000L;
			UserPoint userPoint = userPointTable.selectById(id);
			
			if(userPoint.point() + amount > maxPoint) {
				throw new IllegalArgumentException("포인트 최대 충전량을 초과하셨습니다.");
			}
			
			UserPoint chargeUserPoint = userPointTable.insertOrUpdate(id, userPoint.point() + amount);
			
			pointHistoryTable.insert(id, userPoint.point() + amount, TransactionType.CHARGE, System.currentTimeMillis());
			
			return chargeUserPoint;
			
		} finally {
			// unlock은 예외와 상관없이 무조건 실행되도록 설정
			// lock이 해제되지 않으면 deadlock 발생
			lock.unlock();
		}
		
	}
	
	// 사용자 포인트 사용
	public UserPoint use(long id, long amount) {
		
		UserPoint userPoint = userPointTable.selectById(id);
		
		long currentPoint = userPoint.point();
		
		if(amount > currentPoint) {
			throw new IllegalArgumentException("포인트가 부족합니다.");
		}
		
		if(amount < 0) {
			throw new IllegalArgumentException("포인트는 0미만으로 쓸 수 없습니다.");
		}
		
		UserPoint useUserPoint = userPointTable.insertOrUpdate(id, currentPoint - amount);
		
		pointHistoryTable.insert(id, currentPoint - amount, TransactionType.USE, System.currentTimeMillis());
		
		return useUserPoint;
		
	}
	
	// 사용자 포인트 사용 내역 조회
	public List<PointHistory> history(long id) {
		return pointHistoryTable.selectAllByUserId(id);
	}
	
}
