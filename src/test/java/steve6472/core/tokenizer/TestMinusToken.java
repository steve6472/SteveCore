package steve6472.core.tokenizer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Created by steve6472
 * Date: 10/17/2024
 * Project: SteveCore <br>
 */
public class TestMinusToken
{
    @Test
    void testParsing()
    {
        final TokenStorage tokenStorage;
        TokenParser<Te> parser;

        tokenStorage = new TokenStorage();
        tokenStorage.addTokens(TestToken.class);

        parser = new TokenParser<>(tokenStorage);

        parser.prefixParslet(TestToken.TEST, new TestParslet());

        parser.tokenize("test");
        List<Te> rets = parser.parseAll();

        assertEquals(1, rets.size());
        assertInstanceOf(Test_.class, rets.getFirst());
    }

    @Test
    void testSingleNumberWithMinusInFront()
    {
        final TokenStorage tokenStorage;
        TokenParser<Te> parser;

        tokenStorage = new TokenStorage();
        tokenStorage.addTokens(TestToken.class);

        parser = new TokenParser<>(tokenStorage);

        parser.prefixParslet(TestToken.SUB, new TestParslet());

        parser.tokenize("- 11");

        List<Tokenizer.SmallToken> tokens = parser.tokenizer.getTokens();
        System.out.println("\n\n");
        System.out.println("Tokenized: " + tokens);
        assertEquals(tokens.getFirst().type(), TestToken.SUB);
        assertEquals(tokens.get(1).type(), MainTokens.NUMBER_INT);
        assertEquals(tokens.get(2).type(), MainTokens.EOF);
    }

    @Test
    void testExpWithParenthesisAndMinus()
    {
        final TokenStorage tokenStorage;
        TokenParser<Te> parser;

        tokenStorage = new TokenStorage();
        tokenStorage.addTokens(TestToken.class);

        parser = new TokenParser<>(tokenStorage);
        parser.tokenize("30 + (5 - -2)");

        List<Tokenizer.SmallToken> tokens = parser.tokenizer.getTokens();
        System.out.println("\n\n");
        System.out.println("Tokenized: " + tokens);

        // -2, 43, 40, -2, 45, -2, 41, -1
        assertEquals(tokens.getFirst().type(), MainTokens.NUMBER_INT);
        assertEquals(tokens.get(1).type(), TestToken.ADD);
        assertEquals(tokens.get(2).type(), TestToken.LEFT);
        assertEquals(tokens.get(3).type(), MainTokens.NUMBER_INT);
        assertEquals(tokens.get(4).type(), TestToken.SUB);
        assertEquals(tokens.get(5).type(), MainTokens.NUMBER_INT);
        assertEquals(tokens.get(6).type(), TestToken.RIGHT);
        assertEquals(tokens.get(7).type(), MainTokens.EOF);
    }

    @Test
    void testMerge()
    {
        final TokenStorage tokenStorage;
        TokenParser<Te> parser;

        tokenStorage = new TokenStorage();
        tokenStorage.addTokens(TestToken.class);

        parser = new TokenParser<>(tokenStorage);
        parser.tokenize("5 == 7");

        List<Tokenizer.SmallToken> tokens = parser.tokenizer.getTokens();
        System.out.println("\n\n");
        System.out.println("Tokenized: " + tokens);

        assertEquals(tokens.getFirst().type(), MainTokens.NUMBER_INT);
        assertEquals(tokens.get(1).type(), TestToken.EQUALS);
        assertEquals(tokens.get(2).type(), MainTokens.NUMBER_INT);
        assertEquals(tokens.get(3).type(), MainTokens.EOF);
    }

    private static class TestParslet implements PrefixParselet<Te>
    {
        @Override
        public Te parse(Tokenizer tokenizer, TokenParser<Te> parser)
        {
            return new Test_();
        }
    }

    private interface Te {}
    private static class Test_ implements Te {}

    private enum TestToken implements Token
    {
        TEST("test"),

        SUB("-"),
        ADD("+"),
        LEFT("("),
        RIGHT(")"),
        EQUALS("=="),
        ASSIGN("="),

        ;

        private final String symbol;

        TestToken(String symbol)
        {
            this.symbol = symbol;
        }

        @Override
        public String getSymbol()
        {
            return symbol;
        }
    }
}
