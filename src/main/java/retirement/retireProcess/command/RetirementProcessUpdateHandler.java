package retirement.retireProcess.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import retirement.model.RetirementProcessModel;
import retirement.retireProcess.service.RetirementProcessUpdateService;

public class RetirementProcessUpdateHandler implements CommandHandler {

    private RetirementProcessUpdateService updateService = new RetirementProcessUpdateService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        
        // 모달창에서 전송(POST)된 파라미터 수신
    	// モーダルウィンドウから送信(POST)されたパラメータを受信
        RetirementProcessModel model = new RetirementProcessModel();
        model.setEmployeeNo(req.getParameter("employeeNo"));
        model.setRetirementTypeCode(req.getParameter("retirementTypeCode"));
        model.setResignDate(req.getParameter("resignDate"));
        model.setRetirementReason(req.getParameter("retirementReason"));
        model.setPostRetirementPhone(req.getParameter("postRetirementPhone"));

        //  퇴직 처리 업데이트 로직 실행
        // 退職処理のアップデートロジックを実行
        updateService.processRetirement(model);

        //  처리가 끝난 후 다시 목록 조회 페이지로 리다이렉트 (새로고침 방지)
        // 処理が終了した後、再びリスト照会ページにリダイレクト (リロード防止)
        res.sendRedirect(req.getContextPath() + "/Retire/retireProcess.do?save=success");
        
        return null; 
    }
}