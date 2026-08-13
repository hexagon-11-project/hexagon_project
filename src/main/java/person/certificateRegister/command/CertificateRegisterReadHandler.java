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
		
		// 1. 날짜 기본값 세팅 (시작일: 이번 달 1일, 종료일: 오늘)

		LocalDate today = LocalDate.now();
		LocalDate firstDay = today.withDayOfMonth(1);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		request.setAttribute("startDate", firstDay.format(formatter));
		request.setAttribute("endDate", today.format(formatter));
		
		// 2. Service 호출해서 DB 데이터 리스트 가져오기
		List<CertificatePrintWorkingModel> certList = registerService.getCertificateList();
		
		// 3. JSP 화면에서 뿌려줄 수 있게 request 영역에 세팅
		request.setAttribute("certList", certList);
		
		// 4. 화면을 띄워줄 JSP 파일의 실제 경로 리턴
		return "/WEB-INF/pages/person/certificateRegister.jsp"; 
	}
}