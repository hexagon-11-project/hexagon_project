package config.employee.model;

import java.sql.Date;

// EMPLOYEE_LANGUAGE 테이블 매핑 (어학능력)
public class EmployeeLanguage {
    private String languageName;    // LANGUAGE_NAME (외국어명)
    private String testName;        // TEST_NAME (시험명)
    private String officialScore;   // OFFICIAL_SCORE (공인점수)
    private Date acquisitionDate;   // ACQUISITION_DATE (취득일)
    private String readingLevelCode;  // READING_LEVEL_CODE (독해수준코드)
    private String writingLevelCode;  // WRITING_LEVEL_CODE (작문수준코드)
    private String speakingLevelCode; // SPEAKING_LEVEL_CODE (회화수준코드)

    public String getLanguageName() { return languageName; }
    public void setLanguageName(String v) { this.languageName = v; }
    public String getTestName() { return testName; }
    public void setTestName(String v) { this.testName = v; }
    public String getOfficialScore() { return officialScore; }
    public void setOfficialScore(String v) { this.officialScore = v; }
    public Date getAcquisitionDate() { return acquisitionDate; }
    public void setAcquisitionDate(Date v) { this.acquisitionDate = v; }
    public String getReadingLevelCode() { return readingLevelCode; }
    public void setReadingLevelCode(String v) { this.readingLevelCode = v; }
    public String getWritingLevelCode() { return writingLevelCode; }
    public void setWritingLevelCode(String v) { this.writingLevelCode = v; }
    public String getSpeakingLevelCode() { return speakingLevelCode; }
    public void setSpeakingLevelCode(String v) { this.speakingLevelCode = v; }
}
