package org.example.scripting;

import org.apache.commons.lang3.tuple.Pair;
import org.example.model.VulnerabilityScript;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class ExecutionEngine implements Consumer<List<VulnerabilityScript>> {

    private final Function<List<VulnerabilityScript>, Pair<List<Integer>, Map<Integer, VulnerabilityScript>>> executionPlan;

    public ExecutionEngine(Function<List<VulnerabilityScript>, Pair<List<Integer>, Map<Integer, VulnerabilityScript>>> executionPlan) {
        this.executionPlan = executionPlan;
    }

    @Override
    public void accept(List<VulnerabilityScript> scripts) {
        Pair<List<Integer>, Map<Integer, VulnerabilityScript>> pair = executionPlan.apply(scripts);

        List<Integer> order = pair.getLeft();
        Map<Integer, VulnerabilityScript> scriptMap = pair.getRight();

        for (Integer integer : order) {
            execute(scriptMap.get(integer));
        }
    }

    // execution mock
    private void execute(VulnerabilityScript script) {
        System.out.println("Executing script with id: " + script.getScriptId());
    }
}
