package com.waes.phillips.products.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

@Configuration
@Slf4j
public class ResilienceConfiguration {

    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    public ResilienceConfiguration(@Value("${cb.sliding.window.size}") int slidingWindowSize,
                                   @Value("${cb.failure.rate.threshold}") float failureRateThreshold,
                                   @Value("${cb.wait.duration.in.open.state}") long waitDurationInOpenState,
                                   @Value("${cb.permitted.number.of.calls.in.half.open.state}") int permittedNumberOfCallsInHalfOpenState,
                                   @Value("${cb.name}") String cbName,
                                   @Value("${retry.maxAttempts}") int retryMaxAttempts,
                                   @Value("${retry.waitDuration}") long retryWaitDuration,
                                   @Value("${retry.name}") String retryName) {

        circuitBreaker = instantiateCircuitBreaker(slidingWindowSize, failureRateThreshold, waitDurationInOpenState, permittedNumberOfCallsInHalfOpenState, cbName);
        retry = instantiateRetry(retryMaxAttempts, retryWaitDuration, retryName);
    }

    /**
     * Instantiate the {@link CircuitBreaker}.
     *
     * @param slidingWindowSize
     * @param failureRateThreshold
     * @param waitDurationInOpenState
     * @param permittedNumberOfCallsInHalfOpenState
     * @param cbName
     * @return {@link CircuitBreaker}
     */
    private CircuitBreaker instantiateCircuitBreaker(int slidingWindowSize, float failureRateThreshold, long waitDurationInOpenState, int permittedNumberOfCallsInHalfOpenState, String cbName) {
        final CircuitBreaker circuitBreaker;
        // If 70% of the requests fail the circuit will get opened. After 10 seconds it will let 4 requests pass to check the healthiness of the 3rd party API.
        CircuitBreakerConfig cbConfig = CircuitBreakerConfig
                .custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(slidingWindowSize)
                .failureRateThreshold(failureRateThreshold)
                .waitDurationInOpenState(Duration.ofSeconds(waitDurationInOpenState))
                .permittedNumberOfCallsInHalfOpenState(permittedNumberOfCallsInHalfOpenState)
                .build();

        CircuitBreakerRegistry cbRegistry = CircuitBreakerRegistry.of(cbConfig);
        circuitBreaker = cbRegistry.circuitBreaker(cbName);
        return circuitBreaker;
    }

    /**
     * Instantiate the {@link Retry}.
     *
     * @param retryMaxAttempts
     * @param retryWaitDuration
     * @param retryName
     * @return {@link Retry}
     */
    private Retry instantiateRetry(int retryMaxAttempts, long retryWaitDuration, String retryName) {
        final Retry retry;
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(retryMaxAttempts)
                .waitDuration(Duration.of(retryWaitDuration, SECONDS))
                .build();

        RetryRegistry retryRegistry = RetryRegistry.of(retryConfig);
        retry = retryRegistry.retry(retryName);
        return retry;
    }

    /**
     * Get a {@link Retry} bean.
     *
     * @return {@link Retry}
     */
    @Bean
    public Retry regittry() {
        return retry;
    }
    @Bean
    public RegistryEventConsumer<CircuitBreaker> circuitBreakerEventConsumer() {
        return new RegistryEventConsumer<CircuitBreaker>() {

            @Override
            public void onEntryAddedEvent(EntryAddedEvent<CircuitBreaker> entryAddedEvent) {
                entryAddedEvent.getAddedEntry().getEventPublisher()
                        .onFailureRateExceeded(event -> log.error("circuit breaker {} failure rate {} on {}",
                                event.getCircuitBreakerName(), event.getFailureRate(), event.getCreationTime())
                        )
                        .onSlowCallRateExceeded(event -> log.error("circuit breaker {} slow call rate {} on {}",
                                event.getCircuitBreakerName(), event.getSlowCallRate(), event.getCreationTime())
                        )
                        .onCallNotPermitted(event -> log.error("circuit breaker {} call not permitted {}",
                                event.getCircuitBreakerName(), event.getCreationTime())
                        )
                        .onError(event -> log.error("circuit breaker {} error with duration {}s",
                                event.getCircuitBreakerName(), event.getElapsedDuration().getSeconds())
                        )
                        .onStateTransition(
                                event -> log.warn("circuit breaker {} state transition from {} to {} on {}",
                                        event.getCircuitBreakerName(), event.getStateTransition().getFromState(),
                                        event.getStateTransition().getToState(), event.getCreationTime())
                        );
            }

            @Override
            public void onEntryRemovedEvent(EntryRemovedEvent<CircuitBreaker> entryRemoveEvent) {
                entryRemoveEvent.getRemovedEntry().getEventPublisher()
                        .onFailureRateExceeded(event -> log.debug("Circuit breaker event removed {}",
                                event.getCircuitBreakerName()));
            }

            @Override
            public void onEntryReplacedEvent(EntryReplacedEvent<CircuitBreaker> entryReplacedEvent) {
                entryReplacedEvent.getNewEntry().getEventPublisher()
                        .onFailureRateExceeded(event -> log.debug("Circuit breaker event replaced {}",
                                event.getCircuitBreakerName()));
            }
        };
    }

    @Bean
    public RegistryEventConsumer<TimeLimiter> timeLimiterEventConsumer() {
        return new RegistryEventConsumer<TimeLimiter>() {
            @Override
            public void onEntryAddedEvent(EntryAddedEvent<TimeLimiter> entryAddedEvent) {
                entryAddedEvent.getAddedEntry().getEventPublisher()
                        .onTimeout(event -> log.error("time limiter {} timeout {} on {}",
                                event.getTimeLimiterName(), event.getEventType(), event.getCreationTime())
                        );
            }

            @Override
            public void onEntryRemovedEvent(EntryRemovedEvent<TimeLimiter> entryRemoveEvent) {
                entryRemoveEvent.getRemovedEntry().getEventPublisher()
                        .onTimeout(event -> log.error("time limiter removed {}",
                                event.getTimeLimiterName())
                        );
            }

            @Override
            public void onEntryReplacedEvent(EntryReplacedEvent<TimeLimiter> entryReplacedEvent) {
                entryReplacedEvent.getNewEntry().getEventPublisher()
                        .onTimeout(event -> log.error("time limiter replaced {} ",
                                event.getTimeLimiterName())
                        );
            }
        };
    }
}
