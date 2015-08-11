package sws.murcs.unit.search;

import org.junit.Assert;
import org.junit.Test;
import sws.murcs.search.tokens.Token;

public class TokenTest {
    @Test
    public void blankStringTest() {
        Token rootToken = Token.parse("");
        Assert.assertNull("There is no spoon.", rootToken.matches("lorem ipsem"));
    }

    @Test
    public void textOnlyTest() {
        Token rootToken = Token.parse("This is a test string that could be searched for.");
        Assert.assertNotNull("There really is no spoon.", rootToken.matches("This is a test string that could be searched for. some data"));
        Assert.assertNull("The spoon is real!", rootToken.matches("no spoon"));
    }

    @Test
    public void andTokenTest() {
        Token rootToken = Token.parse("foo && bar");
        Assert.assertNotNull("I've dropped the spoon.", rootToken.matches("This is a magical string that contains both foo as well as bar somewhere within it."));
        Assert.assertNull("Who stole my spoon?", rootToken.matches("Where is my foo????"));
        Assert.assertNull("Jay, did you break my spoon?", rootToken.matches("Where is my bar????"));
        Assert.assertNull("Maybe Dion did work with my spoon?", rootToken.matches("I'm hungry. Where's my spoon?"));
    }

    @Test
    public void orTokenTest() {
        Token rootToken = Token.parse("knife || fork");
        Assert.assertNotNull("Where is my fork?", rootToken.matches("knife"));
        Assert.assertNotNull("Where is my knife?", rootToken.matches("fork"));
        Assert.assertNull("I can't eat without cutlery?", rootToken.matches("spoon"));
        Assert.assertNotNull("Spooner or later this will pass.", rootToken.matches("It would be knifed to know if I forked up."));
    }

    @Test
    public void multipleTokenTest() {
        Token rootToken = Token.parse("spoons && some forks || maybe a knife?");
        Assert.assertNotNull("Really? That's nice.", rootToken.matches("I have spoons and some forks on the table."));
        Assert.assertNotNull("C# is soo much better right?", rootToken.matches("What would I use to dissect this project? maybe a knife?"));
        Assert.assertNotNull("What is life without spoons?", rootToken.matches("spoons, some forks and maybe a knife?"));
        Assert.assertNull("What? No spoons?", rootToken.matches("forks"));
        Assert.assertNull("I can still bend the spoon.", rootToken.matches("some forks"));
        Assert.assertNull("How am I supposed to eat now?", rootToken.matches("knife"));
        Assert.assertNull("The cake is a lie.", rootToken.matches("I am a cake."));
    }

    @Test
    public void initialCaseDirectiveTest() {
        Token rootToken = Token.parse("!case spoon && some forks || maybe a knife?");
        Assert.assertNotNull("Where is my knife?", rootToken.matches("maybe a knife?"));
        Assert.assertNull("I only have a knife not a Knife!", rootToken.matches("maybe a Knife?"));
    }

    @Test
    public void middleCaseDirectiveTest() {
        Token rootToken = Token.parse("spoon && some forks !case || maybe a knife?");
        Assert.assertNotNull("Where is my knife?", rootToken.matches("maybe a knife?"));
        Assert.assertNull("I only have a knife not a Knife!", rootToken.matches("maybe a Knife?"));
    }

    @Test
    public void endCaseDirectiveTest() {
        Token rootToken = Token.parse("spoon && some forks || maybe a knife? !case");
        Assert.assertNotNull("Where is my knife?", rootToken.matches("maybe a knife?"));
        Assert.assertNull("I only have a knife not a Knife!", rootToken.matches("maybe a Knife?"));
    }

    @Test
    public void wildcardEscapeTest() {
        Token rootToken = Token.parse("the \\*quick\\* brown fox jumped over the lazy dog\\?");
        Assert.assertNotNull("Wildcards were not escaped in wildcard mode.",
                rootToken.matches("the *quick* brown fox jumped over the lazy dog?"));
    }

    @Test
    public void wildcardStarTest() {
        Token rootToken = Token.parse("the quick*");
        Assert.assertNotNull("Wildcards failed to match the string.", rootToken.matches("the quick brown fox jumps over the lazy dog"));
        rootToken = Token.parse("the quick * fox jumps over the lazy dog");
        Assert.assertNotNull("Wildcards failed to match the string.", rootToken.matches("the quick brown fox jumps over the lazy dog"));
        rootToken = Token.parse("*fox jumps over the lazy dog");
        Assert.assertNotNull("Wildcards failed to match the string.", rootToken.matches("the quick brown fox jumps over the lazy dog"));
    }

    @Test
    public void wildcardAnyTest() {
        Token rootToken = Token.parse("the quick brow?");
        Assert.assertNotNull("Wildcards failed to match the string.", rootToken.matches("the quick brown fox jumps over the lazy dog"));
        rootToken = Token.parse("the quick brown fox jum?s");
        Assert.assertNotNull("Wildcards failed to match the string.", rootToken.matches("the quick brown fox jumps over the lazy dog"));
        rootToken = Token.parse("?ox jumps over the lazy dog");
        Assert.assertNotNull("Wildcards failed to match the string.", rootToken.matches("the quick brown fox jumps over the lazy dog"));
    }

    @Test
    public void regexTest() {
        Token rootToken = Token.parse("!regex .*");
        Assert.assertNotNull("Using regex failed.", rootToken.matches("this is a test string"));
        Assert.assertNotNull("Using regex failed.", rootToken.matches("blah blah blah (I found the spoon!)"));
    }

    @Test
    public void partialRegexTest() {
        Token rootToken = Token.parse("!regex [0-");
        Assert.assertNull("Broken regex was searched.", rootToken.matches("I found something"));
        Assert.assertNull("Broken regex was searched.", rootToken.matches("Blah"));
    }
}
