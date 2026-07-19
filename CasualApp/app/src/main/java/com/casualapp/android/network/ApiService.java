package com.casualapp.android.network;

import com.casualapp.android.model.Job;
import com.casualapp.android.model.JobAttendance;
import com.casualapp.android.model.JobSignup;
import com.casualapp.android.model.LoginRequest;
import com.casualapp.android.model.User;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ApiService {

    @POST("api/auth/login")
    Call<User> login(@Body LoginRequest request);

    @GET("api/users")
    Call<List<User>> getAllUsers();

    @POST("api/users")
    Call<User> createUser(@Body User user);

    @GET("api/jobs")
    Call<List<Job>> getAllJobs();

    @POST("api/jobs")
    Call<Job> createJob(@Body Job job, @Query("coordinatorId") Long coordinatorId);

    @GET("api/signups")
    Call<List<JobSignup>> getAllSignups();

    @POST("api/signups")
    Call<JobSignup> signUp(@Query("workerId") Long workerId, @Query("jobId") Long jobId);

    @PUT("api/signups/{id}/approve")
    Call<JobSignup> approveSignup(@Path("id") Long id, @Query("coordinatorId") Long coordinatorId);

    @PUT("api/signups/{id}/attend")
    Call<JobAttendance> markAttended(@Path("id") Long id, @Query("recordedByUserId") Long recordedByUserId);

    @GET("jobs/coordinator/{coordinatorId}")
    Call<List<Job>> getJobsByCoordinator(
            @Path("coordinatorId") Long coordinatorId
    );

    @GET("signups/worker/{workerId}")
    Call<List<JobSignup>> getWorkerSignups(
            @Path("workerId") Long workerId
    );

    @GET("signups/job/{jobId}")
    Call<List<JobSignup>> getJobSignups(
            @Path("jobId") Long jobId,
            @Query("coordinatorId") Long coordinatorId
    );

    @GET("signups/coordinator/{coordinatorId}")
    Call<List<JobSignup>> getCoordinatorSignups(
            @Path("coordinatorId") Long coordinatorId
    );

    @PUT("signups/{signupId}/approve")
    Call<JobSignup> approveSignup(
            @Path("signupId") Long signupId,
            @Query("coordinatorId") Long coordinatorId,
            @Query("reason") String reason
    );

    @PUT("signups/{signupId}/reject")
    Call<JobSignup> rejectSignup(
            @Path("signupId") Long signupId,
            @Query("coordinatorId") Long coordinatorId,
            @Query("reason") String reason
    );

    @PUT("signups/{signupId}/attend")
    Call<JobAttendance> markAttendance(
            @Path("signupId") Long signupId,
            @Query("recordedByUserId") Long recordedByUserId,
            @Query("status") String status,
            @Query("lateMinutes") Integer lateMinutes,
            @Query("reason") String reason
    );
}