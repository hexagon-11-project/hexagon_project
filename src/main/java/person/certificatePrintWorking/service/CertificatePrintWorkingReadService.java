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

    // 사원 목록 조회
	// 社員一覧照会
	public List<Employee> getEmployeeList(String searchName) {
	    try (Connection conn = ConnectionProvider.getConnection()) {
	        return certDao.selectEmployeeList(conn, searchName); // 검색어 전달
	    } catch (SQLException e) {								 // 検索キーワードを伝達
	        e.printStackTrace();
	        throw new RuntimeException(e);
	    }
	}

    // 사원 상세 조회 및 개인정보(주민등록번호) 마스킹 처리
	// 社員詳細照会および個人情報(住民登録番号)マスキング処理
    public Employee getEmployeeDetail(String employeeNo) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            Employee emp = certDao.selectEmployeeDetail(conn, employeeNo);
            
            if (emp != null && emp.getResidentRegNo() != null) {
                String rrn = emp.getResidentRegNo();
                // 14자리(000000-0000000) 기준 앞 8자리만 노출
                // 14桁(000000-0000000)基準で前8桁のみ露出
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

    // 근속기간 계산 비즈니스 로직
    // 勤続期間計算ビジネスロジック
    public String calculateWorkPeriod(Employee emp) {
        if (emp == null || emp.getHireDate() == null) {
            return "";
        }

        LocalDate hireDate = emp.getHireDate().toLocalDate();
        LocalDate endDate = LocalDate.now(); // 재직 중이면 오늘 기준
        									 //在職中であれば今日基準
        // 퇴직자이고 퇴사일이 존재하면 퇴사일 기준
        // 退職者であり退職日が存在する場合は退職日基準
        if ("Y".equals(emp.getRetirementYn()) && emp.getResignDate() != null) {
            endDate = emp.getResignDate().toLocalDate();
        }
        
        Period period = Period.between(hireDate, endDate);
        return period.getYears() + "년 " + period.getMonths() + "개월";
    }

    // 증명서 종류별 텍스트 결정 비즈니스 로직
    // 証明書の種類別テキスト決定ビジネスロジック
    public String getCertificateText(String certType) {
        if ("経歴証明書".equals(certType)) {
            return "上記のように経歴を証明します。";
        } else if ("退職証明書".equals(certType)) {
            return "上記人は上記のように在職後、退職したことを証明します。";
        }
        // 기본값: 재직증명서
        // デフォルト値：在職証明書
        return "上記人は現在、上記のように当社に在職していることを証明します。";
    }

}
