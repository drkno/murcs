package sws.project.magic;

import com.sun.istack.internal.NotNull;

/**
 *
 */
public class FooBar {
    @Editable(SimpleStringPaneGenerator.class)
    private String foo;

    @Editable(SimpleBooleanPaneGenerator.class)
    private boolean james;

    //@Editable(SimpleStringPaneGenerator.class)
    private String bar;

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public String getBar() {
        return bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }

    public boolean getJames() {
        return james;
    }

    public void setJames(boolean james) {
        this.james = james;
    }
}
