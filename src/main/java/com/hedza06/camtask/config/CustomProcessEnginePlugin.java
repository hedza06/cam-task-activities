package com.hedza06.camtask.config;

import com.hedza06.camtask.handlers.CustomActivityHistoryEventHandler;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.impl.history.HistoryLevel;
import org.camunda.bpm.engine.impl.history.handler.CompositeDbHistoryEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CustomProcessEnginePlugin implements ProcessEnginePlugin {

    @Autowired
    private CustomActivityHistoryEventHandler customActivityHistoryEventHandler;


    @Override
    public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration)
    {
        List<HistoryLevel> customHistoryLevels = processEngineConfiguration.getCustomHistoryLevels();
        if (customHistoryLevels == null)
        {
            customHistoryLevels = new ArrayList<>();
            processEngineConfiguration.setCustomHistoryLevels(customHistoryLevels);
        }
        customHistoryLevels.add(CustomHistoryLevel.getInstance());
    }

    @Override
    public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration)
    {
        processEngineConfiguration.setHistoryEventHandler(
            new CompositeDbHistoryEventHandler(customActivityHistoryEventHandler)
        );
    }

    @Override
    public void postProcessEngineBuild(ProcessEngine processEngine) {
        // no implementation...
    }

}
