package controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import command.NullHandler;

public class ControllerUsingUri extends HttpServlet {

	private Map<String, CommandHandler> commandHandlerMap = new HashMap<>();

	@Override
	public void init() throws ServletException {
		String configFile = getInitParameter("configFile");
		Properties prop = new Properties();
		String configFilePath = getServletContext().getRealPath(configFile);
		try (FileInputStream fis = new FileInputStream(configFilePath)) {
			prop.load(fis);
		} catch (IOException e) {
			throw new ServletException(e);
		}
		Iterator keyIter = prop.keySet().iterator();
		while (keyIter.hasNext()) {
			String command = (String)keyIter.next();
			String handlerClassName = prop.getProperty(command);
			try {
				Class<?> handlerClass = Class.forName(handlerClassName);
				CommandHandler handlerInstance = (CommandHandler)handlerClass.getDeclaredConstructor().newInstance();
				commandHandlerMap.put(command, handlerInstance);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				throw new ServletException(e);
			} catch (Exception e) {}
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}

	private void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String command = req.getRequestURI();
		if (command.indexOf(req.getContextPath()) == 0) {
			command = command.substring(req.getContextPath().length());
		}
		CommandHandler handler = commandHandlerMap.get(command);
		if (handler == null) {
			handler = new NullHandler();
		}
		String viewPage = null;
		try {
			viewPage = handler.process(req, resp);
		} catch (Exception e) {
			throw new ServletException(e);
		}
		if (viewPage != null) {
			RequestDispatcher dispatcher = req.getRequestDispatcher(viewPage);
			dispatcher.forward(req, resp);
		}
	}
}
