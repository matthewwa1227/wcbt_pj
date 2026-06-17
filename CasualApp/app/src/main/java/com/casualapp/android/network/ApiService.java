package com.casualapp.android.network;

import com.casualapp.android.model.Job;
import com.casualapp.android.model.JobSignup;
import com.casualapp.android.model.User;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ApiService {

    @GET("api/users")
    Call<List<User>> getAllUsers();

    @GET("api/jobs")
    Call<List<Job>> getAllJobs();

    @POST("api/jobs")
    Call<Job> createJob(@Body Job job, @Query("coordinatorId") Long coordinatorId);

    @GET("api/signups")
    Call<List<JobSignup>> getAllSignups();

    @POST("api/signups")
    Call<JobSignup> signUp(@Query("workerId") Long workerId, @Query("jobId") Long jobId);
}