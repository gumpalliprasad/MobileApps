package myschoolapp.com.gsnedutech.JeeMains.Utils;



import org.json.JSONObject;

import java.util.ArrayList;

import myschoolapp.com.gsnedutech.JeeMains.models.Chapter;
import myschoolapp.com.gsnedutech.JeeMains.models.ForgotReq;
import myschoolapp.com.gsnedutech.JeeMains.models.ForgotResult;
import myschoolapp.com.gsnedutech.JeeMains.models.LoginReq;
import myschoolapp.com.gsnedutech.JeeMains.models.Question;
import myschoolapp.com.gsnedutech.JeeMains.models.RegisterUser;
import myschoolapp.com.gsnedutech.JeeMains.models.SignUpRequest;
import myschoolapp.com.gsnedutech.JeeMains.models.SignUpUserObj;
import myschoolapp.com.gsnedutech.JeeMains.models.SubjectsObj;
import myschoolapp.com.gsnedutech.JeeMains.models.SubmitAnswer;
import myschoolapp.com.gsnedutech.JeeMains.models.SubmitExpectedQuestion;
import myschoolapp.com.gsnedutech.JeeMains.models.UserObj;
import myschoolapp.com.gsnedutech.JeeAdvanced.Models.AdvJeeAllSubjects;
import myschoolapp.com.gsnedutech.JeeAdvanced.Models.AdvJeeQuestion;
import myschoolapp.com.gsnedutech.JeeAdvanced.Models.AdvJeeSubjectsObj;
import myschoolapp.com.gsnedutech.JeeAdvanced.Models.AdvJeeSubmitAnswer;
import myschoolapp.com.gsnedutech.JeeAdvanced.Models.AdvJeeYearsObj;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("subjects/aggregation")
    Call<ArrayList<SubjectsObj>> getSubjects(@Query("userId") String id);

    @GET("chapters/aggregation")
    Call<ArrayList<Chapter>> getChapters(@Query("contentMatrixId") String id, @Query("userId") String userId);

    @GET("chapters/aggregation")
    Call<ArrayList<Chapter>> getChapter(@Query("contentMatrixId") String id, @Query("userId") String userId, @Query("chapterId") String chapId);

    @GET("questions/aggregation")
    Call<ArrayList<Question>> getQuestions(@Query("contentMatrixId") String id, @Query("userId") String userId, @Query("chapterId") String chapId);

    @GET("questions")
    Call<ArrayList<Question>> getQuestions(@Query("chapterId") String id);

    @POST("userprofiles")
    Call<JSONObject> registerUser(@Body RegisterUser newUser);

    @GET("users")
    Call<ArrayList<UserObj>> getUser(@Query("uid") String id);

    @POST("studentpractices")
    Call<JSONObject> submitAnswer(@Body SubmitAnswer body);

    @GET("expectedquestions/expectedquestionslist")
    Call<ArrayList<Question>> getExpectedQuestions(@Query("questionRefId") String id, @Query("status") boolean status);

    @POST("expectedquestionspractices")
    Call<JSONObject> submitExpectedQuestion(@Body SubmitExpectedQuestion body);

    //New Api's

    @POST("api/auth/signup")
    Call<UserObj> signupUser(@Body SignUpRequest newUser);

    @POST("api/auth/signin")
    Call<UserObj> signinUser(@Body LoginReq newUser);

    @POST("api/auth/isEmailExist")
    Call<ForgotResult> checkEmail(@Body ForgotReq body);

    @POST("api/auth/changePassword")
    Call<ForgotResult> changePassword(@Body ForgotReq newUser);

    @GET("chapters")
    Call<ArrayList<Chapter>> getChaptersNew();

    @POST("users")
    Call<JSONObject> signUpUser(@Body SignUpUserObj newUser);


    /***
     * AdvJee
     */

    @GET("years/getAllYears")
    Call<ArrayList<AdvJeeYearsObj>> advjeeGetYears(@Query("userId") String userId);

    @GET("subjects")
    Call<ArrayList<AdvJeeSubjectsObj>> AdvjeeGetSubjects();

    @GET("questions/aggregation")
    Call<ArrayList<AdvJeeQuestion>> advjeeFetQuestions(@Query("yearId") String id, @Query("paper") String paperId, @Query("subjectId") String subjectId, @Query("userId") String userId);

    @GET("subjects/aggregation")
    Call<ArrayList<AdvJeeAllSubjects>> advjeeGetAllSubjects(@Query("yearId") String id, @Query("paper") String paperId, @Query("userId") String userId);

    @POST("studentpractices")
    Call<JSONObject> advJeeSubmitAnswer(@Body AdvJeeSubmitAnswer body);

    /***
     *
     */
}
