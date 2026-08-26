package config.payitemset.service;

import java.sql.SQLException;

/**
 * 지급·공제항목 설정 화면의 업무 예외이다.
 * 支給・控除項目設定画面の業務例外である。
 */
public class PayItemSetException extends RuntimeException {

	public PayItemSetException(String message) {
		super(message);
	}

	public PayItemSetException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * DB 오류 코드를 사용자 메시지로 바꾼다.
	 * DBエラーコードをユーザーメッセージに変える。
	 */
	public static PayItemSetException fromSql(SQLException e) {
		int code = e.getErrorCode();
		if (code == 2292) {
			return new PayItemSetException("給与明細で使用されている項目は削除できません。", e);
		}
		if (code == 2291) {
			return new PayItemSetException("非課税項目の選択が正しくありません。", e);
		}
		if (code == 12899) {
			return new PayItemSetException("入力値が長すぎます。", e);
		}
		if (code == 1) {
			return new PayItemSetException("同じ名前の項目がすでに登録されています。", e);
		}
		return new PayItemSetException("保存に失敗しました。", e);
	}

}
