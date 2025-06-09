package com.example.demo.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.example.demo.exception.BadNoteException;
import com.example.demo.util.BadWordFilter;

@Aspect
@Component
public class CheckNotesAspect {

	@Before("@annotation(CheckNotes)")
	public void checkNotes(JoinPoint joinPoint) {
		for (Object arg : joinPoint.getArgs()) {
			try {
				// 嘗試呼叫 getNotes() 方法
				Method getNotesMethod = arg.getClass().getMethod("getNotes");
				Object notesValue = getNotesMethod.invoke(arg);

				if (notesValue instanceof String notesStr) {
					// ✅👉 加在這裡！印出目前檢查的備註內容
					System.out.println("🔍 AOP 正在檢查 notes: " + notesStr);

					// 髒話判斷
					if (BadWordFilter.containsBadWord(notesStr)) {
						throw new BadNoteException("備註含有不當字詞，請重新輸入");
					}
				}

			} catch (NoSuchMethodException e) {
				// 沒有 getNotes()，略過
			} catch (Exception e) {
				throw new RuntimeException("AOP 備註檢查失敗: " + e.getMessage());
			}
		}
	}
}
