package com.hedza06.camtask.services;

import com.hedza06.camtask.constants.VariableConstant;
import com.hedza06.camtask.entity.VariableStoreEntity;
import com.hedza06.camtask.repository.CustomVariableStoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomVariableStoreService {

    @Autowired
    private CustomVariableStoreRepository variableStoreRepository;

    /**
     * Getting custom historic variable store by process instance identifier
     *
     * @param processInstanceId process instance identifier
     * @return VariableStoreEntity Object
     */
    public VariableStoreEntity findByProcessInstance(String processInstanceId) {
        return variableStoreRepository.findByProcessInstanceId(processInstanceId);
    }

    /**
     * Save historic variable store
     *
     * @param variableStore variable store object
     */
    public void save(VariableStoreEntity variableStore) {
        variableStoreRepository.save(variableStore);
    }

    /**
     * Updating store based on variable name
     *
     * @param variableStore existing variable store
     * @param name variable name
     * @param value variable value
     */
    public void updateStoreBasedOnVariableName(VariableStoreEntity variableStore, String name, Long value)
    {
        setupCustomerAndProductVariablesToStore(variableStore, name, value);
        save(variableStore);
    }

    /**
     * Creating new (persist) variable store
     *
     * @param name variable name
     * @param value variable value
     * @param processInstanceId process instance identifier
     */
    public void persistToVariableStore(String name, Long value, String processInstanceId)
    {
        VariableStoreEntity variableStore = new VariableStoreEntity(processInstanceId);
        setupCustomerAndProductVariablesToStore(variableStore, name, value);

        save(variableStore);
    }

    /**
     * Delete variable store by process instance identifier
     *
     * @param processInstanceId process instance identifier
     */
    public void deleteByProcessInstance(String processInstanceId) {
        variableStoreRepository.deleteByProcessInstanceId(processInstanceId);
    }

    /**
     * Setup customer and product variables to store
     *
     * @param variableStore existing variable store
     * @param name variable name
     * @param value variable long value
     */
    private void setupCustomerAndProductVariablesToStore(VariableStoreEntity variableStore,
                                                         String name, Long value)
    {
        if (name.equals(VariableConstant.CUSTOMER_ID)) {
            variableStore.setCustomerId(value);
        }
        if (name.equals(VariableConstant.PRODUCT_ID)) {
            variableStore.setProductId(value);
        }
    }
}
