package ua.mai.zyme.core;

import java.util.*;

public class Collections {

    /*
      interface SequencedCollection<E> extends Collections<E> {
          // You can view the list in reverse.
          Collections<E> reversed();
          // You can add or remove students at the beginning or end.
          void addFirst(E);
          void addLast(E);
          E getFirst();
          E getLast();
          E removeFirst();
          E removeLast();
      }
    */
    static public void example01_SequencedCollection() {
        List<String> classroom = new ArrayList<>();

        // Adding students in the order they joined
        classroom.addLast("Alice");
        classroom.addLast("Bob");
        classroom.addLast("Charlie");
        System.out.println("Classroom: " + classroom);

        // The first student who joined
        System.out.println("First student: " + classroom.getFirst());
        // Adding a new student at the beginning
        classroom.addFirst("Zara");
        System.out.println("After adding Zara at the start: " + classroom);
        // Viewing the register in reverse
        System.out.println("Reversed register: " + classroom.reversed());
        // Removing the last student
        classroom.removeLast();
        System.out.println("After removing the last student: " + classroom);
// Result:
// Classroom: [Alice, Bob, Charlie]
// First student: Alice
// After adding Zara at the start: [Zara, Alice, Bob, Charlie]
// Reversed register: [Charlie, Bob, Alice, Zara]
// After removing the last student: [Zara, Alice, Bob]
    }

    /*
      interface SequencedMap<K,V> {
          SequencedMap<K,V> reversed();
          V putFirst(K key, V value);
          V putLast(K key, V value);
          Entry<K, V> firstEntry();
          Entry<K, V> lastEntry();
          Entry<K, V> pollFirstEntry();
          Entry<K, V> pollLastEntry();
      }
     */
    static public void example02_SequencedMap() {
        LinkedHashMap<String, Integer> reportCard = new LinkedHashMap();

        // Let's add scores in the sequence exams were taken
        reportCard.putLast("English", 85);
        reportCard.putLast("Math", 90);
        reportCard.putLast("History", 80);
        System.out.println("Report Card: " + reportCard);
        // The first subject student took the exam in
        System.out.println("First exam subject and score: " + reportCard.firstEntry());
        // Inserting a score for Geography which was the first exam taken
        reportCard.putFirst("Geography", 88);
        System.out.println("After adding Geography score at the start: " +  reportCard);
        // Viewing the report card in reverse (can be useful for checking the most recent exams first)
        System.out.println("Reversed report card: " + reportCard.reversed());
// Result:
// Report Card: {English=85, Math=90, History=80}
// First exam subject and score: English=85
// After adding Geography score at the start: {Geography=88, English=85, Math=90, History=80}
// Reversed report card: {History=80, Math=90, English=85, Geography=88}
    }

    /*
       interface SequencedSet<E> extends Set<E>, SequencedCollection<E> {
            SequencedSet<E> reversed();
       }
     */
    static public void example03_SequencedSet() {
        LinkedHashSet attendanceToday = new LinkedHashSet();

        // Students entering the classroom and the teacher marking them as present
        attendanceToday.add("James");
        attendanceToday.add("Emily");
        attendanceToday.add("Lucas");
        attendanceToday.add("Sophia");
        // Let's try adding James again, should not be allowed
        attendanceToday.add("James");
        System.out.println("Attendance for today: " + attendanceToday);
        // The sequence in which students entered the classroom in reverse
        System.out.println("Reversed attendance: " + attendanceToday.reversed());
// Result:
// Attendance for today: [James, Emily, Lucas, Sophia]
// Reversed attendance: [Sophia, Lucas, Emily, James]
    }

}
