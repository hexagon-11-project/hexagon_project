package retirement.retirementMnt.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;

public class RetirementMntHandler implements CommandHandler {

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		return "/WEB-INF/pages/retirement/retirementMnt.jsp"; 
	}

}
