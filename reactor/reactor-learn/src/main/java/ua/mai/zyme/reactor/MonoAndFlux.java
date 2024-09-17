package ua.mai.zyme.reactor;

import org.reactivestreams.Publisher;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
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

    static public void example004_generate_with_state() {
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
        Flux<String> outerProducer = Flux.generate(
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
                            new BaseSubscriber<String>() {
                                    @Override
                                    protected void hookOnNext(String value) {
                                        sink.next("From outer: " + value);
                                    }

                                    @Override
                                    protected void hookOnComplete() {
                                        sink.complete();
                                    }
                                }
                        )
             )
            .subscribe(System.out::println);
// Result:
// From outer: OK 25
// From outer: OK 27
// From outer: OK 29
    }

    static public void example006_create_onRequest() {
        Flux<String> producer = Flux.generate(
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

    static public void example010_interval_map_skip_take() throws InterruptedException {
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

    static public void example24_generate_subscribe() {
        Flux<Integer> flux = Flux.generate(
                () -> 1, // начальное состояние
                (state, sink) -> {
                    sink.next(state); // добавить элемент в поток
                    if (state >= 4) {
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
// Received: 1
// Received: 2
// Received: 3
// Received: 4
// Completed
    }

    static public void example25_sample() throws InterruptedException {
        Flux.range(1, 100)
            .delayElements(Duration.ofMillis(1))
            .sample(Duration.ofMillis(200))
            .subscribe(e -> System.out.println("onNext: " + e));
        Thread.sleep(2000L);
// Result:
// onNext: 12
// onNext: 25
// onNext: 37
// onNext: 50
// onNext: 63
// onNext: 75
// onNext: 89
// onNext: 100
    }

    static public void example26_doOnEach() {
        Flux.just(1, 2, 3)
            .concatWith(Flux.error(new RuntimeException("Conn error")))
            .doOnEach(s -> System.out.println("signal: " + s))
            .subscribe();
// Result:
// signal: doOnEach_onNext(1)
//  signal: doOnEach_onNext(2)
//  signal: doOnEach_onNext(3)
//  signal: onError(java.lang.RuntimeException: Conn error)
    }

    static public void example27_materialize_dematerialize() {
        Flux.range(1, 3)
             .doOnNext(e -> System.out.println("data : " + e))
             .materialize()
             .doOnNext(e -> System.out.println("signal: " + e))
             .dematerialize()
             .collectList()
             .subscribe(r-> System.out.println("result: " + r));
// Result:
// data : 1
// signal: onNext(1)
// data : 2
// signal: onNext(2)
// data : 3
// signal: onNext(3)
// signal: onComplete()
// result: [1, 2, 3]
    }

    static public void example27_log() {
        Flux.range(1, 3)
                .log()
                .collectList()
                .subscribe(r-> System.out.println("result: " + r));
// Result:
// | onSubscribe([Synchronous Fuseable] FluxRange.RangeSubscription)
// | request(unbounded)
// | onNext(1)
// | onNext(2)
// | onNext(3)
// | onComplete()
// result: [1, 2, 3]
    }

    static public void example28_push() throws InterruptedException {
        Flux.push(emitter -> IntStream                   // (1)
                      .range(2000, 3000)                 // (1.1)
                      .forEach(emitter::next))           // (1.2)
            .delayElements(Duration.ofMillis(1))         // (2)
            .subscribe(e -> System.out.println("onNext: " + e));   // (3)
        Thread.sleep(6000L);
// Result:
    }

    static public void example29_generate_fibonacci() throws InterruptedException {
        Flux.generate(                                                          // (1)
                 () -> Tuples.of(0L, 1L),                                       // (1.1)
                 (state, sink) -> {                                             //
                     System.out.println("generated value: " + state.getT2());   //
                     sink.next(state.getT2());                                  // (1.2)
                     long newValue = state.getT1() + state.getT2();             //
                     return Tuples.of(state.getT2(), newValue);                 // (1.3)
                 })                                                             //
            .delayElements(Duration.ofMillis(1))                                // (2)
            .take(5)                                                            // (3)
            .subscribe(e -> System.out.println("onNext: " + e));                // (4)
        Thread.sleep(1000L);
// Result:
// generated value: 1
// onNext: 1
// generated value: 1
// onNext: 1
// generated value: 2
// onNext: 2
// generated value: 3
// onNext: 3
// generated value: 5
// onNext: 5
    }

    static public void example30_generate_fibonacci_list() throws InterruptedException {
        Flux.generate(
                        () -> Tuples.of(0L, 1L),
                        (state, sink) -> {
                            System.out.println("generated value: " + state.getT2());
                            sink.next(state.getT2());
                            long newValue = state.getT1() + state.getT2();
                            return Tuples.of(state.getT2(), newValue);
                        })
                .take(6)
                .collectList()
                .subscribe(l -> System.out.println("list: " + l));
// Result:
// generated value: 1
// generated value: 1
// generated value: 2
// generated value: 3
// generated value: 5
// generated value: 8
// list: [1, 1, 2, 3, 5, 8]
    }


    public static class Connection implements AutoCloseable {       // (1)
        private final Random rnd = new Random();

        public Iterable<String> getData() {                         // (2)
            if (rnd.nextInt(10) < 3) {                              // (2.1)
                throw new RuntimeException("Communication error");
            }
            return Arrays.asList("Some", "data");                   // (2.2)
        }
        public void close() { // (3)
            System.out.println("IO Connection closed");
        }
        public static Connection newConnection() { // (4)
            System.out.println("IO Connection created");
            return new Connection();
        }
    }

    static public void example31_using() {
        Flux<String> ioRequestResults = Flux.using(                    // (1)
                Connection::newConnection,                             // (1.1)
                connection -> Flux.fromIterable(connection.getData()), // (1.2)
                Connection::close                                      // (1.3)
        );
        ioRequestResults.subscribe(                                    // (2)
                data -> System.out.println("Received data: " + data),  //
                e -> System.out.println("Error: " + e.getMessage()),   //
                () -> System.out.println("Stream finished"));          //
// Result в случае успеха:
//   IO Connection created
//   Received data: Some
//   Received data: data
//   IO Connection closed
//   Stream finished
// Result в случае ошибки:
//   IO Connection created
//   IO Connection closed
//   Error: Communication error
    }


    public static class SimpleTransaction {
        private static final Random random = new Random();
        private final int id;

        public SimpleTransaction(int id) {
            this.id = id;
            System.out.println("[T: " +  id + "] created");
        }

        public static Mono<SimpleTransaction> beginTransaction() {       // (1)
            return Mono.defer(() ->
                    Mono.just(new SimpleTransaction(random.nextInt(1000))));
        }

        public Flux<String> insertRows(Publisher<String> rows) {   // (2)
            return Flux.from(rows)
                    .delayElements(Duration.ofMillis(100))
                    .flatMap(r -> {
                        if (random.nextInt(10) < 2) {
                            return Mono.error(new RuntimeException("Error: " + r));
                        } else {
                            return Mono.just(r);
                        }
                     });
        }

        public Mono<Void> commit() {                               // (3)
            return Mono.defer(() -> {
                System.out.println("[T: " + id + "{}] commit");
                if (random.nextBoolean()) {
                    return Mono.empty();
                } else {
                    return Mono.error(new RuntimeException("Conflict"));
                }
            });
        }

        public Mono<Void> rollback(Throwable throwable) {                             // (4)
            return Mono.defer(() -> {
                System.out.println("[T: " + id + "] rollback");
                if (random.nextBoolean()) {
                    return Mono.empty();
                } else {
                    return Mono.error(new RuntimeException("Conn error"));
                }
            });
        }

        public Mono<Void> cancel() {                             // (4)
            return Mono.defer(() -> {
                System.out.println("[T: " + id + "{}] rollback");
                if (random.nextBoolean()) {
                    return Mono.empty();
                } else {
                    return Mono.error(new RuntimeException("Cancel"));
                }
            });
        }
    }

    static public void example32_usingWhen() throws InterruptedException {
        Flux.usingWhen(
                SimpleTransaction.beginTransaction(),       // (1)
//                new Function<SimpleTransaction, Publisher<?>>() {
//                    @Override
//                    public Publisher<?> apply(SimpleTransaction transaction) {
//                        return transaction.insertRows(Flux.just("A", "B", "C"));
//                    }
//                },
                transaction -> transaction.insertRows(Flux.just("A", "B", "C")), // (2)
//                new Function<SimpleTransaction, Publisher<?>>() {
//                    @Override
//                    public Publisher<?> apply(SimpleTransaction transaction) {
//                        return transaction.commit();
//                    }
//                },
                SimpleTransaction::commit,                   // (3)
//                new BiFunction<SimpleTransaction, Throwable, Publisher<?>>() {
//                    @Override
//                    public Publisher<?> apply(SimpleTransaction transaction, Throwable throwable) {
//                        return transaction.rollback();
//                    }
//                },
                SimpleTransaction::rollback,                // (4)
                SimpleTransaction::cancel
        )
        .subscribe(
                d -> System.out.println("onNext: " + d),
                e -> System.out.println("onError: " + e.getMessage()),
                () -> System.out.println("onComplete")
        );
        Thread.sleep(3000L);
// Result:
// [T: 78] created
// onNext: A
// [T: 78] rollback
// onError: Async resource cleanup failed after onError
// Result в случае ошибки:
//    [T: 203] created
//    [T: 203{}] rollback
//    onError: Async resource cleanup failed after onError
    }



    private static final Random random = new Random();

    public static Flux<String> recommendedBooks(String userId) {
        return Flux.defer(() -> {                                           // (1)
            if (random.nextInt(10) < 7) {
                return Flux.<String>error(new RuntimeException("Err"))      // (2)
                           .delaySequence(Duration.ofMillis(100));
            } else {
                return Flux.just("Blue Mars", "The Expanse")                // (3)
                           .delayElements(Duration.ofMillis(50));
            }
        }).doOnSubscribe(s -> System.out.println("Request for " + userId)); // (4)
    }

    static public void example33_() throws InterruptedException {
        Flux.just("user-1")                                                 // (1)
            .flatMap(user ->                                                // (2)
                        recommendedBooks(user)                              // (2.1)
                            .retry(3L)                                      // (2.2)
                            .timeout(Duration.ofSeconds(3))                 // (2.3)
                            .onErrorResume(e -> Flux.just("The Martian")))  // (2.4)
            .subscribe(                                                     // (3)
                 b -> System.out.println("onNext: " + b),
                 e -> System.out.println("onError: " + e.getMessage()),
                 () -> System.out.println("onComplete")
             );
        Thread.sleep(2000L);
// Result:
// Request for user-1
// Request for user-1
// Request for user-1
// Request for user-1
// onNext: The Martian
// onComplete
    }


    static public void example34_ConnectableFlux() {
        Flux<Integer> source =
                Flux.range(0, 3)
                    .doOnSubscribe(s ->
                         System.out.println("new subscription for the cold publisher"));
        ConnectableFlux<Integer> conn = source.publish();
        conn.subscribe(e -> System.out.println("[Subscriber 1] onNext: " + e));
        conn.subscribe(e -> System.out.println("[Subscriber 2] onNext: " + e));
        System.out.println("all subscribers are ready, connecting");
        conn.connect();
// Result:
// all subscribers are ready, connecting
// new subscription for the cold publisher
// [Subscriber 1] onNext: 0
// [Subscriber 2] onNext: 0
// [Subscriber 1] onNext: 1
// [Subscriber 2] onNext: 1
// [Subscriber 1] onNext: 2
// [Subscriber 2] onNext: 2
    }

    static public void example34_cache() throws InterruptedException {
        Flux<Integer> source = Flux.range(0, 2)                                   // (1)
                .doOnSubscribe(s ->
                        System.out.println("new subscription for the cold publisher"));
        Flux<Integer> cachedSource = source.cache(Duration.ofSeconds(1));       // (2)
        cachedSource.subscribe(e -> System.out.println("[S 1] onNext: " + e));  // (3)
        cachedSource.subscribe(e -> System.out.println("[S 2] onNext: " + e));  // (4)
        Thread.sleep(3000);                                                     // (5)
        cachedSource.subscribe(e -> System.out.println("[S 3] onNext: " + e));  // (6)
// Result:
// new subscription for the cold publisher
// [S 1] onNext: 0
// [S 1] onNext: 1
// [S 2] onNext: 0
// [S 2] onNext: 1
// new subscription for the cold publisher
// [S 3] onNext: 0
// [S 3] onNext: 1
    }

    static public void example35_share() throws InterruptedException {
        Flux<Integer> source = Flux.range(0, 5)
                .delayElements(Duration.ofMillis(100))
                .doOnSubscribe(s -> System.out.println("new subscription for the cold publisher"));
        Flux<Integer> cachedSource = source.share();
        cachedSource.subscribe(e -> System.out.println("[S 1] onNext: " + e));
        Thread.sleep(400);
        cachedSource.subscribe(e -> System.out.println("[S 2] onNext: " + e));
        Thread.sleep(2000L);
// Result:
// new subscription for the cold publisher
// [S 1] onNext: 0
// [S 1] onNext: 1
// [S 1] onNext: 2
// [S 1] onNext: 3
// [S 2] onNext: 3
// [S 1] onNext: 4
// [S 2] onNext: 4
    }

    static public void example36_elapsed() throws InterruptedException {
        Flux.range(0, 3)
            .delayElements(Duration.ofMillis(100))
            .elapsed()
            .subscribe(e -> System.out.println("Elapsed " + e.getT1() +" ms: " + e.getT2()));
        Thread.sleep(2000L);
// Result:
// Elapsed 115 ms: 0
// Elapsed 111 ms: 1
// Elapsed 110 ms: 2
    }

    static public void example37_transform() {
        Function<Flux<String>, Flux<String>> logUserInfo =          // (1)
                flux -> flux                                        //
                            .index()                                // (1.1)
                            .doOnNext(tp ->                         // (1.2)
                                System.out.println("[" + tp.getT1() + "] User: " + tp.getT2()))
                            .map(Tuple2::getT2);                    // (1.3)
        Flux.range(1000, 3)                                         // (2)
            .map(i -> "user-" + i)                                  //
            .transform(logUserInfo)                                 // (3)
            .subscribe(e -> System.out.println("onNext: " + e));
// Result:
// [0] User: user-1000
// onNext: user-1000
// [1] User: user-1001
// onNext: user-1001
// [2] User: user-1002
// onNext: user-1002
    }

    static public void example38_compose() {
//        Function<Flux<String>, Flux<String>> logUserInfo = (stream) -> { // (1)
//            if (random.nextBoolean()) {
//                return stream
//                        .doOnNext(e -> System.out.println("[path A] User: " + e));
//            } else {
//                return stream
//                        .doOnNext(e -> System.out.println("[path B] User: " + e));
//            }
//        };
//        Flux<String> publisher = Flux.just("1", "2")              // (2)
// ???                                    .compose(logUserInfo);              // (3)
//        publisher.subscribe();                                           // (4)
// Result:
    }

    static public void example39_() {
        // Result:
    }

    static public void example40_() {
        // Result:
    }

    static public void example41_() {
        // Result:
    }

    static public void example42_() {
        // Result:
    }

    static public void example00_() {
        // Result:
    }
}
