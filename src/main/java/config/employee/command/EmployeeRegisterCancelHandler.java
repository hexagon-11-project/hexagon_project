package config.employee.command;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.employee.dao.EmployeeDao;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

// 사원등록 2페이지에서 취소 - 이미 저장된 1페이지 데이터를 DB에서 지우고 1페이지 초기화면으로 이동
// / 社員登録2ページでキャンセル - 保存済みの1ページデータをDBから削除して1ページ初期画面へ
public class EmployeeRegisterCancelHandler implements CommandHandler {

    private EmployeeDao employeeDao = new EmployeeDao();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

        String employeeNo = req.getParameter("employeeNo");

        if (employeeNo != null && !employeeNo.isBlank()) {
            Connection conn = null;
            try {
                conn = ConnectionProvider.getConnection();
                employeeDao.deleteByEmployeeNo(conn, employeeNo);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                JdbcUtil.close(conn);
            }
        }

        res.sendRedirect(req.getContextPath() + "/Config/employeeIns1.do");
        return null;
    }
}
