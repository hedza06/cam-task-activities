package com.hedza06.camtask.repository;

import com.hedza06.camtask.entity.VariableStoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomVariableStoreRepository extends JpaRepository<VariableStoreEntity, Integer>
{
    VariableStoreEntity findByProcessInstanceId(String processInstanceId);

    @Modifying
    @Query(value = "delete from VariableStoreEntity variableStoreEntity " +
            "where variableStoreEntity.processInstanceId = :processInstanceId")
    void deleteByProcessInstanceId(@Param("processInstanceId") String processInstanceId);
}
