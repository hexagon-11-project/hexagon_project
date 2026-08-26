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
    private int size = 30; // 30개 고정 출력

 // 전체 목록 조회 기능 (페이징 안 함)
    public List<RetirementProcessModel> getRetirementEmployeeList(String searchName, String status) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            List<RetirementProcessModel> list = retirementDao.getRetirementList(conn, searchName, status);
            applyWorkYearsLogic(list);
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("DB 조회 중 오류가 발생했습니다.", e);
        }
    }

    // 30개 단위 페이징 처리된 페이지 객체 가져오기
    public RetirementProcessPage getRetirementProcessPage(int pageNum, String searchName, String status) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            int total = retirementDao.getRetirementCount(conn, searchName, status);
            List<RetirementProcessModel> list = null;
            
            if (total > 0) {
                int firstRow = (pageNum - 1) * size + 1;
                int endRow = firstRow + size - 1;
                list = retirementDao.getRetirementListByPaging(conn, searchName, status, firstRow, endRow);
                applyWorkYearsLogic(list); // 근속연수 포맷팅 적용
            }
            return new RetirementProcessPage(total, pageNum, size, list);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("DB 페이징 조회 중 오류가 발생했습니다.", e);
        }
    }

    // 근속연수 계산 공통 로직
    private void applyWorkYearsLogic(List<RetirementProcessModel> list) {
        if (list == null) return;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (RetirementProcessModel model : list) {
            if (model.getHireDate() != null && !model.getHireDate().isEmpty()) {
                LocalDate hireDate = LocalDate.parse(model.getHireDate(), formatter);
                LocalDate endDate = LocalDate.now();
                
                if ("Y".equals(model.getRetirementYn()) && model.getResignDate() != null && !model.getResignDate().isEmpty()) {
                    endDate = LocalDate.parse(model.getResignDate(), formatter);
                }
                
                Period period = Period.between(hireDate, endDate);
                model.setWorkYears(period.getYears() + "년 " + period.getMonths() + "개월");
            } else {
                model.setWorkYears("-");
            }
        }
    }
}