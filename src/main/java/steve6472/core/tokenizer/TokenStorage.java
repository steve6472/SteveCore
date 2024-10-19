package steve6472.core.tokenizer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by steve6472
 * Date: 9/3/2022
 * Project: ScriptIt
 */
public class TokenStorage
{
	private final Set<Token> tokenList;

	public TokenStorage()
	{
		tokenList = new HashSet<>();
	}

	public void addTokens(Token[] operators)
	{
		Collections.addAll(tokenList, operators);
	}

	public void addTokens(Class<? extends Token> tokens)
	{
		if (!tokens.isEnum())
		{
			throw new RuntimeException(tokens.getSimpleName() + " is not an enum! Use addTokens(Token[]) instead!");
		}

		Collections.addAll(tokenList, tokens.getEnumConstants());
	}

	public Token fromSymbol(String symbol)
	{
		return tokenList
			.stream()
			.filter(s -> s.getSymbol().equals(symbol))
			.findFirst()
			.orElse(null);
	}

	public Set<Token> getTokenList()
	{
		return tokenList;
	}
}
