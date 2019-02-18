package com.stackoverflow.demo.demo;

import org.junit.Test;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.test.StateMachineTestPlan;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;

public class TestEventNotAccepted {

    @Test
    public void testEventNotAccepted() throws Exception {
        StateMachine<String, String> machine = buildMachine();
        StateMachineTestPlan<String, String> plan =
                StateMachineTestPlanBuilder.<String, String>builder()
                        .defaultAwaitTime(2)
                        .stateMachine(machine)
                        .step()
                        .expectStates("SI")
                        .and()
                        .step()
                        .sendEvent("E2")
                        .and()
                        .build();
        plan.test();
    }

    private StateMachine<String, String> buildMachine() throws Exception {
        StateMachineBuilder.Builder<String, String> builder = StateMachineBuilder.builder();

        builder.configureConfiguration()
                .withConfiguration()
                .taskExecutor(new SyncTaskExecutor())
                .listener(customListener())
                .autoStartup(true);

        builder.configureStates()
                .withStates()
                .initial("SI")
                .state("S1")
                .state("S2");

        builder.configureTransitions()
                .withExternal()
                .source("SI").target("S1")
                .event("E1")
                .action(c -> c.getExtendedState().getVariables().put("key1", "value1"))
                .and()
                .withExternal()
                .source("S1").target("S2").event("E2");

        return builder.build();
    }

    private StateMachineListener<String, String> customListener() {
        return new StateMachineListenerAdapter<String, String>() {
            @Override
            public void eventNotAccepted(Message event) {
                System.out.println("EVENT NOT ACCEPTED: " + event);
            }
        };
    }
}
