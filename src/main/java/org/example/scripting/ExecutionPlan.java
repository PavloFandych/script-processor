package org.example.scripting;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.example.model.VulnerabilityScript;

import java.util.*;
import java.util.stream.Collectors;

public class ExecutionPlan {

    public Pair<List<Integer>, Map<Integer, VulnerabilityScript>> getPlan(List<VulnerabilityScript> scripts) {
        if (CollectionUtils.isEmpty(scripts)) {
            throw new IllegalStateException("Scripts list is null or empty");
        }

        List<Integer> result = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();

        Map<Integer, VulnerabilityScript> scriptMap = prepareScriptMap(scripts);

        for (VulnerabilityScript script : scripts) {
            if (script != null && !visited.contains(script.getScriptId())) {
                run(script, scriptMap, result, visited);
            }
        }

        return Pair.of(Collections.unmodifiableList(result), Collections.unmodifiableMap(scriptMap));
    }

    private void run(VulnerabilityScript script,
                     Map<Integer, VulnerabilityScript> scriptMap,
                     List<Integer> result,
                     Set<Integer> visited) {
        if (script == null || visited.contains(script.getScriptId())) {
            return;
        }

        visited.add(script.getScriptId());

        List<Integer> dependencies = script.getDependencies();
        if (dependencies != null) {
            for (int dependencyId : dependencies) {
                VulnerabilityScript dependency = scriptMap.get(dependencyId);
                if (dependency == null) {
                    throw new IllegalStateException("Missing dependency script with id: " + dependencyId);
                }
                if (!visited.contains(dependencyId)) {
                    run(dependency, scriptMap, result, visited);
                }
            }
        }

        result.add(script.getScriptId());
    }

    private Map<Integer, VulnerabilityScript> prepareScriptMap(List<VulnerabilityScript> scripts) {
        return scripts.stream()
                .collect(
                        Collectors.toMap(VulnerabilityScript::getScriptId,
                                script -> script, (a, b) -> b)
                );
    }
}

