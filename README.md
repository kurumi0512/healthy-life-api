# Healthy Life API - 健康管理後端系統

本專案為《體重與健康 AI 追蹤系統》的後端服務，使用 **Spring Boot** 架構，提供會員登入、健康紀錄 CRUD 操作、BMI 計算、AI 建議生成等 RESTful API。

前端專案請見：[Healthy Life UI（React）](https://github.com/kurumi0512/healthy-life-ui)

---

## 技術架構

- Spring Boot 
- RESTful API 設計
- MySQL 資料庫
- Spring Data JPA（資料存取）
- Bean Validation（欄位驗證）
- Spring Security（會員登入驗證）
- Email 驗證機制
- OpenAI 介接：整合本地部署的 **Ollama Gemma3 模型**
- SSE 串流傳輸（AI 回覆即時顯示）
- ModelMapper（DTO ↔ Entity 映射）

---

## 專案模組

- `Account`：帳號註冊、登入、忘記密碼、Email 驗證
- `User`：使用者基本資料
- `WeightRecord`：體重記錄與 BMI 計算
- `BloodPressureRecord`：血壓記錄
- `BloodSugarRecord`：血糖記錄
- `Advice`：串接 AI 模型，產生個人化建議
- `Food`：食物營養資料表
-`FoodLimit`：食物建議攝取上限表

---

## 專題簡介

這是一套協助使用者追蹤健康數據的系統，提供：

- 一鍵填入紀錄功能，加快輸入效率
- 趨勢圖表與分類提示
- 根據變化自動顯示互動訊息（如灑花、鼓勵）
- 串接 AI 模型，根據使用者 BMI 給予飲食與運動建議
- 提供歷史建議紀錄查詢與選擇

---

## 相關連結

- 前端專案（React）：[healthy-life-ui](https://github.com/kurumi0512/healthy-life-ui)
- 專案簡報與截圖：https://www.canva.com/design/DAGoy0TjKa0/jpDxnkWfQ6EU-NqTkrNijg/view?utm_content=DAGoy0TjKa0&utm_campaign=designshare&utm_medium=link2&utm_source=uniquelinks&utlId=hfad3a53be3
- 開發者：羅筱筑
