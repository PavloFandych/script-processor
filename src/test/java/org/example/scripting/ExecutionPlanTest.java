package org.example.scripting;

import org.example.model.VulnerabilityScript;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExecutionPlanTest {

    private static final ExecutionPlan EXECUTION_PLAN = new ExecutionPlan();

    @Test
    void getOrderNullInputTest() {
        // given
        List<VulnerabilityScript> scripts = null;

        //when then
        assertThatThrownBy(() -> EXECUTION_PLAN.getOrder(scripts)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Scripts list is null or empty");
    }

    @Test
    public void getOrderWhenDependencyIsMissingTest() {
        // given
        List<VulnerabilityScript> scripts = List.of(
                new VulnerabilityScript(1, List.of(2, 3)),
                new VulnerabilityScript(2, List.of()) // script 3 does not exist
        );

        // when then
        assertThatThrownBy(() -> EXECUTION_PLAN.getOrder(scripts)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Missing dependency script with id: 3");

    }

    @Test
    void getOrderWhenDependencyListIsNullTest() {
        // given when then
        assertThatThrownBy(() -> {
            List<VulnerabilityScript> scripts = List.of(
                    new VulnerabilityScript(1, List.of(2, 3)),
                    new VulnerabilityScript(2, null),
                    new VulnerabilityScript(3, null)
            );
            EXECUTION_PLAN.getOrder(scripts);
        }).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("dependency list is null");
    }

    @Test
    void getOrderHappyPathTest() {
        //given
        List<VulnerabilityScript> scripts = List.of(
                new VulnerabilityScript(1, List.of(2, 3)),
                new VulnerabilityScript(2, List.of(4)),
                new VulnerabilityScript(3, List.of()),
                new VulnerabilityScript(4, List.of())
        );

        //when
        List<Integer> result = EXECUTION_PLAN.getOrder(scripts);

        //then
        assertThat(result).isNotNull().isNotEmpty();
        assertThat(result).containsExactlyElementsOf(List.of(4, 2, 3, 1));
    }

    @Test
    void getOrderCircularDependencyTest() {
        //given
        List<VulnerabilityScript> scripts = List.of(
                new VulnerabilityScript(1, List.of(2)),
                new VulnerabilityScript(2, List.of(1))
        );

        //when
        List<Integer> result = EXECUTION_PLAN.getOrder(scripts);

        //then
        assertThat(result).isNotNull().isNotEmpty();
        assertThat(result).containsExactlyElementsOf(List.of(2, 1));
    }
}
