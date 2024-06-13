package ua.mai.zyme.reactor;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.IntStream;


public class MonoAndFlux {

    private static final Logger logger = LogManager.getLogger(MonoAndFlux.class);

    static public void example001_range() {
        Flux.range(1, 4)
            .subscribe(System.out::println);
// Result:
// 1
// 2
// 3
// 4
    }

    static public void example002_fromIterable() {
        Flux.fromIterable(Arrays.asList(5,7,8))
            .subscribe(System.out::println);
// Result:
// 5
// 7
// 8
    }

    static public void example003_generate_delayElements_take() throws InterruptedException {
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

    static public void example004_generate() {
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

    static public void example005_create_with_producer_BaseSubscriber() {
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

    static public void example006_create_onRequest() {
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

    static public void example007_create_onRequest_producer() {
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

    static public void example008_create_producer_onRequest() {
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

    static public void example009_zipWith() {
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

    static public void example010_interval_skip_take() throws InterruptedException {
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

    static public void example011_just_onErrorResume() throws InterruptedException {
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

    static public void example012_subscribe() {
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

    static public void example013_index() {
        Flux.range(2018, 3)
                .index()
                .subscribe(
                        e -> logger.info("e: {}; index: {};  value: {}",
                                e,
                                e.getT1(),
                                e.getT2()));
// Result:
// e: [0,2018]; index: 0;  value: 2018
// e: [1,2019]; index: 1;  value: 2019
// e: [2,2020]; index: 2;  value: 2020
    }

    static public void example014_timestamp() {
        Flux.range(2018, 3)
                .timestamp()
                .subscribe(
                        e -> logger.info("e: {}; index: {}; timestamp: {}; value: {}",
                                e,
                                e.getT1(),
                                Instant.ofEpochMilli(e.getT1()),
                                e.getT2()));
// Result:
// e: [1717673374055,2018]; index: 1717673374055; timestamp: 2024-06-06T11:29:34.055Z; value: 2018
// e: [1717673374059,2019]; index: 1717673374059; timestamp: 2024-06-06T11:29:34.059Z; value: 2019
// e: [1717673374059,2020]; index: 1717673374059; timestamp: 2024-06-06T11:29:34.059Z; value: 2020
    }

    static public void example015_index_timestamp() {
        Flux.range(2018, 3)
                .timestamp()
                .index()
                .subscribe(e ->
                        logger.info("e: {}; index: {}, timestamp: {}, value: {}",
                                e,
                                e.getT1(),
                                Instant.ofEpochMilli(e.getT2().getT1()),
                                e.getT2().getT2()));
// Result:
// e: [0,[1717673783792,2018]]; index: 0, timestamp: 2024-06-06T11:36:23.792Z, value: 2018
// e: [1,[1717673783798,2019]]; index: 1, timestamp: 2024-06-06T11:36:23.798Z, value: 2019
// e: [2,[1717673783798,2020]]; index: 2, timestamp: 2024-06-06T11:36:23.798Z, value: 2020
    }

    static public void example016_skipUntilOther_takeUntilOther_startStopStreamProcessing() throws InterruptedException {
        Mono<?> startCommand = Mono.delay(Duration.ofSeconds(1));
        Mono<?> stopCommand = Mono.delay(Duration.ofSeconds(3));
        Flux<Long> streamOfData = Flux.interval(Duration.ofMillis(100));

        streamOfData
                .skipUntilOther(startCommand)
                .takeUntilOther(stopCommand)
                .subscribe(System.out::println);

        Thread.sleep(4000);
    }

    static public void example17_concat() {
        Flux.concat(
                Flux.range(1, 1),
                Flux.range(4, 2),
                Flux.range(6, 1)
        ).subscribe(e -> logger.info("onNext: {}", e));
// Result:
// onNext: 1
// onNext: 4
// onNext: 5
// onNext: 6
    }

    static public void example18_buffer() {
        Flux.range(1, 7)
                .buffer(4)
                .subscribe(e -> logger.info("onNext: {}", e));
// Result:
// onNext: [1, 2, 3, 4]
// onNext: [5, 6, 7]
    }

    public static boolean isPrime(int number) {
        return number > 2
                && IntStream.rangeClosed(2, (int) Math.sqrt(number))
                            .noneMatch(n -> (number % n == 0));
    }

    static public void example19_windowUntil_window() {
        Flux<Flux<Integer>> fluxFlux =
                Flux.range(101, 8)
                    .windowUntil(elem -> isPrime(elem), true);

        fluxFlux.subscribe(window -> window.collectList()
                                           .subscribe(e -> logger.info("window: {}", e))
        );
// Result:
// window: []
// window: [101, 102]
// window: [103, 104, 105, 106]
// window: [107, 108]
    }

// TODO Результат???
//    static public void example20_groupBy() {
//        Flux<String> flux = Flux.just("apple", "banana", "apricot", "orange", "avocado", "blueberry", "blackberry");
//
//        Flux<GroupedFlux<Character, String>> groupedFlux = flux.groupBy(s -> s.charAt(0));
//
//        groupedFlux.subscribe(group -> {
//            logger.info("Processing group: {}", group.key());
//            group.subscribe(
//                    item -> logger.info("Item: {}", item),
//                    error -> logger.error("Error: ", error),
//                    () -> logger.info("Completed group: {}", group.key())
//            );
//        });
//// Result:
////
//    }

    static public void example21_groupBy_reduce() {
        Flux.range(1, 7)
                .groupBy(e -> e % 2 == 0 ? "Even" : "Odd")
                .subscribe(groupFlux -> groupFlux
                        .reduce(new LinkedList<>(),
                                (list, elem) -> {
                                    list.add(elem);
                                    return list;
                                })
//                        .filter(arr -> !arr.isEmpty())
                        .subscribe(data ->
                                logger.info("{}: {}", groupFlux.key(), data)));
// Result:
// Even: [2, 4, 6]
// Odd: [1, 3, 5, 7]
    }

    static public void example22_groupBy_scan() {
        Flux.range(1, 5)
                .groupBy(e -> e % 2 == 0 ? "Even" : "Odd")
                .subscribe(groupFlux -> groupFlux
                        .scan(
                                new LinkedList<>(),
                                (list, elem) -> {
                                    list.add(elem);
                                    return list;
                                })
                        .filter(arr -> !arr.isEmpty())
                        .subscribe(data -> logger.info("{}: {}", groupFlux.key(), data)));
// Result:
// Odd: [1]
// Even: [2]
// Odd: [1, 3]
// Even: [2, 4]
// Odd: [1, 3, 5]
    }

    static public void example23_groupBy_scan() {
        Flux.range(1, 5)
                .groupBy(e -> e % 2 == 0 ? "Even" : "Odd")
                .subscribe(groupFlux -> groupFlux
                        .scan(
                                new LinkedList<>(),
                                (list, elem) -> {
                                    if (list.size() > 1) {
                                        list.remove(0);
                                    }
                                    list.add(elem);
                                    return list;
                                })
                        .filter(arr -> !arr.isEmpty())
                        .subscribe(data -> logger.info("{}: {}", groupFlux.key(), data)));
// Result:
// Odd: [1]
// Even: [2]
// Odd: [1, 3]
// Even: [2, 4]
// Odd: [3, 5]
    }

    static public void example01_() {
        Flux<Integer> flux = Flux.generate(
                () -> 1, // начальное состояние
                (state, sink) -> {
                    sink.next(state); // добавить элемент в поток
                    if (state >= 10) {
                        sink.complete(); // завершить поток при достижении значения 10
                    }
                    return state + 1; // обновить состояние
                }
        );

        flux.subscribe(
                item -> System.out.println("Received: " + item),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Completed")
        );
        // Result:
//
    }

}
