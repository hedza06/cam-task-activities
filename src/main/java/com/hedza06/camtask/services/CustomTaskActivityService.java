package com.hedza06.camtask.services;

import com.hedza06.camtask.entity.TaskEventEntity;
import com.hedza06.camtask.repository.CustomTaskActivityRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.history.event.HistoricProcessInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricTaskInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricVariableUpdateEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.history.event.HistoryEventTypes;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.task.IdentityLink;
import org.camunda.bpm.engine.task.IdentityLinkType;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomTaskActivityService {

    private static final String CUSTOMER_ID = "customerId";
    private static final String PRODUCT_ID  = "productId";

    private final Map<String, Object> variableMapping = new HashMap<>();

    @Autowired
    private CustomTaskActivityRepository customTaskActivityRepository;


    /**
     * Processing process instance events | excluding migrate events because of performance issues
     *
     * @param historyEvent history process event
     */
    public void processProcessInstanceEvent(HistoryEvent historyEvent)
    {
        HistoricProcessInstanceEventEntity instanceEventEntity = (HistoricProcessInstanceEventEntity) historyEvent;
        String migrateEvent = HistoryEventTypes.PROCESS_INSTANCE_MIGRATE.getEventName();
        if (!instanceEventEntity.getEventType().equals(migrateEvent))
        {
            updateHistoricTaskSuperProcessInstance(
                instanceEventEntity.getProcessInstanceId(),
                instanceEventEntity.getSuperProcessInstanceId()
            );
        }
    }

    /**
     * Processing variable update historic event
     *
     * @param historyEvent history event
     */
    public void processVariableUpdateEvent(HistoryEvent historyEvent)
    {
        if (historyEvent instanceof HistoricVariableUpdateEventEntity)
        {
            HistoricVariableUpdateEventEntity variableEventEntity = (HistoricVariableUpdateEventEntity) historyEvent;
            String variableName = variableEventEntity.getVariableName();
            if (variableName.equals(CUSTOMER_ID) || variableName.equals(PRODUCT_ID))
            {
                appendVariableIdToVariableMap(
                    variableName,
                    variableEventEntity.getLongValue(),
                    variableEventEntity.getProcessInstanceId()
                );
            }
        }
    }

    /**
     * Process historic task instance event
     *
     * @param historyEvent history event
     */
    public void processTaskInstanceEvent(HistoryEvent historyEvent)
    {
        if (historyEvent instanceof HistoricTaskInstanceEventEntity)
        {
            log.info("History task instance invoked. Event type: {}", historyEvent.getEventType());
            HistoricTaskInstanceEventEntity historicTaskInstance = (HistoricTaskInstanceEventEntity) historyEvent;
            handleTaskInstanceEventBasedOnEventType(historicTaskInstance, historyEvent.getEventType());
        }
    }

    /**
     * Append variable id to variable map
     *
     * @param varName variable name
     * @param longValue variable long value
     * @param processInstanceId process instance identifier
     */
    @SuppressWarnings("unchecked")
    private void appendVariableIdToVariableMap(String varName, Long longValue, String processInstanceId)
    {
        if (variableMapping.containsKey(processInstanceId))
        {
            Map<String, Long> vars = (Map<String, Long>) variableMapping.get(processInstanceId);

            Map<String, Long> data = new HashMap<>(vars);
            data.put(varName, longValue);

            variableMapping.put(processInstanceId, data);
        }
        else {
            variableMapping.put(processInstanceId, Variables.createVariables().putValue(varName, longValue));
        }
    }

    /**
     * Handling task instance event based on event type
     *
     * @param historicTaskInstance historic task instance
     * @param eventType historic event type
     */
    private void handleTaskInstanceEventBasedOnEventType(HistoricTaskInstanceEventEntity historicTaskInstance,
                                                         String eventType)
    {
        if (eventType.equals(HistoryEventTypes.TASK_INSTANCE_CREATE.getEventName())) {
            createTaskEventEntity(historicTaskInstance);
        }
        else if (eventType.equals(HistoryEventTypes.TASK_INSTANCE_COMPLETE.getEventName())) {
            completeTaskEventEntity(historicTaskInstance);
        }
    }

    /**
     * Creating task event entity from historic task instance event entity
     *
     * @param historicTaskInstance historic task instance event entity
     */
    private void createTaskEventEntity(HistoricTaskInstanceEventEntity historicTaskInstance)
    {
        TaskEventEntity taskEventEntity = new TaskEventEntity();
        taskEventEntity.setProcessDefinitionKey(historicTaskInstance.getProcessDefinitionKey());
        taskEventEntity.setProcessDefinitionName(
            fetchProcessDefinitionNameFromDefKey(historicTaskInstance.getProcessDefinitionKey())
        );
        taskEventEntity.setProcessInstanceId(historicTaskInstance.getProcessInstanceId());
        taskEventEntity.setExecutionId(historicTaskInstance.getExecutionId());
        taskEventEntity.setTaskId(historicTaskInstance.getTaskId());
        taskEventEntity.setTaskName(historicTaskInstance.getName());
        taskEventEntity.setTaskInstanceId(historicTaskInstance.getTaskId());
        taskEventEntity.setLastAssignee(historicTaskInstance.getAssignee());
        taskEventEntity.setCandidateUsers(
            fetchCandidateUsersByTaskId(historicTaskInstance.getTaskId())
        );
        taskEventEntity.setStartTime(historicTaskInstance.getStartTime());

        fetchCustomerAndProductRelevantData(taskEventEntity, historicTaskInstance);
        variableMapping.remove(historicTaskInstance.getProcessInstanceId());

        customTaskActivityRepository.save(taskEventEntity);
    }

    /**
     * Complete task event entity
     *
     * @param historicTaskInstance historic task instance event entity
     */
    private void completeTaskEventEntity(HistoricTaskInstanceEventEntity historicTaskInstance)
    {
        TaskEventEntity taskEventEntity = customTaskActivityRepository.findByExecutionIdAndTaskId(
            historicTaskInstance.getExecutionId(), historicTaskInstance.getTaskId()
        );
        if (taskEventEntity != null)
        {
            taskEventEntity.setEndTime(
                historicTaskInstance.getEndTime() != null ? historicTaskInstance.getEndTime() : new Date()
            );
            customTaskActivityRepository.save(taskEventEntity);
        }
    }

    /**
     * Getting process definition name from process def key
     *
     * @param processDefinitionKey process definition key
     * @return process definition name as String
     */
    private String fetchProcessDefinitionNameFromDefKey(String processDefinitionKey)
    {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();

        ProcessDefinition processDefinition = repositoryService
            .createProcessDefinitionQuery()
            .processDefinitionKey(processDefinitionKey)
            .active()
            .latestVersion()
            .singleResult();

        return processDefinition.getName();
    }

    /**
     * Fetch candidate users by task identifier
     *
     * @param taskId task identifier
     * @return String value of candidate users
     */
    private String fetchCandidateUsersByTaskId(String taskId)
    {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();

        List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(taskId);
        if (identityLinks != null && !identityLinks.isEmpty())
        {
            String candidateUsers = identityLinks.stream()
                .filter(identityLink -> identityLink.getType().equals(IdentityLinkType.CANDIDATE))
                .map(IdentityLink::getUserId)
                .collect(Collectors.joining(","));

            if (StringUtils.isNotBlank(candidateUsers)) {
                return candidateUsers;
            }
        }
        return null;
    }

    /**
     * Fetch customer and product relevant data
     *
     * @param taskEventEntity task event entity reference
     * @param historicTaskInstance historic task instance
     */
    @SuppressWarnings("unchecked")
    private void fetchCustomerAndProductRelevantData(TaskEventEntity taskEventEntity,
                                                     HistoricTaskInstanceEventEntity historicTaskInstance)
    {
        String processInstanceId = historicTaskInstance.getProcessInstanceId();
        Map<String, Long> vars = (Map<String, Long>) variableMapping.get(processInstanceId);
        if (vars != null && !vars.isEmpty())
        {
            taskEventEntity.setCustomerId(
                getCustomerIdFromSourceIfNotExists(vars.get(CUSTOMER_ID), processInstanceId)
            );
            taskEventEntity.setProductId(vars.get(PRODUCT_ID));
        }
        else
        {
            TaskEventEntity storedTaskEntity = customTaskActivityRepository
                .findFirstByProcessInstanceIdOrSuperProcessInstanceId(processInstanceId, processInstanceId);

            taskEventEntity.setCustomerId(storedTaskEntity.getCustomerId());
            taskEventEntity.setProductId(storedTaskEntity.getProductId());
        }
    }

    /**
     * Getting customer identifier from source if not exists in variable mapping
     *
     * @param customerId customer identifier
     * @param processInstanceId process instance identifier
     * @return Long value of customer id
     */
    private Long getCustomerIdFromSourceIfNotExists(Long customerId, String processInstanceId)
    {
        if (customerId == null)
        {
            TaskEventEntity taskEventEntity = customTaskActivityRepository
                .findFirstByProcessInstanceIdOrSuperProcessInstanceId(processInstanceId, processInstanceId);

            return taskEventEntity.getCustomerId();
        }
        return customerId;
    }

    /**
     * Updating super process instance of historic task if exists
     *
     * @param processInstanceId process instance identifier
     * @param superProcessInstanceId super process instance identifier
     */
    private void updateHistoricTaskSuperProcessInstance(String processInstanceId, String superProcessInstanceId)
    {
        TaskEventEntity historicTaskEvent = customTaskActivityRepository.findFirstByProcessInstanceId(processInstanceId);
        if (historicTaskEvent != null)
        {
            historicTaskEvent.setSuperProcessInstanceId(superProcessInstanceId);
            customTaskActivityRepository.save(historicTaskEvent);
        }
    }
}
