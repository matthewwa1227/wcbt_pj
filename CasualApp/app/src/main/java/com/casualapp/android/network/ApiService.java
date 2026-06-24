package com.casualapp.android.network;

import com.casualapp.android.model.Job;
import com.casualapp.android.model.JobAttendance;
import com.casualapp.android.model.JobSignup;
import com.casualapp.android.model.User;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ApiService {

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
}