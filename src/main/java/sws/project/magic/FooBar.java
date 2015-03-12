package sws.project.magic;

import com.sun.istack.internal.NotNull;

/**
 *
 */
public class FooBar {
    @Editable(SimpleStringPaneGenerator.class)
    @NotNull private String foo;

    @Editable(SimpleStringPaneGenerator.class)
    private String bar;

    private String james;

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

    public String getJames() {
        return james;
    }

    public void setJames(String james) {
        this.james = james;
    }
}
