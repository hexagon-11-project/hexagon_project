package config.employee.command;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

// 파라미터 가로채서 인덱스 바꿔치기 해주는 래퍼 클래스
// JSP 화면 코드는 전혀 손 안 대고, 중간에서 번호만 싹 당겨서 맞춰줌
// パラメータを横取りしてインデックスをすり替えるラッパークラス
// JSPの画面コードには全く手を出さず、中間で番号だけを前に詰めて合わせてあげる
public class RowShiftRequestWrapper extends HttpServletRequestWrapper {

    private final String keyPrefix;               // 예: "family"
    private final Map<Integer, Integer> indexMap;  // 새 번호 -> 원래 번호

    public RowShiftRequestWrapper(HttpServletRequest request, String keyPrefix, Map<Integer, Integer> indexMap) {
        super(request);
        this.keyPrefix = keyPrefix;
        this.indexMap = indexMap;
    }

    // JSP에서 request.getParameter("familyName3") 하고 꺼낼 때 여기를 탐.
    // 끝에 붙은 숫자(새 인덱스)만 똑 떼서 맵에서 원래 번호를 찾은 다음, "진짜" 파라미터 값으로 바꿔치기해서 던져줌
    // JSPで request.getParameter("familyName3") と取得する時にここを走る。
    // 末尾の数字（新しいインデックス）だけを切り取ってマップから元の番号を探し、「本当の」パラメータ値にすり替えて返す
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
                
                // 맵에 없으면 남는 줄이니까 그냥 쿨하게 null 뱉음
                // マップになければ余った行なので、クールにnullを返す
                if (originalIndex == null) {
                    return null; // 남은 행 개수보다 뒤쪽 번호는 빈 값
                }
                return super.getParameter(keyPrefix + fieldPart + originalIndex);
            }
        }
        // 내가 관심 없는 파라미터면 얌전히 부모(원래 request)한테 토스함
        // 自分が関心のないパラメータなら、大人しく親（元のrequest）にトスする
        return super.getParameter(name);
    }
}