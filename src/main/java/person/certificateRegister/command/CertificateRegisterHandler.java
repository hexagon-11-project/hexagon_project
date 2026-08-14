package person.certificateRegister.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;

public class CertificateRegisterHandler implements CommandHandler {


	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		return "/WEB-INF/pages/person/certificateRegister.jsp"; 
	}

}
