package myschoolapp.com.gsnedutech.Neet.Utils;

import org.json.JSONObject;

import java.util.ArrayList;

import myschoolapp.com.gsnedutech.Neet.models.ForgotReq;
import myschoolapp.com.gsnedutech.Neet.models.ForgotResult;
import myschoolapp.com.gsnedutech.Neet.models.LoginReq;
import myschoolapp.com.gsnedutech.Neet.models.NeetChapter;
import myschoolapp.com.gsnedutech.Neet.models.NeetQuestion;
import myschoolapp.com.gsnedutech.Neet.models.NeetSubjectsObj;
import myschoolapp.com.gsnedutech.Neet.models.NeetSubmitAnswer;
import myschoolapp.com.gsnedutech.Neet.models.NeetSubmitExpectedQuestion;
import myschoolapp.com.gsnedutech.Neet.models.RegisterUser;
import myschoolapp.com.gsnedutech.Neet.models.SignUpRequest;
import myschoolapp.com.gsnedutech.Neet.models.UserObj;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NeetApiInterface {

    @GET("subjects/aggregation")
    Call<ArrayList<NeetSubjectsObj>> getSubjects(@Query("userId") String id);

    @GET("chapters/aggregation")
    Call<ArrayList<NeetChapter>> getChapters(@Query("contentMatrixId") String id, @Query("userId") String userId);

    @GET("chapters/aggregation")
    Call<ArrayList<NeetChapter>> getChapter(@Query("contentMatrixId") String id, @Query("userId") String userId, @Query("chapterId") String chapId);

    @GET("questions/aggregation")
    Call<ArrayList<NeetQuestion>> getQuestions(@Query("contentMatrixId") String id, @Query("userId") String userId, @Query("chapterId") String chapId);

    @GET("questions")
    Call<ArrayList<NeetQuestion>> getQuestions(@Query("chapterId") String id);

    @POST("userprofiles")
    Call<JSONObject> registerUser(@Body RegisterUser newUser);

    @GET("userprofiles")
    Call<ArrayList<UserObj>> getUser(@Query("uid") String id );

    @POST("studentpractices")
    Call<JSONObject> submitAnswer(@Body NeetSubmitAnswer body);

    @GET("expectedquestions")
    Call<ArrayList<NeetQuestion>> getExpectedQuestions(@Query("questionRefId") String id, @Query("status") boolean status);

    @POST("expectedquestionspractices")
    Call<JSONObject> submitExpectedQuestion(@Body NeetSubmitExpectedQuestion body);

    //New Api's

    @POST("api/auth/signup")
    Call<UserObj> signupUser(@Body SignUpRequest newUser);

    @POST("api/auth/signin")
    Call<UserObj> signinUser(@Body LoginReq newUser);

    @POST("api/auth/isEmailExist")
    Call<ForgotResult> checkEmail(@Body ForgotReq body);

    @POST("api/auth/changePassword")
    Call<ForgotResult> changePassword(@Body ForgotReq newUser);

}
