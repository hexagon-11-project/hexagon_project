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
		
		// 날짜 기본값 세팅 (최초 접속 시 파라미터가 비어있을 때)
		// // 日付のデフォルト値セッティング (初回アクセス時にパラメータが空の場合)
		if (startDate == null || endDate == null) {
			LocalDate today = LocalDate.now();
			LocalDate firstDay = today.withDayOfMonth(1);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			
			startDate = firstDay.format(formatter);
			endDate = today.format(formatter);
		}
		
		// 선택값 기본 처리 ("전체" 또는 입력 안 한 경우)
		// 選択値のデフォルト処理 ("全体"または入力されていない場合)
		if (certType == null) certType = "全体";
		if (empName == null) empName = "";
		
		request.setAttribute("startDate", startDate);
		request.setAttribute("endDate", endDate);
		request.setAttribute("certType", certType);
		request.setAttribute("empName", empName);
		
		List<CertificatePrintWorkingModel> certList = registerService.getCertificateList(startDate, endDate, certType, empName);
		
		request.setAttribute("certList", certList);
		
		return "/WEB-INF/pages/person/certificateRegister.jsp"; 
	}
}