package person.model;

public class CertificatePrintWorkingModel {
	// 1. 화면에서 넘겨받을 실제 데이터들
    private String employeeNo;          // 사원번호 (화면에서 hidden으로 넘어옴, 조인용)
    private String certificateTypeCode; // 증명서종류코드 (재직/경력/퇴직 등)
    private String issueNo;             // 발급번호
    private String purpose;             // 용도 (DB의 PURPOSE)
    private String submissionTarget;    // 제출처 (선택사항, 화면에 제출처가 있다면 사용)
    private String certificateYn ; // 상태 (기본값 'Y')
    private String regId;              // 담당자 
    private String employeeName;
    private String issueDate;
    // 기본 생성자
    public CertificatePrintWorkingModel() {}

    // Getter & Setter
    public String getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    public String getCertificateTypeCode() {
        return certificateTypeCode;
    }

    public void setCertificateTypeCode(String certificateTypeCode) {
        this.certificateTypeCode = certificateTypeCode;
    }

    public String getIssueNo() {
        return issueNo;
    }

    public void setIssueNo(String issueNo) {
        this.issueNo = issueNo;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getSubmissionTarget() {
        return submissionTarget;
    }

    public void setSubmissionTarget(String submissionTarget) {
        this.submissionTarget = submissionTarget;
    }

    public String getCertificateYn() {
        return certificateYn;
    }

    public void setCertificateYn(String certificateYn) {
        this.certificateYn = certificateYn;
    }

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}
    
    
}


