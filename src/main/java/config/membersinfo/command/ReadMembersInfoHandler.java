package config.membersinfo.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.membersinfo.service.CompanyNotFoundException;
import config.membersinfo.service.ReadmembersInfoService;
import config.model.CompanyInfo;

public class ReadMembersInfoHandler implements CommandHandler {

	private ReadmembersInfoService readService = new ReadmembersInfoService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		try {
			// 로그인 기능 없으므로 ID 1001로 고정
			CompanyInfo companyInfo = readService.getCompanyInfo(1001);

			req.setAttribute("companyInfo", companyInfo);

			return "/WEB-INF/pages/config/membersInfo.jsp";

		} catch (CompanyNotFoundException e) {
			// 잘못된 회사 ID의 회사 정보가 없을 경우 404 Not Found 에러 응답
			req.getServletContext().log("no company info", e);
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}
}