package person.certificatePrintWorking.command;

import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.employee.model.Employee;
import person.certificatePrintWorking.service.CertificatePrintWorkingService;

public class certificatePrintWorkingReadHandler implements CommandHandler {

	private CertificatePrintWorkingService certService = new CertificatePrintWorkingService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        
        String employeeNo = req.getParameter("employeeNo");
        String certType = req.getParameter("certType");
        
        if (certType == null || certType.trim().isEmpty()) {
            certType = "재직증명서"; 
        }

        // 1. 좌측 리스트 세팅
        List<Employee> empList = certService.getEmployeeList();
        req.setAttribute("empList", empList);

        // 2. 우측 상세 데이터 세팅
        if (employeeNo != null && !employeeNo.isEmpty()) {
            Employee empDetail = certService.getEmployeeDetail(employeeNo);
            
            if (empDetail != null) {
                // 퇴직증명서 예외 처리
                if ("퇴직증명서".equals(certType) && "N".equals(empDetail.getRetirementYn())) {
                    req.setAttribute("alertMessage", "해당 사원은 퇴직 처리되지 않아 퇴직증명서를 발급할 수 없습니다.");
                    certType = "재직증명서"; 
                }
                
                req.setAttribute("empDetail", empDetail);
                req.setAttribute("workPeriod", certService.calculateWorkPeriod(empDetail));
                req.setAttribute("certText", certService.getCertificateText(certType));
            }
        }

        // 3. 화면 상태 유지용 세팅
        req.setAttribute("selectedEmpNo", employeeNo);
        req.setAttribute("selectedCertType", certType);
        req.setAttribute("today", LocalDate.now().toString());

        // 4. 리턴할 JSP 뷰 경로 (경로는 프로젝트 설정에 맞게 조금 수정해서 쓰시면 됩니다)
        return "/WEB-INF/pages/person/certificatePrintWorking.jsp";
    }
}
