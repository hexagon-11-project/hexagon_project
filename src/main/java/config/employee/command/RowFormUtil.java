package config.employee.command;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// 동적 테이블 행(Row) 추가/삭제 처리 유틸
// 動的テーブルの行（Row）追加・削除処理ユーティリティ
public class RowFormUtil {

    private RowFormUtil() {}

    // 파라미터값 안전하게 int로 바꿔줌 (null이거나 숫자 아니면 세팅해둔 디폴트값 뱉음)
    // パラメータの値を安全にintに変換する（nullまたは数値でなければ設定したデフォルト値を返す）
    public static int parseIntOrDefault(String value, int defaultValue) {
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /** [추가] 처리: 현재 줄 수를 하나 늘린 값을 request attribute로 세팅한다. (호출한 쪽에서 FORM_VIEW를 return 하면 됨) */
    // [추가] 버튼 눌렀을 때 타는 로직. 현재 줄 수 가져와서 단순히 +1 해줌
    // [追加] ボタンを押した時に走るロジック。現在の行数を取得して単純に+1する
    public static void addRow(HttpServletRequest request, String rowCountParam, int minRows) {
        int currentCount = parseIntOrDefault(request.getParameter(rowCountParam), minRows);
        request.setAttribute(rowCountParam, currentCount + 1);
    }

    /**
     * [선택삭제] 처리: 체크된 행을 뺀 나머지 행들을 1번부터 다시 채번해서,
     * RowShiftRequestWrapper로 감싼 request를 가지고 handler가 직접 forward까지 수행한다.
     * (그래서 이 메서드를 부르고 나면 그냥 return null 하면 됨 — Controller가 또 forward하면 안 되므로)
     */
    // [선택삭제] 버튼 눌렀을 때 타는 로직. 
    // 삭제 체크된 애들 날리고, 살아남은 애들끼리 인덱스 번호 1번부터 예쁘게 다시 매겨서 포워딩까지 한 번에 쳐줌
    // [選択削除] ボタンを押した時に走るロジック。
    // 削除チェックされたものを飛ばし、生き残ったものだけでインデックス番号を1番から綺麗に振り直して、フォワードまで一気に処理する
    public static void forwardWithDeletedRows(HttpServletRequest request, HttpServletResponse response,
            String formView, String tableKey, String rowCountParam, String delFieldPrefix, int minRows)
            throws Exception {

        int currentCount = parseIntOrDefault(request.getParameter(rowCountParam), minRows);

        Map<Integer, Integer> indexMap = new LinkedHashMap<>();
        int newIndex = 1;
        for (int i = 1; i <= currentCount; i++) {
            boolean checked = "on".equals(request.getParameter(delFieldPrefix + i));
            // 체크 안 된(살아남은) 데이터만 모아서 새 인덱스 부여
            // チェックされていない（生き残った）データだけを集めて新しいインデックスを付与
            if (!checked) {
                indexMap.put(newIndex, i);
                newIndex++;
            }
        }

        // 싹 다 지워도 화면 폼 깨지지 않게 최소 줄 수는 유지되도록 방어코드 작성
        // 全て消しても画面のフォームが崩れないよう、最小の行数は維持されるように防御コードを作成
        int newCount = Math.max(indexMap.size(), minRows); // 최소 줄 수는 유지 (다 지워도 빈 줄 하나는 남김)
        request.setAttribute(rowCountParam, newCount);

        RowShiftRequestWrapper wrapped = new RowShiftRequestWrapper(request, tableKey, indexMap);
        request.getRequestDispatcher(formView).forward(wrapped, response);
    }
}