package org.example.scripting;

import org.example.model.VulnerabilityScript;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExecutionPlanTest {

    private static final ExecutionPlan EXECUTION_PLAN = new ExecutionPlan();

    @Test
    void getPlanNullInputTest() {
        // given
        List<VulnerabilityScript> scripts = null;

        //when then
        assertThatThrownBy(() -> EXECUTION_PLAN.getPlan(scripts)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Scripts list is null or empty");
    }

    @Test
    public void getPlanWhenDependencyIsMissingTest() {
        // given
        List<VulnerabilityScript> scripts = List.of(
                new VulnerabilityScript(1, List.of(2, 3)),
                new VulnerabilityScript(2, List.of()) // script 3 does not exist
        );

        // when then
        assertThatThrownBy(() -> EXECUTION_PLAN.getPlan(scripts)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Missing dependency script with id: 3");

    }

    @Test
    void getPlanWhenDependencyListIsNullTest() {
        // given when then
        assertThatThrownBy(() -> {
            List<VulnerabilityScript> scripts = List.of(
                    new VulnerabilityScript(1, List.of(2, 3)),
                    new VulnerabilityScript(2, null),
                    new VulnerabilityScript(3, null)
            );
            EXECUTION_PLAN.getPlan(scripts);
        }).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("dependency list is null");
    }

    @Test
    void getPlanHappyPathTest() {
        //given
        List<VulnerabilityScript> scripts = List.of(
                new VulnerabilityScript(1, List.of(2, 3)),
                new VulnerabilityScript(2, List.of(4)),
                new VulnerabilityScript(3, List.of()),
                new VulnerabilityScript(4, List.of())
        );

        //when
        List<Integer> result = EXECUTION_PLAN.getPlan(scripts).getLeft();

        //then
        assertThat(result).isNotNull().isNotEmpty();
        assertThat(result).containsExactlyElementsOf(List.of(4, 2, 3, 1));
    }

    @Test
    void getPlanCircularDependencyTest() {
        //given
        List<VulnerabilityScript> scripts = List.of(
                new VulnerabilityScript(1, List.of(2)),
                new VulnerabilityScript(2, List.of(1))
        );

        //when
        List<Integer> result = EXECUTION_PLAN.getPlan(scripts).getLeft();

        //then
        assertThat(result).isNotNull().isNotEmpty();
        assertThat(result).containsExactlyElementsOf(List.of(2, 1));
    }

    /**
     * The ExecutionPlan#getOrder() method is designed to be thread-safe.
     * This test is intended to prove that.
     */
    @Test
    void getPlanThreadSafetyTest() throws InterruptedException, ExecutionException {
        // given
        List<VulnerabilityScript> scriptsOne = List.of(
                new VulnerabilityScript(1, List.of(2, 3)),
                new VulnerabilityScript(2, List.of(4)),
                new VulnerabilityScript(3, List.of()),
                new VulnerabilityScript(4, List.of())
        );
        List<VulnerabilityScript> scriptsTwo = List.of(
                new VulnerabilityScript(1, List.of(2)),
                new VulnerabilityScript(2, List.of())
        );

        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(2);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            // when
            Future<List<Integer>> scriptsOneFuture = executor.submit(() -> {
                try {
                    startGate.await();
                    System.out.println("scriptsOne start time: " + System.currentTimeMillis());
                    return EXECUTION_PLAN.getPlan(scriptsOne).getLeft();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    endGate.countDown();
                }
            });
            Future<List<Integer>> scriptsTwoFuture = executor.submit(() -> {
                try {
                    startGate.await();
                    System.out.println("scriptsTwo start time: " + System.currentTimeMillis());
                    return EXECUTION_PLAN.getPlan(scriptsTwo).getLeft();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    endGate.countDown();
                }
            });

            startGate.countDown();
            endGate.await();

            // then
            assertThat(scriptsOneFuture.get()).isNotNull().isNotEmpty();
            assertThat(scriptsOneFuture.get()).containsExactlyElementsOf(List.of(4, 2, 3, 1));

            assertThat(scriptsTwoFuture.get()).isNotNull().isNotEmpty();
            assertThat(scriptsTwoFuture.get()).containsExactlyElementsOf(List.of(2, 1));
        }
    }

    @Test
    void getPlanWithDependencyDuplicationTest() {
        //given
        List<VulnerabilityScript> scripts = List.of(
                new VulnerabilityScript(1, List.of(2, 3, 3)),
                new VulnerabilityScript(2, List.of(3)),
                new VulnerabilityScript(3, List.of())
        );

        //when
        List<Integer> result = EXECUTION_PLAN.getPlan(scripts).getLeft();

        //then
        assertThat(result).isNotNull().isNotEmpty();
        assertThat(result).containsExactlyElementsOf(List.of(3, 2, 1));
    }
}
