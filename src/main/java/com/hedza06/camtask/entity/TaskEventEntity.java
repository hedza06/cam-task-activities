package com.hedza06.camtask.entity;

import com.hedza06.camtask.entity.converters.JsonConverter;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
@Table(name = "task_event")
@DynamicInsert
@DynamicUpdate
public class TaskEventEntity implements Serializable {

    private static final long serialVersionUID = 2021031417390000L;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @NotEmpty
    @NotBlank
    @Column(name = "process_definition_key", nullable = false)
    private String processDefinitionKey;

    @NotNull
    @NotEmpty
    @NotBlank
    @Column(name = "process_definition_name", nullable = false)
    private String processDefinitionName;

    @NotNull
    @NotEmpty
    @NotBlank
    @Column(name = "process_instance_id", nullable = false)
    private String processInstanceId;

    @Column(name = "super_process_instance_id")
    private String superProcessInstanceId;

    @NotNull
    @NotEmpty
    @NotBlank
    @Column(name = "execution_id", nullable = false)
    private String executionId;

    @NotNull
    @NotEmpty
    @NotBlank
    @Column(name = "task_instance_id", nullable = false)
    private String taskInstanceId;

    @NotNull
    @NotEmpty
    @NotBlank
    @Column(name = "task_id", nullable = false)
    private String taskId;

    @NotNull
    @NotEmpty
    @NotBlank
    @Column(name = "task_name", nullable = false)
    private String taskName;

    @Column(name = "last_assignee")
    private String lastAssignee;

    @Column(name = "candidate_users")
    private String candidateUsers;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "customer_general_data")
    @Convert(converter = JsonConverter.class)
    @SuppressWarnings("java:S1948")
    private Map<String, Object> customerGeneralData = new HashMap<>();

    @Column(name = "product_general_data")
    @Convert(converter = JsonConverter.class)
    @SuppressWarnings("java:S1948")
    private Map<String, Object> productGeneralData = new HashMap<>();

    @Column(name = "start_time", nullable = false)
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

}
