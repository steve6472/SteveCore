package steve6472.core.tokenizer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by steve6472
 * Date: 10/17/2024
 * Project: SteveCore <br>
 */
public class FullTest
{
    @org.junit.jupiter.api.Test
    void fullTest()
    {
        final TokenStorage tokenStorage;
        TokenParser<Te> parser;

        tokenStorage = new TokenStorage();
        tokenStorage.addTokens(TestToken.class);

        parser = new TokenParser<>(tokenStorage);

        parser.prefixParslet(TestToken.COLLISION, new TestParslet());

        parser.tokenize("test");
        List<Te> rets = parser.parseAll();

        assertEquals(1, rets.size());
        assertInstanceOf(Test.class, rets.getFirst());
    }

    private static class TestParslet implements PrefixParselet<Te>
    {
        @Override
        public Te parse(Tokenizer tokenizer, TokenParser<Te> parser)
        {
            return new Test();
        }
    }

    private interface Te {}
    private static class Test implements Te {}

    private enum TestToken implements Token
    {
        COLLISION("test")

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
