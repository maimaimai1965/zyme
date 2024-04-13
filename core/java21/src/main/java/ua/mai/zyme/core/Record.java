package ua.mai.zyme.core;

public class Record {

    record Student(String name, int age) {}

    static public void example01_BasicRecordPattern() {
        Object obj = new Student("Petrenko", 21);

        if (obj instanceof Student(String name, int age)) {
            System.out.println(name + " - age " + age);
        }
        if (obj instanceof Student(String fName, int fAge)) {  // Названия полей не совпадают в instanceof и в record Student.
            System.out.println(fName + " - age " + fAge);
        }
        if (obj instanceof Student r) {
            System.out.println(r.name + " - age " + r.age);
        }
// Result:
// Petrenko - age 21
// Petrenko - age 21
// Petrenko - age 21
    }


    record Tutor(String name) {}
    record Course(Tutor tutor, Student student) {}

    static public void example02_NestedRecordPattern() {
        Tutor mathTutor = new Tutor("Mr. Smith");
        Student bob = new Student("Bob", 10);
        Course mathCourse = new Course(mathTutor, bob);

        if (mathCourse instanceof Course(Tutor(String tutorName), Student student)) {
            System.out.println("The tutor for this course is: " + tutorName);
        }
// Result:
// The tutor for this course is: Mr. Smith
    }


    record Box<T>(T value) {};

    static public void example03_TypeInference() {
        Box<Student> studentBox = new Box<>(new Student("Charlie", 19));

        if (studentBox instanceof Box(Student(String name, var age))) {
            System.out.println("Student Name: " + name + ", Age: " + age);
        }
        if (studentBox instanceof Box(Student r)) {
            System.out.println("Student Name: " + r.name + ", Age: " + r.age);
        }
// Result:
// Student Name: Charlie, Age: 19
// Student Name: Charlie, Age: 19
    }

    static public void example04_TypeInference() {
        Box<Student> studentBox = new Box<>(new Student("Charlie", 19));
        printBox(studentBox);

        Box<Tutor> tutorBox = new Box<>(new Tutor("Mr. Smith"));
        printBox(tutorBox);

        Box<String> stringBox = new Box<>("Completed");
        printBox(stringBox);
// Result:
// Box for Student - Name: Charlie, Age: 19
// Box for Tutor - Name: Mr. Smith
// Box for String - Completed
    }
    static private <T> void printBox(Box<T> box) {
        switch (box) {
            case Box(Student r) -> System.out.println("Box for Student - Name: " + r.name + ", Age: " + r.age);
            case Box(Tutor r) -> System.out.println("Box for Tutor - Name: " + r.name());
            default -> System.out.println("Box for " + box.value.getClass().getSimpleName() + " - " + box.value.toString());
        }
    }

// Preview Java 21
//    record Address(String city, String country) {};
//    record StudentAddresses(Address primaryAddress, Address secondaryAddress) {};
//
//    static public void example05_UsingUnderscore() {
//        StudentAddresses addresses = new StudentAddresses(new Address("Kharkiv", "Ukrain"), new Address("Kiev", "Ukrain"));
//
//        if (addresses instanceof StudentAddresses(Address(var city, _), _)) {
//            System.out.println("Primary address city is: " + city);
//        }
//// Result:
//    }

}
