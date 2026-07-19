package com.casualapp.android.network;

import com.casualapp.android.model.Job;
import com.casualapp.android.model.JobAttendance;
import com.casualapp.android.model.JobSignup;
import com.casualapp.android.model.LoginRequest;
import com.casualapp.android.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Authentication

    @POST("api/auth/login")
    Call<User> login(
            @Body LoginRequest request
    );


    // Users

    @GET("api/users")
    Call<List<User>> getAllUsers();

    @POST("api/users")
    Call<User> createUser(
            @Body User user
    );


    // Jobs

    @GET("api/jobs")
    Call<List<Job>> getAllJobs();

    @GET("api/jobs/coordinator/{coordinatorId}")
    Call<List<Job>> getJobsByCoordinator(
            @Path("coordinatorId") Long coordinatorId
    );

    @POST("api/jobs")
    Call<Job> createJob(
            @Body Job job,
            @Query("coordinatorId") Long coordinatorId
    );


    // Signups

    @GET("api/signups")
    Call<List<JobSignup>> getAllSignups();

    @POST("api/signups")
    Call<JobSignup> signUp(
            @Query("workerId") Long workerId,
            @Query("jobId") Long jobId
    );

    @GET("api/signups/worker/{workerId}")
    Call<List<JobSignup>> getWorkerSignups(
            @Path("workerId") Long workerId
    );

    @GET("api/signups/job/{jobId}")
    Call<List<JobSignup>> getJobSignups(
            @Path("jobId") Long jobId,
            @Query("coordinatorId") Long coordinatorId
    );

    @GET("api/signups/coordinator/{coordinatorId}")
    Call<List<JobSignup>> getCoordinatorSignups(
            @Path("coordinatorId") Long coordinatorId
    );

    @PUT("api/signups/{signupId}/approve")
    Call<JobSignup> approveSignup(
            @Path("signupId") Long signupId,
            @Query("coordinatorId") Long coordinatorId,
            @Query("reason") String reason
    );

    @PUT("api/signups/{signupId}/reject")
    Call<JobSignup> rejectSignup(
            @Path("signupId") Long signupId,
            @Query("coordinatorId") Long coordinatorId,
            @Query("reason") String reason
    );


    // Attendance

    @PUT("api/signups/{signupId}/attend")
    Call<JobAttendance> markAttendance(
            @Path("signupId") Long signupId,
            @Query("recordedByUserId") Long recordedByUserId,
            @Query("status") String status,
            @Query("lateMinutes") Integer lateMinutes,
            @Query("reason") String reason
    );
}