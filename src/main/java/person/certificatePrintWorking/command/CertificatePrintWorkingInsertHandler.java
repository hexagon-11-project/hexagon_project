package person.certificatePrintWorking.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import person.certificatePrintWorking.service.CertificatePrintWorkingInsertService;
import person.model.CertificatePrintWorkingModel;

public class CertificatePrintWorkingInsertHandler implements CommandHandler {
	private CertificatePrintWorkingInsertService service = new CertificatePrintWorkingInsertService();

    // @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        
        // GET 방식 요청은 차단하거나 폼 화면으로 돌려보냅니다.
        if (req.getMethod().equalsIgnoreCase("GET")) {
            return "/WEB-INF/pages/person/certificatePrintWorking.jsp"; // 에러 페이지 경로
        }

        // 1. 화면(form)에서 전송된 파라미터(name 속성값) 꺼내기
        // *주의: <input type="hidden" name="employeeNo"> 등으로 폼에 있어야 합니다.
        String employeeNo = req.getParameter("employeeNo"); 
        String certificateTypeCode = req.getParameter("certificateTypeCode");
        // String issueNo = req.getParameter("issueNo");
        // 화면에서 넘어오는 값 대신, 겹치지 않는 고유한 발급번호를 서버에서 직접 생성합니다.
        String issueNo = "ISSUE-" + System.currentTimeMillis();
        String purpose = req.getParameter("purpose");
        String submissionTarget = req.getParameter("submissionTarget");

        // 2. 파라미터 검증 (필수값이 비어있으면 에러 처리)
        if (employeeNo == null || purpose == null || certificateTypeCode == null) {
            req.setAttribute("errorMsg", "필수 입력 항목이 누락되었습니다.");
            return "/WEB-INF/pages/person/certificatePrintWorking.jsp"; // 다시 입력폼으로
        }

        // 3. Model 객체 생성 및 데이터 세팅
        CertificatePrintWorkingModel model = new CertificatePrintWorkingModel();
        model.setEmployeeNo(employeeNo);
        model.setCertificateTypeCode(certificateTypeCode);
        model.setIssueNo(issueNo);
        model.setPurpose(purpose);
        model.setSubmissionTarget(submissionTarget);
        // 상태(certificateYn)는 Model에서 기본값 "Y"로 세팅되어 있음

        // 4. 비즈니스 로직(Service) 호출
        boolean isSaved = service.insertCertificatePrintWorking(model);

        // 5. 결과에 따라 이동할 View 결정
        if (isSaved) {
            // 저장 성공 시: 발급 완료 화면 또는 목록 화면으로 리다이렉트
            // req.setAttribute("successMsg", "증명서가 성공적으로 발급/저장되었습니다.");
        	 return "/WEB-INF/pages/person/certificatePrintWorking.jsp";
        } else {
            // 저장 실패 시
            req.setAttribute("errorMsg", "저장에 실패했습니다. 다시 시도해주세요.");
            return "/WEB-INF/pages/person/certificatePrintWorking.jsp";
        }
    }

}
