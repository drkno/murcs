package sws.murcs.unit.search;

import org.junit.Assert;
import org.junit.Test;
import sws.murcs.search.tokens.Token;

public class ScannerTest {
    @Test
    public void blankStringTest() {
        Token rootToken = Token.parse("");
        Assert.assertTrue("There is no spoon.", rootToken.matches("lorem ipsem"));
    }

    @Test
    public void textOnlyTest() {
        Token rootToken = Token.parse("This is a test string that could be searched for.");
        Assert.assertTrue("There really is no spoon.", rootToken.matches("This is a test string that could be searched for. some data"));
        Assert.assertFalse("The spoon is real!", rootToken.matches("no spoon"));
    }

    @Test
    public void andTokenTest() {
        Token rootToken = Token.parse("foo && bar");
        Assert.assertTrue("I've dropped the spoon.", rootToken.matches("This is a magical string that contains both foo as well as bar somewhere within it."));
        Assert.assertFalse("Who stole my spoon?", rootToken.matches("Where is my foo????"));
        Assert.assertFalse("Jay, did you break my spoon?", rootToken.matches("Where is my bar????"));
        Assert.assertFalse("Maybe Dion did work with my spoon?", rootToken.matches("I'm hungry. Where's my spoon?"));
    }

    @Test
    public void orTokenTest() {
        Token rootToken = Token.parse("knife || fork");
        Assert.assertTrue("Where is my fork?", rootToken.matches("knife"));
        Assert.assertTrue("Where is my knife?", rootToken.matches("fork"));
        Assert.assertFalse("I can't eat without cutlery?", rootToken.matches("spoon"));
        Assert.assertTrue("Spooner or later this will pass.", rootToken.matches("It would be knifed to know if I forked up."));
    }

    @Test
    public void multipleTokenTest() {
        Token rootToken = Token.parse("spoons && some forks || maybe a knife?");
        Assert.assertTrue("Really? That's nice.", rootToken.matches("I have spoons and some forks on the table."));
        Assert.assertTrue("C# is soo much better right?", rootToken.matches("What would I use to dissect this project? maybe a knife?"));
        Assert.assertTrue("What is life without spoons?", rootToken.matches("spoons, some forks and maybe a knife?"));
        Assert.assertFalse("What? No spoons?", rootToken.matches("forks"));
        Assert.assertFalse("I can still bend the spoon.", rootToken.matches("some forks"));
        Assert.assertFalse("How am I supposed to eat now?", rootToken.matches("knife"));
        Assert.assertFalse("The cake is a lie.", rootToken.matches("I am a cake."));
    }
}
