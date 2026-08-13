package person.certificatePrintWorking.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import config.employee.model.Employee;
import connection.ConnectionProvider;
import person.certificatePrintWorking.dao.CertificatePrintWorkingDao;

public class CertificatePrintWorkingReadService {
	private CertificatePrintWorkingDao certDao = new CertificatePrintWorkingDao();

    // 1. 사원 목록 조회
    public List<Employee> getEmployeeList() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return certDao.selectEmployeeList(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // 2. 사원 상세 조회 및 개인정보(주민등록번호) 마스킹 처리
    public Employee getEmployeeDetail(String employeeNo) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            Employee emp = certDao.selectEmployeeDetail(conn, employeeNo);
            
            if (emp != null && emp.getResidentRegNo() != null) {
                String rrn = emp.getResidentRegNo();
                // 14자리(000000-0000000) 기준 앞 8자리만 노출
                if (rrn.length() >= 14) {
                    emp.setResidentRegNo(rrn.substring(0, 8) + "******");
                }
            }
            return emp;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // 3. 근속기간 계산 비즈니스 로직
    public String calculateWorkPeriod(Employee emp) {
        if (emp == null || emp.getHireDate() == null) {
            return "";
        }

        LocalDate hireDate = emp.getHireDate().toLocalDate();
        LocalDate endDate = LocalDate.now(); // 재직 중이면 오늘 기준
        
        // 퇴직자이고 퇴사일이 존재하면 퇴사일 기준
        if ("Y".equals(emp.getRetirementYn()) && emp.getResignDate() != null) {
            endDate = emp.getResignDate().toLocalDate();
        }
        
        Period period = Period.between(hireDate, endDate);
        return period.getYears() + "년 " + period.getMonths() + "개월";
    }

    // 4. 증명서 종류별 텍스트 결정 비즈니스 로직
    public String getCertificateText(String certType) {
        if ("경력증명서".equals(certType)) {
            return "위와 같이 경력을 증명합니다.";
        } else if ("퇴직증명서".equals(certType)) {
            return "상기인은 위와 같이 재직 후 퇴직하였음을 증명합니다.";
        }
        // 기본값: 재직증명서
        return "상기인은 현재 위와 같이 당사에 재직하고 있음을 증명합니다.";
    }

}
