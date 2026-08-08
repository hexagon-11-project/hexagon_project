package config.employee.command;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 사원등록1/2 페이지의 "부양가족/학력/경력/자격면허/교육훈련/상벌/발령" 같은
 * 여러 줄짜리 표에서 [추가]/[선택삭제] 버튼을 처리하는 공통 로직.
 * 이 클래스 하나로 7개 테이블이 다 같은 방식으로 동작한다.
 */
public class RowFormUtil {

    private RowFormUtil() {}

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
    public static void addRow(HttpServletRequest request, String rowCountParam, int minRows) {
        int currentCount = parseIntOrDefault(request.getParameter(rowCountParam), minRows);
        request.setAttribute(rowCountParam, currentCount + 1);
    }

    /**
     * [선택삭제] 처리: 체크된 행을 뺀 나머지 행들을 1번부터 다시 채번해서,
     * RowShiftRequestWrapper로 감싼 request를 가지고 handler가 직접 forward까지 수행한다.
     * (그래서 이 메서드를 부르고 나면 그냥 return null 하면 됨 — Controller가 또 forward하면 안 되므로)
     */
    public static void forwardWithDeletedRows(HttpServletRequest request, HttpServletResponse response,
            String formView, String tableKey, String rowCountParam, String delFieldPrefix, int minRows)
            throws Exception {

        int currentCount = parseIntOrDefault(request.getParameter(rowCountParam), minRows);

        Map<Integer, Integer> indexMap = new LinkedHashMap<>();
        int newIndex = 1;
        for (int i = 1; i <= currentCount; i++) {
            boolean checked = "on".equals(request.getParameter(delFieldPrefix + i));
            if (!checked) {
                indexMap.put(newIndex, i);
                newIndex++;
            }
        }

        int newCount = Math.max(indexMap.size(), minRows); // 최소 줄 수는 유지 (다 지워도 빈 줄 하나는 남김)
        request.setAttribute(rowCountParam, newCount);

        RowShiftRequestWrapper wrapped = new RowShiftRequestWrapper(request, tableKey, indexMap);
        request.getRequestDispatcher(formView).forward(wrapped, response);
    }
}
