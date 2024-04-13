package ua.mai.zyme.reactor;


public class Greeting {

    private String text;

    public Greeting() {
    }

    public Greeting(String message) {
        this.text = message;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Greeting {" + "text='" + text + '\'' + '}';
    }
}
