package person.certificatePrintWorking.command;

import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.employee.model.Employee;
import person.certificatePrintWorking.service.CertificatePrintWorkingReadService;

public class CertificatePrintWorkingReadHandler implements CommandHandler {

	private CertificatePrintWorkingReadService certService = new CertificatePrintWorkingReadService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        
        String employeeNo = req.getParameter("employeeNo");
        String certType = req.getParameter("certType");
        String searchName = req.getParameter("searchName"); // 검색어 파라미터
        if (certType == null || certType.trim().isEmpty()) {// 検索キーワードパラメータ
            certType = "在職証明書"; 
        }

        // 좌측 리스트 세팅
        // 左側リストセッティング
        List<Employee> empList = certService.getEmployeeList(searchName);
        req.setAttribute("empList", empList);

        // 우측 상세 데이터 세팅
        // 右側詳細データセッティング
        if (employeeNo != null && !employeeNo.isEmpty()) {
            Employee empDetail = certService.getEmployeeDetail(employeeNo);
            
            if (empDetail != null) {
                // 퇴직증명서 예외 처리
            	// 退職証明書の例外処理
                if ("退職証明書".equals(certType) && "N".equals(empDetail.getRetirementYn())) {
                    req.setAttribute("alertMessage", "該当社員は退職処理されていないため、退職証明書を発行できません。");
                    certType = "在職証明書"; 
                }
                
                req.setAttribute("empDetail", empDetail);
                req.setAttribute("workPeriod", certService.calculateWorkPeriod(empDetail));
                req.setAttribute("certText", certService.getCertificateText(certType));
            }
        }

        // 화면 상태 유지용 세팅
        // 画面状態維持用セッティング
        req.setAttribute("selectedEmpNo", employeeNo);
        req.setAttribute("selectedCertType", certType);
        req.setAttribute("today", LocalDate.now().toString());

        return "/WEB-INF/pages/person/certificatePrintWorking.jsp";
    }
}
