package com.casualapp.backend.persistence;

//import com.casualapp.backend.persistence.ports.EventLogPersistence;
//import com.casualapp.backend.persistence.ports.JobAttendancePersistence;
import com.casualapp.backend.persistence.ports.JobPersistence;
import com.casualapp.backend.persistence.ports.JobSignupPersistence;
import com.casualapp.backend.persistence.ports.UserPersistence;
//import com.casualapp.backend.persistence.ports.VenuePersistence;
//import com.casualapp.backend.persistence.ports.WorkerProfilePersistence;

public interface PersistenceFactory {
    UserPersistence users();
    // WorkerProfilePersistence workerProfiles();
    // VenuePersistence venues();
    JobPersistence jobs();
    JobSignupPersistence jobSignups();
    // JobAttendancePersistence jobAttendance();
    // EventLogPersistence eventLogs();
}