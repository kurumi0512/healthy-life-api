package com.example.demo.service;

//健康建議相關功能的服務介面（用於整合 AI 模型產生與建議儲存）
public interface HealthAdviceService {

	// 產生基本模式下的 prompt 給 AI 模型（含身高、體重、年齡、目標）
	String generatePrompt(double height, double weight, int age, String goal);

	// 產生指定模式的 prompt，例如區分「飲食」、「運動」、「蛋白質建議」等用途
	String generatePrompt(double height, double weight, int age, String goal, String mode);

	// 儲存一筆建議紀錄（含輸入內容與 AI 回覆結果）
	void saveAdviceRecord(Integer userId, String input, String advice);

	// 呼叫 AI 模型，根據身高、體重、年齡與目標取得健康建議（基本模式）
	String getAdvice(double height, double weight, int age, String goal);

	// 呼叫 AI 模型，根據身高、體重、年齡、目標與模式取得進階建議
	String getAdvice(double height, double weight, int age, String goal, String mode);

	// 判斷某個詞是否應顯示（用於 AI 回覆中的特殊處理，如排除 think block 區塊）
	boolean shouldDisplayWord(String word, boolean[] insideThinkBlock);
}