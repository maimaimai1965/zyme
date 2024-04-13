package ua.mai.zyme.core;

public class Switch {

    enum Coin {
        HEADS, TAILS
    }

    static public void example01() {
        Coin coin = Coin.HEADS;

        switch (coin) {
            case HEADS -> System.out.println("Heads");  // происходит выход из switch
            case TAILS -> System.out.println("Tails");
        }
// Result:
// Heads
    }

    static public void example02() {
        Coin coin = Coin.HEADS;

        switch (coin) {
            case Coin.HEADS : System.out.println("Heads"); // продолжается обработка switch
            case Coin.TAILS : System.out.println("Tails");
        }
// Result:
// Heads
// Tails
    }

    static public void example03_Types() {
        Object obj = Double.valueOf(2.33);
        String formatted = switch (obj) {
            case Integer i -> String.format("int %d", i);
            case Long l -> String.format("long %d", l);
            case Double d -> String.format("double %f", d);
            case String s -> String.format("String %s", s);
            default -> obj.toString();
        };
        System.out.println(formatted);
// Result:
// double 2,330000
    }

    static public void example04_Types_and_when() {
        Object obj = "2345";
        String formatted = switch (obj) {
            case String s when s.startsWith("1") && s.length()<10 -> String.format("String start from \"1\": %s", s);
            case String s when s.startsWith("2") && s.length()<10 -> String.format("String start from \"2\": %s", s);
            case String s -> String.format("String %s", s);
            case null, default -> obj.toString();
        };
        System.out.println(formatted);
// Result:
// String start from "2": 2345
    }

    static public void example05_null() {
        Object obj = null;
        String formatted = switch (obj) {
            case null -> "null";
            case String s -> String.format("String %s", s);
            default -> obj.toString();
        };
        System.out.println(formatted);
// Result:
// null
    }


    sealed interface SchoolTeam permits House, ChessClub {}
    public enum House implements SchoolTeam { GRYFFINDOR, HUFFLEPUFF, RAVENCLAW, SLYTHERIN }
    final class ChessClub implements SchoolTeam {}

    static public void example06_sealed_interface() {
        SchoolTeam team = House.SLYTHERIN;
        switch (team) {
            case House.GRYFFINDOR -> System.out.println("Hooray GRYFFINDOR!");
            case House.HUFFLEPUFF -> System.out.println("Hooray HUFFLEPUFF!");
            case House.RAVENCLAW  -> System.out.println("Hooray RAVENCLAW!");
            case House.SLYTHERIN  -> System.out.println("Hooray SLYTHERIN!");
            case ChessClub club   -> System.out.println("Make the right move!");
        }
// Result:
// Hooray SLYTHERIN!
    }


    record Student(String name, int age) {}

    static public void example07_record() {
        Object obj = new Record.Student("Petrenko", 21);

        switch (obj) {
            case Record.Student(String name, int age) when age >= 18 -> System.out.println("adult: " + name + " - age " + age);
            case Student st                                          -> System.out.println("no adult: " + st.name + " - age " + st.age);
            default                                                  -> System.out.println("Not Student");
        }
// Result:
// adult: Petrenko - age 21
    }

//    static private void example03_() {
//// Result:
////
//    }

}
