package steve6472.core.tokenizer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by steve6472
 * Date: 10/17/2024
 * Project: SteveCore <br>
 */
public class TestMinusToken
{
    @Test
    void testSingleNumber()
    {
        final TokenStorage tokenStorage;
        TokenParser<Te> parser;

        tokenStorage = new TokenStorage();
        tokenStorage.addTokens(TestToken.class);

        parser = new TokenParser<>(tokenStorage);

        parser.prefixParslet(TestToken.SUB, new TestParslet());

        parser.tokenize("- 11");

        List<Tokenizer.SmallToken> tokens = parser.tokenizer.getTokens();
        System.out.println(tokens);
        assertEquals(tokens.getFirst().type(), TestToken.SUB);
        assertEquals(tokens.get(1).type(), MainTokens.NUMBER_INT);
        assertEquals(tokens.get(2).type(), MainTokens.EOF);
    }

    @Test
    void testExp()
    {
        final TokenStorage tokenStorage;
        TokenParser<Te> parser;

        tokenStorage = new TokenStorage();
        tokenStorage.addTokens(TestToken.class);

        parser = new TokenParser<>(tokenStorage);
        parser.tokenize("30 + (5 - -2)");

        List<Tokenizer.SmallToken> tokens = parser.tokenizer.getTokens();

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
        SUB("-"),
        ADD("+"),
        LEFT("("),
        RIGHT(")"),

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

        @Override
        public boolean isMerge()
        {
            return false;
        }
    }
}
