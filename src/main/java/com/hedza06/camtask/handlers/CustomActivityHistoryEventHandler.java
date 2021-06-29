package com.hedza06.camtask.handlers;

import com.hedza06.camtask.services.CustomTaskActivityService;
import org.camunda.bpm.engine.impl.history.event.HistoricTaskInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricVariableUpdateEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomActivityHistoryEventHandler implements HistoryEventHandler {

    @Autowired
    private CustomTaskActivityService customTaskActivityService;


    @Override
    public void handleEvent(HistoryEvent historyEvent)
    {
        if (historyEvent instanceof HistoricTaskInstanceEventEntity) {
            customTaskActivityService.processTaskInstanceEvent(historyEvent);
        }

        if (historyEvent instanceof HistoricVariableUpdateEventEntity) {
            customTaskActivityService.processVariableUpdateEvent(historyEvent);
        }
    }

    @Override
    public void handleEvents(List<HistoryEvent> historyEvents)
    {
        for (HistoryEvent historyEvent : historyEvents) {
            handleEvent(historyEvent);
        }
    }
}
