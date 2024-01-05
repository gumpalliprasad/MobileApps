package myschoolapp.com.gsnedutech.Util;

public class AppUrls {

    /**
     * DomainName , BASEURL and SCHEMA NAME
     */
//    private static final String DomainName  ="k12hub.in:9000";
//    private static final String OtDomainName = "13.127.159.96:3000";

//    private static final String DomainName  ="15.207.72.130:80";
//    private static final String OtDomainName = "15.207.72.130:3000";

//    private static final String DomainName = "web.myschoolapp.io:9000";
//    private static final String OtDomainName = "13.233.204.118:4000";
//    private static final String DomainName = "admin.newmasterminds.online";
//    private static final String SchemaDomainName = "newmasterminds.online";

//    private static final String DomainName = "msassoadmin.myschoolapp.io";  //"3.109.4.27"; //"15.206.126.139:9001"; // "gmsadmin.myschoolapp.io"; //"webmaster.myschoolapp.io"
//    private static final String OtDomainName = "msassoapi.myschoolapp.io"; //"exams.myschoolapp.io"; //gmsapi.myschoolapp.io //

    private static final String DomainName = "admin.gsnedutech.com";  //"3.109.4.27"; //"15.206.126.139:9001"; // "gmsadmin.myschoolapp.io"; //"webmaster.myschoolapp.io"
    private static final String OtDomainName = "msassoapi.myschoolapp.in"; //"exams.myschoolapp.io"; //gmsapi.myschoolapp.io //

    /**
     * For getting the Schema
     */
    //   public static final String OT_URL = "https://" + OtDomainName + "/";
//    public static final String BASE_URL = "https://" + DomainName + "/";
    public static final String OT_URL = "https://" + OtDomainName + "/";
    public static final String BASE_URL = "https://" + DomainName + "/";
    public static String GetCollegeCode = BASE_URL + "getCollegeCode?domainName=" + DomainName;
//    public static String GetCollegeCode = BASE_URL + "getCollegeCode?domainName=" + SchemaDomainName;
//    public static String GetCollegeCode = "http://13.232.213.153:9001/getCollegeCode?domainName=13.232.213.153:9001";

    public static String GetCredDetails = BASE_URL + "getCredDetails?";

    /**
     * LOGIN USER Active Status
     */
    public static final String GetStudentActiveStatus = BASE_URL + "getStudentActiveStatus?";

    /**
     * SharedPreferences
     */
    public static final String shCredentials = "mySchool";

    /**
     * Server Time
     */
    public static final String GetServerTime = BASE_URL + "getDateAndTime";

    /**
     * LOGIN STUDENT
     */
    public static final String GetStudentDetailsiForMobile = BASE_URL + "getStudentDetailsiForMobile";

    /**
     * LOGIN STUDENT
     */
    public static final String LOGIN_STUDENT = BASE_URL + "loginStudent";
    /**
     * UPDATE STUDENT LOGINSTATUS
     */
    public static final String UPDATE_STUDENT_LOGINSTATUS = BASE_URL + "updateStudentLoginStatus";

    /**
     * LOGIN USER (Staff and Parents)
     */
    public static final String LOGIN_USER = BASE_URL + "loginUser";

    /**
     * Reg_validateUserByDOB
     */
    public static final String Reg_validateUserByDOB = BASE_URL + "validateUserByDOB?";
    /**
     * REG resetStudentPassword
     */
    public static final String RESET_STUDENT_PASSWORD = BASE_URL + "resetStudentPassword";

    /**
     * Change Student Password
     */
    public static final String CHANGE_STUDENT_PASSWORD = BASE_URL + "changeStudentPassword";

    /**
     * Student Opted Courses
     */
    public static final String Get_StudentOptedCourses = BASE_URL + "getStudentOptedCourses?";

    /**
     * Student Schedules
     */
    public static final String Get_StudentSchedules = BASE_URL + "getStudentSchedules?";

    /**
     * Student Assignments
     */
    public static final String Get_StudentAssignments = BASE_URL + "getStudentAssignments?";

    /**
     * Student Library Files
     */
    public static final String Get_StudentLibraryFiles = BASE_URL + "getStudentLibraryFiles?";

    /**
     * GetChapterTopicsBySubId
     */
    public static final String GetChapterTopicsBySubId = BASE_URL + "getChapterTopicsBySubId?";

    /**
     * GetContentAccessByClassCourse
     */
    public static final String GetContentAccessByClassCourse = BASE_URL + "contentAccessByClassCourse?";

    /**
     * GetContentAccessBySection
     */
    public static final String GetContentAccessBySection = BASE_URL + "contentAccessBySection?";

    /**
     * GetTopicContent
     */
    public static final String GetTopicContent = BASE_URL + "getTopicContent?";

    /**
     * GetVideosByTopicId
     */
    public static final String GetTopicVideos = BASE_URL + "getTopicVideos?";

    /**
     * GetClassroomVideosByTopicId
     */
    public static final String GetClassroomVideos = BASE_URL + "getClassroomVideos?";

    /**
     * GetTextbookFilesByTopic
     */
    public static final String GetTextbookFilesByTopic = BASE_URL + "getTextbookFilesByTopic?";


    /**
     * Student - GetStudentSubjects - sectionId=1&classId=3&courseId=5&studentId=1
     */
    public static final String GetStudentSubjects = BASE_URL + "getStudentSubjects?";

    /**
     * Student - getDefaultCourseClassByInstType - branchId=1&courseId=5&
     */
    public static final String GetDefaultCourseClassByInstType = BASE_URL + "getDefaultCourseClassByInstType?";

    /**
     * Student - getDefaultCourseClassByInstType - branchId=1&courseId=5&
     */
    public static final String GetSectionOptCourseSubjects = BASE_URL + "getSectionOptCourseSubjects?";


    /**
     * Student - TEST - Getting Course Topic Summary
     */
    public static final String GetCourseTopicSummary = BASE_URL + "getChapterAnnexure?";

    /**
     * Student - Course - Getting Course Topic Q&A
     */
    public static final String GetCourseTopicQA = BASE_URL + "getQuestionAndAnswers?";


    /**
     * Student - TEST - Getting all ClassCCourseSubjects
     */
    public static final String GetAllStudentClassCourseSubjects = BASE_URL + "getAllStudentClassCourseSubjects?";

    /**
     * Student - TEST - Getting all TestCategories
     */
    public static final String GetTestCategories = BASE_URL + "getTestCategories?";

    /**
     * Student - TEST - Post MockTestResult
     */
    public static final String PostMockTestResult = BASE_URL + "savePracticeMockTest";

    /**
     * Student - TEST - POST - StudentMockTestReport
     */
    public static final String GetStudentMockTestReport = BASE_URL + "getStudentMockTestReport";

    /**
     * Student - TEST - Course Practice - GetPracticeTestquestionCount
     */
    public static final String GetPracticeTestquestionCount = BASE_URL + "getStudentPracticeTestquestionCount?";
    /**
     * Student - TEST - Course Practice - Getting Questions
     */
    public static final String GetCoursePracticeQueestions = BASE_URL + "getStudentPractiseQuestions?";

    /**
     * Student - TEST - Course Practice - Posting Questions
     */
    public static final String PostCoursePracticeQueestion = BASE_URL + "updatePracticeStudentTestStatus";

    /**
     * Student - TEST - Course MockTest - Getting Questions
     */
    public static final String GetMockTestQueestions = BASE_URL + "getMockTestQuestionsForStudent";

    /**
     * Student - TEST - Online Tests - Getting the Online Tests
     */
    public static final String GetStudentOnlineTests = BASE_URL + "getStudentTests?";
    public static final String GetStudentOnlineTestsV2 = BASE_URL + "getStudentTestsV2?";

    /**
     * Student - TEST - Online Tests - Getting the Online Test Questions
     */
    public static final String GetStudentOnlineTestQuestionsV2 = BASE_URL + "getStudentTestQuestionsV2?";

    /**
     * Student - TEST - Online Tests - Get Student DB TestQuestions if we are saving the questions in DB
     */
    public static final String GetStudentDBTestQuestions = BASE_URL + "getStudentDBTestQuestions?";
    /**
     * Student - TEST - Online Tests - UpdateStudentTestQuestion
     */
    public static final String UpdateStudentTestQuestion = BASE_URL + "updateStudentTestQuestion";
    /**
     * Student - TEST - Online Tests - UpdateStyle
     */
    public static final String UpdateStyle = BASE_URL + "updateStyle";
    /**
     * Student - TEST - Online Tests - UpdateTimeTaken
     */
    public static final String UpdateTimeTaken = BASE_URL + "updateTimeTaken";

    /**
     * Student - TEST - Online Tests - SendAwsFailedStudentProgress
     */
    public static final String SendAwsFailedStudentProgress = BASE_URL + "sendAwsFailedStudentProgress";
    /**
     * Student - TEST - Online Tests - SubmitStudentOnlineTest
     */
    public static final String SubmitStudentOnlineTest = BASE_URL + "submitStudentTest";
    public static final String SubmitStudentOnlineTestV2 = BASE_URL + "submitStudentTestV2";
    /**
     * Student - TEST - Online Tests - GetStudentTestAnalysis
     */
    public static final String GetStudentTestAnalysis = BASE_URL + "getStudentTestAnalysis?";

    /**
     * Student - TEST - Online Tests - GetStudentTestQuestionAnalysis
     */
    public static final String GetStudentTestQuestionAnalysis = BASE_URL + "getStudentTestQuestionAnalysis?";

    /**
     * Student - MySchool - Diary - HomeWorks
     */
    public static final String GetStudentHomeWorks = BASE_URL + "getStudentHomeWorks?";

    /**
     * Student - MySchool - Diary - HomeWorks
     */
    public static final String GetStudentHomeWorksByStatus = BASE_URL + "getStudentHomeWorksByStatus?";

    /**
     * Student - GetHomeWorkFilesByStudent
     */
    public static final String GetHomeWorkFilesByStudent = BASE_URL + "getHomeWorkFilesByStudent?";
    /**
     * HomeWorks - HomeWorkFileDownLoad Img
     */
    public static final String HomeWorkFileDownLoad = BASE_URL + "HomeWorkFileDownLoad?filePath=";
    /**
     * Student - UpdateStudentHomework
     */
    public static final String UpdateStudentHomework = BASE_URL + "updateStudentHomework";

    /**
     * Student - StudentHWFileDelete
     */
    public static final String StudentHWFileDelete = BASE_URL + "studentHWFileDelete?";

    /**
     * Student - Analytics  - TotalStudentMockTestsByTestCategory
     */
    public static final String GetTotalStudentMockTestsByTestCategory = BASE_URL + "totalStudentMockTestsByTestCategory?";

    /**
     * Student - Analytics  - TotalStudentMockTestsByClass
     */
    public static final String GetTotalStudentMockTestsByClass = BASE_URL + "totalStudentMockTestsByClass?";

    /**
     * Student - Analytics  - StudentMockTestsSubjectAnalysisByclass
     */
    public static final String GetSubjectAnalysisByclass = BASE_URL + "studentMockTestsSubjectAnalysisByclass?";

    /**
     * Student - Analytics  - StudentMockTestsAnalysisByChapter
     */
    public static final String GetStudentMockTestsAnalysisByChapter = BASE_URL + "studentMockTestsAnalysisByChapter";

    /**
     * Student - Analytics  - StudentMockTestsAnalysisByTopic
     */
    public static final String GETStudentMockTestsAnalysisByTopic = BASE_URL + "studentMockTestsAnalysisByTopic";

    /**
     * Student - Profile  - GetstudentDetailsById
     */
    public static final String GetstudentDetailsById = BASE_URL + "studentDetailsById?";
    /**
     * Student - Profile  - GetstudentProfilePic
     */
    public static final String GetstudentProfilePic = BASE_URL + "studentProfileDownload?filePath=";

    /**
     * Teacher - Profile  - UploadTeacherProfilePic
     */
    public static final String UploadTeacherProfilePic = BASE_URL + "uploadUserProfilePic";
    /**
     * Student - Profile  - UploadStudentProfilePic
     */
//    public static final String UploadStudentProfilePic = BASE_URL + "uploadStudentProfilePic";
    public static final String UploadStudentProfilePic = BASE_URL + "uploadStudentProfilePicS3";


    /**
     * Student - TEST - Online Tests - Getting the Sections of the Online Tests
     */
    public static final String GetStudentOnlineTestSectons = BASE_URL + "getJeeTestSections?";
    /**
     * Student - Funzone - GetGameTypes
     */
    public static final String FunZoneGetGameTypes = BASE_URL + "getGameTypes?";
    /**
     * Student - Funzone - GetCCGameFiles
     */
    public static final String FunZoneGetCCGameFiles = BASE_URL + "getCCGameFiles?";

    /**
     * Student - SUBZONE - GetCourseClassSubjects
     */
    public static final String GetCourseClassSubjects = BASE_URL + "getCourseClassSubjects?";

    /**
     * Student -SubGaming - GetSubGamingCreativeContents
     */
    public static final String GetSubGamingCreativeContents = BASE_URL + "getAllFunGameConcepts?";

    /**
     * Student -SubGaming - Creative - GetAllFilesByConceptId
     */
    public static final String GetAllFilesByConceptId = BASE_URL + "getAllFunGameConceptFileByConceptId?";

    /**
     * Student -SubGaming - GetSubGamingSubjective
     */
    public static final String GetSubGamingSubjective = BASE_URL + "getSubjectiveGameQuestions?";

    /**
     * Student - SubGaming - GetContentType
     */
    public static final String GetContentType = BASE_URL + "getStudentCourseContentType?";

    /**
     * Student - OnlineClasses - getStudentLiveVideos
     */
    public static final String GetStudentLiveVideos = BASE_URL + "getStudentLiveVideos?";

    /**
     * Student - OnlineClasses - GetstudentLiveAttendance
     */
    public static final String GetstudentLiveAttendance = BASE_URL + "studentLiveAttendance";

    /**
     * Student - OnlineClasses - GetStudentLiveMeetingDetails
     */
    public static final String GetStudentLiveMeetingDetails = BASE_URL + "getStudentLiveMeetingDetails?";

    /**
     * Student - OnlineClasses - GetSessionRecordedVideos
     */
    public static final String GetSessionRecordedVideos = BASE_URL + "getRecordedVideos?";

    /**
     * GetStudent GetLiveSessionRecordings
     */
    public static final String GetLiveSessionRecordings = BASE_URL + "getLiveSessionRecordings";


    /**
     * YouTube API Key
     */
//    public static final String YouTubeAPIKey = "AIzaSyBLtf0Mkki9E92vb7BaHH9FsAIyk6ZgWOc";
    public static final String YouTubeAPIKey1 = "AIzaSyBLtf0Mkki9";
    public static final String YouTubeAPIKey2 = "E92vb7BaHH9Fs";
    public static final String YouTubeAPIKey3 = "AIyk6ZgWOc";

    //teacher
    /**
     * TeacherModule -  Course,Class,Section,Subject Details
     */
    public static final String TeacherCCSSDetials = BASE_URL + "getTeacherClassSectionSubjects?";

    /**
     * Teacher - Featured - GetUpcomingTestForTeacherDashBoard
     */
    public static final String GetUpcomingTestForTeacherDashBoard = BASE_URL + "getTestsForTeacherDashBoard?";

    /**
     * TeacherModule - HomeWork - Getting the Seection HomeWorks- Getting Students based on the Subject Selected
     */
    public static final String HOMEWORK_GetSectionHomeWorkDetails = BASE_URL + "getTeacherHomeWorks?";

    /**
     * AdminModule - HomeWork - sendStudentHomeworkSMSNotificationBySection
     */
    public static final String HomeWork_SMSNotificationBySection = BASE_URL + "sendStudentHomeworkSMSNotificationBySection";

    /**
     * TeacherModule - HomeWork - GetHomeworkDetailsById
     */
    public static final String HOMEWORK_GetHomeworkDetailsById = BASE_URL + "getStudentsForHomework?";

    /**
     * TeacherModule - HomeWork - uploadFiles - Posting  the Attachments
     */
    public static final String HOMEWORK_PostUploadFiles = BASE_URL + "uploadHomeWork";

    /**
     * TeacherModule - HomeWork - AddNewHomeWork - Creating New HomeWork
     */
    public static final String HOMEWORK_PostInsertSecHomeWork = BASE_URL + "insertSectionHomework";
    /**
     * TeacherModule - HomeWork - AddNewHomeWork - Getting HomeWorkTyps
     */
    public static final String HOMEWORK_GetTypes = BASE_URL + "getHomeWorkTypes?";
    /**
     * TeacherModule - HomeWork - AddNewHomeWork - Getting Students based on the Subject Selected
     */
    public static final String HOMEWORK_GetStudentsBySubject = BASE_URL + "getStudentsForHomeworkBySubject?";

    /**
     * Admin - Courses - GetselectContentOwner
     */
    public static final String GetselectContentOwner = BASE_URL + "selectContentOwner?";

    /**
     * TeacherModule - HomeWork - Deleting the  HomeWorks file attachments
     */
    public static final String HOMEWORK_GetHWFileDelete = BASE_URL + "HWFileDelete?";
    /**
     * TeacherModule - HomeWork - UpdateHomeWork - Updating HomeWork
     */
    public static final String HOMEWORK_PostUpdateSecHomeWork = BASE_URL + "updateSectionHomeworkDetails";
    /**
     * TeacherModule - HomeWork - UpdateStudentHWSubmission
     */
    public static final String UpdateStudentHWSubmission = BASE_URL + "updateStudentHWSubmission";

    /**
     * TeacherMOdule - HomeWork - Evaluate Single File
     */
    public static final String UPDATESTUDENTFILEFEEDBACK = BASE_URL + "updateStudentFileFeedback";

    /**
     * TeacherModule - TimeeTable - GetTimetable Details
     */
    public static final String GetTeacher_TimeTable_Details = BASE_URL + "getTeacherTimetable?";

    /**
     * Get Teacher Assigned LiveClasses
     */
    public static final String GetTeacherAssignedLiveClasses = BASE_URL + "getTeacherAssignedLiveClasses?";

    /**
     * Get Teacher Assigned Tests by Date
     */
    public static final String GetMonthTestsForTeacher = BASE_URL + "getMonthTestsForTeacher?";

    /**
     * TeacherModule - Test - GetTestByCalendarForTeacher
     */
    public static final String Test_GetTestByCalendarForTeacher = BASE_URL + "getTestByCalendarForTeacher?";
    /**
     * TeacherModule - Test - GetAllTestsForTeacherByCalendar
     */
    public static final String Test_GetAllTestsForTeacherByCalendar = BASE_URL + "getAllTestsForTeacherByCalendar?";

    /**
     * Student - MySchool - Calendar -  GetEventsAndHolidaysMonth
     */
    public static final String GetEventsAndHolidaysMonth = BASE_URL + "getEventsAndHolidaysMonth";

    /**
     * Student - GetStudentSurveyForms
     */
    public static final String GetStudentSurveyForms = BASE_URL + "getStudentSurveyForms?";
    /**
     * Student - GetSurveyQuestionByForm
     */
    public static final String GetSurveyQuestionByForm = BASE_URL + "getSurveyQuestionByForm?";
    /**
     * Student - SaveStudentSurveyFormFeedback
     */
    public static final String SaveStudentSurveyFormFeedback = BASE_URL + "saveStudentSurveyFormFeedback";

    /**
     * Student - GetSessionRecBySecTopic
     */
    public static final String GetSessionRecBySecTopic = BASE_URL + "getSessionRecBySecTopic?";


    /**
     * Student - MySchool - Attendance - GetStudentOverallAttendance
     */
    public static final String GetStudentOverallAttendance = BASE_URL + "getStudentOverallAttendance?";
    /**
     * Student - MySchool - Attendance - GetStudentAttendanceForAnalysis
     */
    public static final String GetStudentAttendanceForAnalysis = BASE_URL + "getStudentAttendanceForAnalysis?";

    /**
     * Student - MySchool - Attendance - GetStudentMonthWiseAttendanceDetails
     */
    public static final String GetStudentMonthWiseAttendanceDetails = BASE_URL + "getStudentMonthWiseAttendanceDetails?";


    /**
     * Student - MySchool - Attendance - GetTotalLeaveRequestsForStudent
     */
    public static final String GetTotalLeaveRequestsForStudent = BASE_URL + "getTotalLeaveRequestsForStudent?";


    /**
     * Admin Module - Circulars - GetCirculars
     */
    public static final String GetCirculars = BASE_URL + "getCCTransactions";

    /**
     * HomeWorks - HomeWorkFileDownLoad PDF
     */
//    public static final String HomeWorkFileDownLoadPDF = "http://drive.google.com/viewerng/viewer?embedded=true&url=" + BASE_URL + "HomeWorkFileDownLoad?filePath=";
    public static final String HomeWorkFileDownLoadPDF = BASE_URL + "HomeWorkFileDownLoad?filePath=";


    /**
     * Student - TEST - Online Tests - Getting the Online Test Questions
     */
    public static final String GetStudentOnlineTestQuestions = BASE_URL + "getStudentTestQuestions?";

    /**
     * Student - GetStudentPersonalNotes
     */
    public static final String GetStudentPersonalNotes = BASE_URL + "getStudentPersonalNotes?";

    /**
     * Student - AddPersonalNote
     */
    public static final String AddPersonalNote = BASE_URL + "addPersonalNote";

    /**
     * Student - UpdateStudentPersonalNote
     */
    public static final String UpdateStudentPersonalNote = BASE_URL + "updateStudentPersonalNote";
    /**
     * Student - DeleteStudentPersonalNote
     */
    public static final String DeleteStudentPersonalNote = BASE_URL + "deleteStudentPersonalNote?";

    /**
     * AdminModule - Attendance - GetHolidaysForStudentAttendance
     */
    public static final String GetHolidaysForStudentAttendance = BASE_URL + "getHolidaysForStudentAttendance";

    /**
     * TeacherModule - Attendance - Taking Attendance - Get Students
     */
    public static final String Attendance_GetStudents = BASE_URL + "getStudentsForAttendance?";

    /**
     * TeacherModule - Attendance - Taking Attendance - Get SectionsDay Attendance
     */
    public static final String Attendance_GetSectionDayReports = BASE_URL + "getSectionDayAttendanceReport?";

    /**
     * TeacherModule - Attendance - Taking Attendance - Get Absent Students
     */
    public static final String Attendance_GetAbsentStudents = BASE_URL + "getAbsentStudentsForAtt?";

    /**
     * TeacherModule - Attendance - Taking Attendance - Posting the Attendance
     */
    public static final String Attendance_PostInsertStudents = BASE_URL + "insertStudentsAttendance";

    /**
     * TeacherModule - Attendance - GetStudentLeaveRequestInfo - Get Students
     */
    public static final String Attendance_GetStudentLeaveRequestInfo = BASE_URL + "getStudentLeaveRequestInfo?";


    /**
     * TeacherModule - Attendance - Taking Attendance - Update the Attendance
     */
    public static final String Attendance_PostUpdateStudents = BASE_URL + "updateSectionAttendance";

    /**
     * Parent - Messages  - GetAllMessages
     */
    public static final String GetAllMessages = BASE_URL + "getAllMessages?";

    /**
     * TeacherModule - Attendance - GetTotalLeaveRequestCountByDate
     */
    public static final String Attendance_GetTotalLeaveRequestCountByDate = BASE_URL + "getTotalLeaveRequestCountByDate?";

    /**
     * TeacherModule - Attendance - GetTotalLeaveRequestsDetailsByDate
     */
    public static final String Attendance_GetTotalLeaveRequestsDetailsByDate = BASE_URL + "getTotalLeaveRequestsDetailsByDate?";

    /**
     * AdminModule - LeaveeRequest - updateStudentLeaveRequest
     */
    public static final String UpdateStudentLeaveRequest = BASE_URL + "updateStudentLeaveRequest";

    /**
     * AdminModule - Attendance - Attendance_StudentAttendanceNotification
     */
    public static final String Attendance_StudentAttendanceNotification = BASE_URL + "sendStudentAttendanceNotification";

    /**
     * TeacherModule - HomeWork - Getting the Student List assigned to the  HomeWork-
     */
    public static final String HOMEWORK_GetStudentsofHomework = BASE_URL + "getStudentsForHomework?";


    /**
     * Student - GetStudentToDo
     */
    public static final String GetStudentToDo = BASE_URL + "getTodoList?";


    /**
     * Student - AddStudentTODO
     */
    public static final String AddStudentToDo = BASE_URL + "addTodoList";


    /**
     * Student - UpdateStudentTODO
     */
    public static final String UpdateStudentTODO = BASE_URL + "updateToDoList";

    /**
     * Student - CompletedToDoList
     */
    public static final String CompletedToDoList = BASE_URL + "completedToDoList";


    /**
     * Teacher - GetTeacherToDo
     */
    public static final String GetTeacherToDo = BASE_URL + "getTodoList?";


    /**
     * Teacher - AddTeacherTODO
     */
    public static final String AddTeacherToDo = BASE_URL + "addTodoList";


    /**
     * Teacher - UpdateTeacherTODO
     */
    public static final String UpdateTeacherTODO = BASE_URL + "updateToDoList";


    /**
     * Student - MySchool - TimeTable  - GetSectionTimeTable
     */
    public static final String GetSectionTimeTable = BASE_URL + "getSectionTimeTable?";

    /**
     * GetMonthWiseAttendanceAnalysis
     */
    public static final String GetMonthWiseAttendanceAnalysis = BASE_URL + "getMonthWiseAttendanceAnalysis?";

    /**
     * Parent - Messages  - GetAdminForParentMessages
     */
    public static final String GetAdminForParentMessages = BASE_URL + "getAdminForParent?";


    /**
     * Parent - Messages  - GetMessageDetails
     */
    public static final String GetMessageDetails = BASE_URL + "getMessageDetails?";

    /**
     * Parent - Messages  - PostCreateNewMessage
     */
    public static final String PostCreateNewMessage = BASE_URL + "createNewMessage";


    /**
     * Admin - StaffAttendanceOverAll - GetStaffOverallAttendance - For Every individual
     */
    public static final String GetStaffOverallAttendance = BASE_URL + "getStaffOverallAttendance?";
    /**
     * Admin - StaffAttendanceOverAll - GetStaffAttendanceForAnalysis - For Every individual
     */
    public static final String GetStaffAttendanceForAnalysis = BASE_URL + "getStaffAttendanceForAnalysis?";

    /**
     * Admin - StaffAttendanceOverAll - GetStaffMonthWiseAttendanceDetails - For Every individual
     */
    public static final String GetStaffMonthWiseAttendanceDetails = BASE_URL + "getStaffMonthWiseAttendanceDetails?";

    /**
     * TeacherModule -  GetTotalLeaveRequestsForStaff
     */
    public static final String GetTotalLeaveRequestsForStaff = BASE_URL + "getTotalLeaveRequestsForStaff?";

    /**
     * TeacherModule -  AddStaffLeaveRequests
     */
    public static final String AddStaffLeaveRequests = BASE_URL + "addStaffLeaveRequests";

    /**
     * Parent - LeaveRequest  - addStudentsLeaveRequest
     */
    public static final String PostAddStudentsLeaveRequest = BASE_URL + "addStudentsLeaveRequest";


    /**
     * Admin - Featured - GetAllBranches
     */
    public static final String GetAllBranches = BASE_URL + "getAllBranches?";
    /**
     * Admin - Featured - GetCollegeInstituteTypes
     */
    public static final String GetCollegeInstituteTypes = BASE_URL + "getCollegeInstituteTypes?";
    /**
     * Admin - Featured - GetAdminClassCoursesByBId
     */
    public static final String GetAdminClassCoursesByBId = BASE_URL + "getClassCoursesByBId?";
    /**
     * Admin - Featured - GetStudentAttendanceReportForAdmin
     */
    public static final String GetStudentAttendanceReportForAdmin = BASE_URL + "getStudentAttendanceReportForAdmin?";
    /**
     * Admin - Featured - GetStaffAttendanceReportForAdmin
     */
    public static final String GetStaffAttendanceReportForAdmin = BASE_URL + "getStaffAttendanceReportForAdmin?";
    /**
     * Admin - Featured - GetTotalLeaveRequestsForStaffByBranch
     */
    public static final String GetTotalLeaveRequestsForStaffByBranch = BASE_URL + "getTotalLeaveRequestsForStaffByBranch?";
    /**
     * Admin - Featured - GetAllLeaveRequestsByBranch for that day
     */
    public static final String GetAllLeaveRequestsByBranch = BASE_URL + "getAllLeaveRequestsByBranch?";

    /**
     * Admin - Featured - GetUpcomingTestForAdminDashBoard
     */
    public static final String GetUpcomingTestForAdminDashBoard = BASE_URL + "getUpcomingTestForAdminDashBoard?";

    /**
     * Admin - Courses - GetCourseBranchId
     */
    public static final String GetCourseBranchId = BASE_URL + "getCourseBranchId?";
    /**
     * Admin - Courses - GetClassByCoursesID
     */
    public static final String GetClassByCoursesID = BASE_URL + "getClassByCoursesID?";
    /**
     * Admin - Courses - GetCourseClassSubjects
     */
    public static final String GetAdminCourseClassSubjects = BASE_URL + "getCourseClassSubjects?";

    /**
     * Admin - Tests - GetTestByCalender
     */
    public static final String GetTestOfAdminByCalender = BASE_URL + "getTestByCalender?";

    /**
     * Admin - Tests - GetAllTestsForAdminByCalnderForMobile
     */
    public static final String GetAllTestsForAdminByCalnderForMobile = BASE_URL + "getAllTestsForAdminByCalnderForMobile?";
    /**
     * Admin - StaffLeaveRequests - Approving/Rejecting
     */
    public static final String UpdateStaffLeaveRequest = BASE_URL + "updateStaffLeaveRequest";
    /**
     * Admin - StaffLeaveRequests - GetStaffLeaveRequestsByDate
     */
    public static final String GetStaffLeaveRequestsByDate = BASE_URL + "getStffLeaveRequestsByDate?";
    /**
     * Admin - Messages  - SearchMember
     */
    public static final String SearchMember = BASE_URL + "searchMember?";
    /**
     * AdminModule - Attendance - Taking Staff Attendance - Posting the Attendance
     */
    public static final String Attendance_PostInsertStafff = BASE_URL + "insertStaffAttendance";
    /**
     * AdminModule - Attendance - Updating Staff Attendance - Update the Attendance
     */
    public static final String Attendance_PostUpdateStaff = BASE_URL + "updateStaffAttendance";
    /**
     * AdminModule - Attendance - GetHolidaysForStaffAttendance
     */
    public static final String GetHolidaysForStaffAttendance = BASE_URL + "getHolidaysForStaffAttendance";
    /**
     * Admin - Featured - GetStaffAttendance
     */
    public static final String GetStaffForAttendanceAnalysis = BASE_URL + "getStaffForAttendanceAnalysis?";
    /**
     * Admin - Attendance - GetStaffDayAttendanceReport
     */
    public static final String GetStaffDayAttendanceReport = BASE_URL + "getStaffDayAttendanceReport?";
    /**
     * AdminModule - Attendance - GetAbsentStaffByMonthByDate
     */
    public static final String GetAbsentStaffByMonth = BASE_URL + "getAbsentStaffByMonth?";
    /**
     * AdminModule - Attendance - Get Staff LeaveRequestInfo - While taking Attendance
     */
    public static final String Attendance_GetStaffLeaveRequestInfo = BASE_URL + "getStaffLeaveRequestInfo?";

    /**
     * Admin - Featured - GetSectionsByBranchCourseClass
     */
    public static final String GetSectionsByBranchCourseClass = BASE_URL + "selectSectionsByBranchCourseClass?";

    /**
     * Admin - Featured - GetAllCourseClassSubjects
     */
    public static final String getAllCourseClassSubjects = BASE_URL + "getAllCourseClassSubjects?";

    /**
     * Teacher&Admin - GetuserDetailsForProfile
     */
    public static final String GetuserDetailsForProfile = BASE_URL + "userDetailsForProfile?";

    /**
     * Staff - Profile  - GetStaffProfilePic
     */
    public static final String GetStaffProfilePic = BASE_URL + "userProfileDownload?filePath=";


    /**
     * Student - LiveClasses - studentLiveAttendance
     */

    public static final String StudentLiveAttendance = BASE_URL + "studentLiveAttendance";

    /**
     * AddStudentLiveClassAttendance
     */
    public static final String AddStudentLiveClassAttendance = BASE_URL + "addStudentLiveClassAttendance?";


    /**
     * Arena API's
     */

    /**
     * Getting all the Categories available in arena
     */
    public static final String Arena_GetArenaCategories = BASE_URL + "getArenaCategories?";

    /**
     * Getting all the Arenas
     */
    public static final String GetArenas = BASE_URL + "arenas";

    /**
     * Getting popular Arenas
     */
    public static final String GetPopularArenas = BASE_URL + "popularArenas";

    /**
     * GetStudentArenas
     */
    public static final String GetStudentArenas = BASE_URL + "getUserArenas";

    /**
     * GetTeacherArenas
     */
    public static final String GetTeacherArenas = BASE_URL + "getTeacherArenas";

    /**
     * Get Quiz and FlashCards Questions
     */
    public static final String GetQuizQuestionsById = BASE_URL + "getQuizQuestionsById?";

    /**
     * InsertArenaRecord
     */
    public static final String InsertArenaRecord = BASE_URL + "insertArenaRecord?";

    /**
     * InsertTeacherArenaRecord
     */
    public static final String InsertTeacherArenaRecord = BASE_URL + "insertArenaRecord?";

    /**
     * InsertArenaSubRecords
     */
    public static final String InsertArenaSubRecords = BASE_URL + "insertArenaSubRecords?";

    /**
     * InsertTeacherArenaSubRecords
     */
    public static final String InsertTeacherArenaSubRecords = BASE_URL + "insertArenaSubRecords?";

    /**
     * DeleteArenaDetailFile
     */
    public static final String DeleteArenaDetailFile = BASE_URL + "deleteArenaDetailFile?";

    /**
     * DeleteArena
     */
    public static final String DeleteArena = BASE_URL + "deleteArena";

    /**
     * SubmitStudentArena
     */
    public static final String SubmitStudentArena = BASE_URL + "submitStudentArena?";

    /**
     * SubmitTeacherArena
     */
    public static final String SubmitTeacherArena = BASE_URL + "submitStudentArena?";

    /**
     * Arena API's - GetGeneralArenaDetailsById
     */

    public static final String GetGeneralArenaDetailsById = BASE_URL + "getGeneralArenaDetailsById?";

    /**
     * Arena API's - GetArenasforTeacher
     */

    public static final String GetArenasforTeacher = BASE_URL + "getArenasforTeacher";

    /**
     * Arena API's - ReviewArenaStatus
     */

    public static final String ReviewArenaStatus = BASE_URL + "reviewArenaStatus";

    /**
     * LikeArena
     */
    public static final String LikeArena = BASE_URL + "likeRateArenaQuestion";

    /**
     * GetTeachersForArena
     */
    public static final String GetTeachersForArena = BASE_URL + "getTeachersForArena";

    /**
     * InsertArenaScore
     */
    public static final String InsertArenaScore = BASE_URL + "insertArenaScore";

    /**
     * UpdateArenaScore
     */
    public static final String UpdateArenaScore = BASE_URL + "updateArenaScore";

    /**
     * FetchArenaLeaderboard
     */
    public static final String FetchArenaLeaderboard = BASE_URL + "fetchArenaLeaderboard";

    /**
     * FetchArenaUserRank
     */
    public static final String FetchArenaUserRank = BASE_URL + "fetchArenaUserRank";

    /**
     * GetAllBranchClassCourseSections
     */
    public static final String GetAllBranchClassCourseSections = BASE_URL + "getAllBranchClassCourseSections?";

    /**
     * GetAllArenaBranchClassCourseSections
     */
    public static final String GetAllArenaBranchClassCourseSections = BASE_URL + "getAllArenaBranchClassCourseSections?";


    /**
     * UpdateArenaQuestion
     */
    public static final String UpdateArenaQuestion = BASE_URL + "updateArenaQuestion";

    /**
     * InsertStudentPoll
     */
    public static final String InsertStudentPoll = BASE_URL + "insertStudentPoll";

    /**
     * Arena API's
     */

    /**
     * QBox API's
     */

    /**
     * InsertStudentQBoxQuestion
     */
    public static final String InsertStudentQBoxQuestion = BASE_URL + "insertStudentQBoxQuestion";
    /**
     * GetStudentQBoxQuestions
     */
    public static final String GetStudentQBoxQuestions = BASE_URL + "stuQBoxQuestions";
    /**
     * FetchQBoxReplies
     */
    public static final String FetchQBoxReplies = BASE_URL + "fetchQBoxReplies";
    /**
     * Teacher QBox Questions
     */
    public static final String GetTeacherQBoxQuestions = BASE_URL + "teacherQBoxQuestions";
    /**
     * InsertTeacherQBoxQuesAnswer
     */
    public static final String InsertTeacherQBoxQuesAnswer = BASE_URL + "insertTeacherQBoxQuesAnswer";
    /**
     * UpdateqboxQuestionStatus
     */
    public static final String UpdateqboxQuestionStatus = BASE_URL + "updateqboxQuestionStatus";
    /**
     * LikeRateqboxQuestion
     */
    public static final String LikeRateqboxQuestion = BASE_URL + "likeRateqboxQuestion";
    /**
     * AutoCompleteQboxSuggestions
     */
    public static final String AutoCompleteQboxSuggestions = BASE_URL + "autoCompleteQboxSuggestions";
    /**
     * GetQboxFiles?
     */
    public static final String GetQboxFiles = BASE_URL + "getqboxFiles?";
    /**
     * Get QboxDetailQuestionById
     */
    public static final String GetQboxDetailQuestionById = BASE_URL + "qboxDetailQuestionById";

    /**
     * QBox API's
     */

    /**
     * Khub Api's Start
     */

    /**
     * Khub Base Url
     */
    public static final String KHUB_BASE_URL = "https://khub.myschoolapp.io/student/api/v1/";

    /***
     * Khub Categories
     */
    public static final String CATEGORIES = "categories";
    /**
     * Khub Courses
     */
    public static final String COURSES = "courses?";
    /**
     * Khub modules
     */
    public static final String MODULES = "modules?";
    /**
     * Khub module content types
     */
    public static final String ModuleContentTypes = "moduleContentTypes?";
    /**
     * Khub content
     */
    public static final String Content = "content?";
    /**
     * Khub post content Activity
     */
    public static final String ContentActivity = "contentActivity";
    /**
     * khub course enrollment
     */
    public static final String EnrollCourse = "enrollCourse";
    /**
     * khub mycourses
     */
    public static final String MyCourses = "myCourses?";
    /**
     * khub courseView
     */
    public static final String CourseView = "courseView";
    /**
     * Khub Banners
     */
    public static final String KHUBBanners = "banners";
    /**
     * Khub PostScholorship
     */
    public static final String KhubScholorShip = "studentScholorShip";
    /**
     * Khub Popular
     */
    public static final String KhubPopularCourses = "popularCourses";
    /**
     * Khub Trending
     */
    public static final String KhubTrendingCourses = "trendingCourses";
    /**
     * Khub Course Rating
     */
    public static final String KhubCourseRating = "courseRating";

    /**
     * Khub Api's End
     */


    /**
     * LOGIN OnlineTest STUDENT
     */

    public static final String GetSudentByAdmissionNo = OT_URL + "GetSudentByAdmissionNo?";

    public static final String LOGIN_ONLINE_Test_STUDENT = OT_URL + "login";

    public static final String GETSTUDENTEXAMBYID = OT_URL + "getStudentExamById";

    public static final String UPDATESTUDENTEXAMSTATUS = OT_URL + "updateStudentExamStatus";

    public static final String UPDATESTATUSBYID = OT_URL + "updateStatusById";

    public static final String GETSTATUSBYID = OT_URL + "getStatusById";

    public static final String UPDATEDEVICE = OT_URL + "updateDevice";

    public static final String GETSTUDENTEXAMSTATUS = OT_URL + "getStudentExamStatus";

    public static final String GETSTUDENTEXAMS = OT_URL + "GetStudentExams?";

    public static final String UPDATELIVEEXAMSTATUS = OT_URL + "updateLiveExamStatus";

    public static final String GetExamLastModifiedDate = OT_URL + "getExamLastModifiedDate";

    /**
     * Student - Login- SetStudentPassword
     */
    public static final String SetStudentPassword = OT_URL + "setStudentPassword";
    /**
     * Student - GetConfigBySchema
     */
    public static final String GetConfigBySchema = OT_URL + "getConfigBySchema";

    /**
     * Student - Get Results from DB if S3 fails
     */
    public static final String GetExamResultByStudentId = OT_URL + "examResultByStudentId?";

    /**
     * OnLine Test PlatForm Ended
     */

    /**
     * Student - Login- DOB
     */
    public static final String ValidateUserByDOB = BASE_URL + "validateUserByDOB?";

    /**
     * Student - Login- ResetStudentPassword
     */
    public static final String ResetStudentPassword = BASE_URL + "resetStudentPassword";

    /**
     * Student Notifications
     */
    public static final String GetStuNotifications = BASE_URL + "getStuNotifications?";

    /**
     * LOGIN Descriptive STUDENT
     */

    public static final String DESCRIPTIVE_URL = OT_URL + "descriptive/";

    public static final String DGetSudentByAdmissionNo = DESCRIPTIVE_URL + "GetSudentByAdmissionNo?";

    public static final String DLOGIN_ONLINE_Test_STUDENT = DESCRIPTIVE_URL + "login";

    public static final String DGETSTUDENTEXAMBYID = DESCRIPTIVE_URL + "getStudentExamById";

    public static final String DUPDATESTUDENTEXAMSTATUS = DESCRIPTIVE_URL + "updateStudentExamStatus";

    public static final String DUPDATESTATUSBYID = DESCRIPTIVE_URL + "updateStatusById";

    public static final String DGETSTATUSBYID = DESCRIPTIVE_URL + "getStatusById";

    public static final String DUPDATEDEVICE = DESCRIPTIVE_URL + "updateDevice";

    public static final String DGETSTUDENTEXAMSTATUS = DESCRIPTIVE_URL + "getStudentExamStatus";

    public static final String DGETSTUDENTEXAMS = DESCRIPTIVE_URL + "GetStudentExams?";

    public static final String DUPDATELIVEEXAMSTATUS = DESCRIPTIVE_URL + "updateLiveExamStatus";

    public static final String DGetExamLastModifiedDate = DESCRIPTIVE_URL + "getExamLastModifiedDate";

    /**
     * Student - Login- SetStudentPassword
     */
    public static final String DSetStudentPassword = DESCRIPTIVE_URL + "setStudentPassword";
    /**
     * Student - GetConfigBySchema
     */
    public static final String DGetConfigBySchema = DESCRIPTIVE_URL + "getConfigBySchema";

    /**
     * Student - Get Results from DB if S3 fails
     */
    public static final String DGetExamResultByStudentId = DESCRIPTIVE_URL + "examResultByStudentId?";

    /**
     * Teacher - GetStudentsExamBySectionIds
     */
    public static final String DGetStudentsExamBySectionIds = DESCRIPTIVE_URL + "getStudentsExamBySectionIds?";

    /**
     * Teacher - UpdateMarks
     */
    public static final String DUpdateMarks = DESCRIPTIVE_URL + "updateMarks";

    /**
     * Teacher - RemoveMarks
     */
    public static final String DRemoveMarks = DESCRIPTIVE_URL + "removeMarks";

    /**
     * Teacher - GetTeacherDescExams
     */
    public static final String GetTeacherDescExams = BASE_URL + "getTeacherDescExams?";

    /**
     * Server Time
     */
    public static final String startStaffLiveClass = BASE_URL + "startStaffLiveClass";

    //Change password for teacher
    public static final String CHANGE_TEACHER_PASSWORD = BASE_URL + "changePassword";


    //college code
    public static final String INST_COLLEGE_CODE = BASE_URL + "getInstCollegeCode?collegeCode=";

    //getImportantContent
    public static final String getImportantContent = BASE_URL + "getImportantContent?";

    //getTextbookContent
    public static final String getTextbookContent = BASE_URL + "getTextbookContent?";

    //getStudentMockTestReportCount
    public static final String getStudentMockTestReportCount = BASE_URL + "getStudentMockTestReportCount";

    /**
     * Parent - Fee Module Starts
     */

    /**
     * Parent - GetStudentTotalFee
     */
    public static final String GetStudentTotalFee = BASE_URL + "getStudentTotalFee?";

    /**
     * Parent - GetStudentDueFee
     */
    public static final String GetStudentDueFee = BASE_URL + "getStudentDueFee?";

    /**
     * Parent - GetStudentFeeCategories
     */
    public static final String GetStudentFeeCategories = BASE_URL + "getStudentFeeCategories?";

    /**
     * Parent - GetStudentDueFeeDetail
     */
    public static final String GetStudentDueFeeDetail = BASE_URL + "getStudentDueFeeDetail?";

    /**
     * Parent - UpdateFeePayment
     */
    public static final String UpdateFeePayment = BASE_URL + "updateFeePayment";
}

