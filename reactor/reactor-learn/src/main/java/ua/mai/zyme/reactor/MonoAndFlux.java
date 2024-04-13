package ua.mai.zyme.reactor;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;


public class MonoAndFlux {

    static private void example001_range() {
        Flux.range(1, 4)
            .subscribe(System.out::println);
// Result:
// 1
// 2
// 3
// 4
    }

    static private void example002_fromIterable() {
        Flux.fromIterable(Arrays.asList(5,7,8))
            .subscribe(System.out::println);
// Result:
// 5
// 7
// 8
    }

    static private void example003_generate_delayElements_take() throws InterruptedException {
        Flux.<String>generate(sink -> {
                    sink.next("OK");
                })
                .delayElements(Duration.ofMillis(300))
                .take(3)
                .subscribe(System.out::println);
        Thread.sleep(1500L);
// Result:
// OK
// OK
// OK
    }

    static private void example004_generate() {
        Flux.generate(
                () -> 25,
                (state, sink) -> {
                    if (state > 30) {
                        sink.complete();
                    } else {
                        sink.next("OK " + state);
                    }
                    return state + 2;
                }
            )
            .subscribe(System.out::println);
// Result:
// OK 25
// OK 27
// OK 29
    }

    static private void example005_create_with_producer_BaseSubscriber() {
        Flux<Object> outerProducer = Flux.generate(
                () -> 25,
                (state, sink) -> {
                    if (state > 30) {
                        sink.complete();
                    } else {
                        sink.next("OK " + state);
                    }
                    return state + 2;
                }
        );
        Flux.create(
                sink -> outerProducer.subscribe(
                        new BaseSubscriber<Object>() {
                            @Override
                            protected void hookOnNext(Object value) {
                                sink.next("From outer: " + value);
                            }

                            @Override
                            protected void hookOnComplete() {
                                sink.complete();
                            }
                        })
             )
            .subscribe(System.out::println);
// Result:
// From outer: OK 25
// From outer: OK 27
// From outer: OK 29
    }

    static private void example006_create_onRequest() {
        Flux<Object> producer = Flux.generate(
                () -> 25,
                (state, sink) -> {
                    if (state > 30) {
                        sink.complete();
                    } else {
                        sink.next("Step " + state);
                    }
                    return state + 2;
                }
        );
        Flux.create(
                sink -> sink.onRequest(r -> {
                    sink.next("DB return: " + producer.next().block());
                })
             )
            .subscribe(System.out::println);
// Result:
// DB return: Step 25
    }

    static private void example007_create_onRequest_producer() {
        Flux<Object> producer = Flux.generate(
                () -> 25,
                (state, sink) -> {
                    if (state > 30) {
                        sink.complete();
                    } else {
                        sink.next("Step " + state);
                    }
                    return state + 2;
                }
        );
        Flux
//            .push(    // синхронный
            .create(  // асинхронный
                    sink -> {
                        sink.onRequest(r -> {
                            sink.next("DB return: " + producer.next().block());
                        });
                        producer.subscribe(
                                new BaseSubscriber<Object>() {
                                    @Override
                                    protected void hookOnNext(Object value) {
                                        sink.next(value);
                                    }

                                    @Override
                                    protected void hookOnComplete() {
                                        sink.complete();
                                    }
                                });
                    }
             )
            .subscribe(System.out::println);
// Result:
// DB return: Step 25
// Step 25
// Step 27
// Step 29
    }

    static private void example008_create_producer_onRequest() {
        Flux<Object> producer = Flux.generate(
                () -> 25,
                (state, sink) -> {
                    if (state > 30) {
                        sink.complete();
                    } else {
                        sink.next("Step " + state);
                    }
                    return state + 2;
                }
        );
        Flux
//            .push(    // синхронный
            .create(  // асинхронный
                    sink -> {
                        producer.subscribe(
                                new BaseSubscriber<Object>() {
                                    @Override
                                    protected void hookOnNext(Object value) {
                                        sink.next(value);
                                    }

                                    @Override
                                    protected void hookOnComplete() {
                                        sink.complete();
                                    }
                                });
                        sink.onRequest(r -> {
                            sink.next("DB return: " + producer.next().block());
                        });
                        sink.complete();
                    }
             )
            .subscribe(System.out::println);
// Result:
// Step 25
// Step 27
// Step 29
// DB return: Step 25
    }

    static private void example009_zipWith() {
        Flux<String> num = Flux
                .just("One", "Two", "Three")
                .repeat();
        Flux<String> sum = Flux
                .just("Java", "C", "C++", "Python", "Javascript")
                // Объединяет поток sum c потоком num (его элементы берутся ротационно - используется repeat()).
                // Если для num не использовать repeat() - выведется всего 3 строки (по количеству в num).
                .zipWith(num, (first, second) -> String.format("%s %s", first, second));
        sum.subscribe(System.out::println);
// Result:
// Java One
// C Two
// C++ Three
// Python One
// Javascript Two
    }

    static private void example010_interval_skip_take() throws InterruptedException {
        Flux
                .interval(Duration.ofMillis(100))
                .map(String::valueOf)
                .skip(2)
                .take(3)
                .subscribe(System.out::println);
        Thread.sleep(1000L);
// Result:
// 2
// 3
// 4
    }

    static private void example011_just_onErrorResume() throws InterruptedException {
        Flux
                .just("One", "Two", "Three")
                .delayElements(Duration.ofMillis(100)) // Задержка на получение элементов. Вызывает необходимость использования Thread.sleep(1000L).
                .timeout(Duration.ofMillis(50))        // Таймаут на получение элемента.
                .retry(3)                   // Количество попыток получения элемента.
                .onErrorResume(throwable ->
                                Flux.just("Err A", "Err B"))
                .subscribe(System.out::println);
        Thread.sleep(1000L);
// Result:
// Err A
// Err B
    }

    static private void example012_subscribe() {
        Flux stringFlux = Flux.just("One", "Two", "Three");
        stringFlux.subscribe(
                value -> System.out.println(value),
                error -> System.err.println(error),
                () -> System.out.println("Finished")
        );
// Result:
// One
// Two
// Three
// Finished
    }

    static private void example01_() {
// Result:
//
    }

    public static void main(String[] args) throws InterruptedException {
        Mono.empty();
        Flux.empty();

        Mono<Integer> mono = Mono.just(1);
        Flux<Integer> flux = Flux.just(1, 2, 3);

        Flux<Integer> fluxFromMono = mono.flux();
        Mono<Integer> monoFromFlux = flux.elementAt(1);

//        example001_range();
//        example002_fromIterable();
//        example003_generate_delayElements_take();
//        example004_generate();
        example005_create_with_producer_BaseSubscriber();
//        example006_create_onRequest();
//        example007_create_onRequest_producer();
//        example008_create_producer_onRequest();
//        example009_zipWith();
//        example010_interval_skip_take();
//        example010_interval_skip_take();
//        example011_just_onErrorResume();
//        example012_subscribe();

//        Thread.sleep(3000L);
    }

}
