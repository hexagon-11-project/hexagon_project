package person.certificateRegister.command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import person.certificateRegister.service.CertificateRegisterService;
import person.model.CertificatePrintWorkingModel;

public class CertificateRegisterReadHandler implements CommandHandler {

	private CertificateRegisterService registerService = new CertificateRegisterService();

	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String certType = request.getParameter("certType");
		String empName = request.getParameter("empName");
		
		// 1. 날짜 기본값 세팅 (최초 접속 시 파라미터가 비어있을 때)
		if (startDate == null || endDate == null) {
			LocalDate today = LocalDate.now();
			LocalDate firstDay = today.withDayOfMonth(1);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			
			startDate = firstDay.format(formatter);
			endDate = today.format(formatter);
		}
		
		// 선택값 기본 처리 ("전체" 또는 입력 안 한 경우)
		if (certType == null) certType = "전체";
		if (empName == null) empName = "";
		
		// JSP에서 검색어와 조건을 계속 유지할 수 있게 request에 저장
		request.setAttribute("startDate", startDate);
		request.setAttribute("endDate", endDate);
		request.setAttribute("certType", certType);
		request.setAttribute("empName", empName);
		
		// 2. Service 호출 시 4개의 파라미터를 넘겨주도록 변경
		List<CertificatePrintWorkingModel> certList = registerService.getCertificateList(startDate, endDate, certType, empName);
		
		// 3. JSP 화면에서 뿌려줄 수 있게 request 영역에 세팅
		request.setAttribute("certList", certList);
		
		// 4. 화면을 띄워줄 JSP 파일의 실제 경로 리턴
		return "/WEB-INF/pages/person/certificateRegister.jsp"; 
	}
}