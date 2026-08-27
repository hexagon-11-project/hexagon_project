package person.certificatePrintWorking.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import person.certificatePrintWorking.service.CertificatePrintWorkingInsertService;
import person.model.CertificatePrintWorkingModel;

public class CertificatePrintWorkingInsertHandler implements CommandHandler {
	private CertificatePrintWorkingInsertService service = new CertificatePrintWorkingInsertService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        
        // GET 방식 요청은 차단
    	// GET方式の要求はブロック
        if (req.getMethod().equalsIgnoreCase("GET")) {
            return "/WEB-INF/pages/person/certificatePrintWorking.jsp"; 
        }

        String employeeNo = req.getParameter("employeeNo"); 
        String certificateTypeCode = req.getParameter("certificateTypeCode");
        // 화면에서 넘어오는 값 대신, 겹치지 않는 고유한 발급번호를 서버에서 직접 생성
        // 画面から渡される値の代わりに、重複しない固有の発行番号をサーバーで直接生成
        String issueNo = "ISSUE-" + System.currentTimeMillis();
        String purpose = req.getParameter("purpose");
        String submissionTarget = req.getParameter("submissionTarget");

        if (employeeNo == null || purpose == null || certificateTypeCode == null) {
            req.setAttribute("errorMsg", "必須入力項目が入力されていません。");
            return "/WEB-INF/pages/person/certificatePrintWorking.jsp"; 
        }

        CertificatePrintWorkingModel model = new CertificatePrintWorkingModel();
        model.setEmployeeNo(employeeNo);
        model.setCertificateTypeCode(certificateTypeCode);
        model.setIssueNo(issueNo);
        model.setPurpose(purpose);
        model.setSubmissionTarget(submissionTarget);
        // 상태(certificateYn)는 Model에서 기본값 "Y"로 세팅되어 있음
        // ステータス(certificateYn)はModelでデフォルト値「Y」にセッティングされている

        boolean isSaved = service.insertCertificatePrintWorking(model);

        if (isSaved) {
            // URL에 한글(증명서 종류)이 들어가므로 인코딩 처리
        	// URLに韓国語(証明書の種類)が入るためエンコーディング処理
            String encodedCertType = java.net.URLEncoder.encode(certificateTypeCode, "UTF-8");
            
            // 저장 완료 신호(save=success)와 함께 화면 리다이렉트
            // 保存完了シグナル(save=success)と共に画面リダイレクト
            res.sendRedirect(req.getContextPath() + "/Person/certificatePrintWorking.do?employeeNo=" + employeeNo + "&certType=" + encodedCertType + "&save=success");
            return null;
        } else {
            req.setAttribute("errorMsg", "保存に失敗しました。もう一度やり直してください.");
            return "/WEB-INF/pages/person/certificatePrintWorking.jsp";
        }
    }

}
