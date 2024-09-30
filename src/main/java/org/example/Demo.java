package org.example;

import org.example.model.VulnerabilityScript;
import org.example.scripting.ExecutionEngine;
import org.example.scripting.ExecutionPlan;

import java.util.List;
import java.util.function.Consumer;

public class Demo {

    public static void main(String[] args) {
        List<VulnerabilityScript> scripts = List.of(
                new VulnerabilityScript(1, List.of(2, 3)),
                new VulnerabilityScript(2, List.of(3)),
                new VulnerabilityScript(3, List.of())
        );

        Consumer<List<VulnerabilityScript>> executionEngine = new ExecutionEngine(new ExecutionPlan());
        executionEngine.accept(scripts);
    }
}