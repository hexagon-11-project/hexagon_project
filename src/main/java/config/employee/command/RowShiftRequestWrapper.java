package config.employee.command;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * [선택삭제] 처리 전용 헬퍼.
 *
 * 예: family1~family4 중 2번을 삭제하면, 화면은 다시 1~3번으로 채워져야 한다.
 * 근데 실제로 지워진 건 없고 "3번 자리에 원래 4번 데이터를 보여준다"는 매핑만 있으면 되므로,
 * request.getParameter()를 가로채서 새 번호(newIndex)로 물어보면 원래 번호(originalIndex)의
 * 값을 대신 돌려주는 방식으로 처리한다. 이렇게 하면 JSP 쪽 코드는 家 손댈 필요가 없다
 * (항상 그냥 1..N번으로 물어보기만 하면 됨).
 */
public class RowShiftRequestWrapper extends HttpServletRequestWrapper {

    private final String keyPrefix;               // 예: "family"
    private final Map<Integer, Integer> indexMap;  // 새 번호 -> 원래 번호

    public RowShiftRequestWrapper(HttpServletRequest request, String keyPrefix, Map<Integer, Integer> indexMap) {
        super(request);
        this.keyPrefix = keyPrefix;
        this.indexMap = indexMap;
    }

    @Override
    public String getParameter(String name) {
        if (name.startsWith(keyPrefix)) {
            String rest = name.substring(keyPrefix.length());
            int cut = rest.length();
            while (cut > 0 && Character.isDigit(rest.charAt(cut - 1))) {
                cut--;
            }
            if (cut < rest.length()) { // 끝이 숫자로 끝나는 파라미터인 경우만 (예: familyName3)
                String fieldPart = rest.substring(0, cut);
                int newIndex = Integer.parseInt(rest.substring(cut));
                Integer originalIndex = indexMap.get(newIndex);
                if (originalIndex == null) {
                    return null; // 남은 행 개수보다 뒤쪽 번호는 빈 값
                }
                return super.getParameter(keyPrefix + fieldPart + originalIndex);
            }
        }
        return super.getParameter(name);
    }
}
