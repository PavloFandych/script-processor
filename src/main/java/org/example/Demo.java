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
                        new VulnerabilityScript(3, List.of(4, 5)),
                        new VulnerabilityScript(2, List.of(3, 4)),
                        new VulnerabilityScript(5, List.of()),
                        new VulnerabilityScript(1, List.of(2, 3)),
                        new VulnerabilityScript(4, List.of(5))
                )
        );
    }
}