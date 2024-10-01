package org.example;

import org.example.model.VulnerabilityScript;
import org.example.scripting.ExecutionEngine;
import org.example.scripting.ExecutionPlan;

import java.util.List;

public class Demo {

    public static void main(String[] args) {
        new ExecutionEngine(
                new ExecutionPlan()).accept(
                List.of(
                        new VulnerabilityScript(1, List.of(2, 3)),
                        new VulnerabilityScript(2, List.of(3)),
                        new VulnerabilityScript(3, List.of())
                )
        );
    }
}