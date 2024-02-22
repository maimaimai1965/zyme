package ua.mai.zyme.reactor;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;


public class MonoFlux {

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

    static private void example005_create_with_producer() {
        Flux<Object> producer = Flux.generate(
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
                sink -> producer.subscribe(
                        new BaseSubscriber<Object>() {
                            @Override
                            protected void hookOnNext(Object value) {
                                sink.next(value);
                            }

                            @Override
                            protected void hookOnComplete() {
                                sink.complete();
                            }
                        })
             )
            .subscribe(System.out::println);
// Result:
// OK 25
// OK 27
// OK 29
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
                .zipWith(num, (first, second) -> String.format("%s %s", first, second));
        sum.subscribe(System.out::println);
// Result:
// Java One
// C Two
// C++ Three
// Python One
// Javascript Two
    }

    static private void example00_() {
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
//        example005_create_with_producer();
//        example006_create_onRequest();
//        example007_create_onRequest_producer();
//        example008_create_producer_onRequest();
        example009_zipWith();

//        Thread.sleep(3000L);
    }

}
