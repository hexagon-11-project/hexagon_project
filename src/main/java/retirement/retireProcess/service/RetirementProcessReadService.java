package retirement.retireProcess.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

import connection.ConnectionProvider;
import retirement.model.RetirementProcessModel;
import retirement.retireProcess.dao.RetirementProcessReadDao;

public class RetirementProcessReadService {
	private RetirementProcessReadDao retirementDao = new RetirementProcessReadDao();

    public List<RetirementProcessModel> getRetirementEmployeeList(String searchName, String status) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            
            List<RetirementProcessModel> list = retirementDao.getRetirementList(conn, searchName, status);
            
            // UI에 맞춰 근속연수 계산 비즈니스 로직 적용 (기존 서비스 로직 반영)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            for (RetirementProcessModel model : list) {
                if (model.getHireDate() != null && !model.getHireDate().isEmpty()) {
                    LocalDate hireDate = LocalDate.parse(model.getHireDate(), formatter);
                    LocalDate endDate = LocalDate.now(); // 재직 중이면 오늘 기준
                    
                    // 퇴직자이고 퇴사일이 존재하면 퇴사일 기준
                    if ("Y".equals(model.getRetirementYn()) && model.getResignDate() != null && !model.getResignDate().isEmpty()) {
                        endDate = LocalDate.parse(model.getResignDate(), formatter);
                    }
                    
                    Period period = Period.between(hireDate, endDate);
                    model.setWorkYears(period.getYears() + "년 " + period.getMonths() + "개월");
                } else {
                    model.setWorkYears("-");
                }
            }
            
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("DB 조회 중 오류가 발생했습니다.", e);
        }
    }
}
