package com.hedza06.camtask.repository;

import com.hedza06.camtask.entity.TaskEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomTaskActivityRepository extends JpaRepository<TaskEventEntity, Integer>
{
    TaskEventEntity findByExecutionIdAndTaskId(String executionId, String taskId);
    TaskEventEntity findFirstByProcessInstanceId(String processInstanceId);
}
