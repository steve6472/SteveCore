package steve6472.core.tokenizer;

import java.util.*;
import java.util.function.Supplier;

/**********************
 * Created by steve6472
 * Date: 9/3/2022
 * Project: ScriptIt
 * <p>
 * <a href="https://en.cppreference.com/w/cpp/language/operator_precedence">C++ operator precedence</a>
 *
 ***********************/
public class TokenParser<R>
{
	private static final Precedence MIN_PRECEDENCE = () -> 0;

	public final Map<Token, PrefixParselet<R>> prefixParslets = new HashMap<>();
	public final Map<Token, InfixParslet<R>> infixParslets = new HashMap<>();

	public final Set<Class<?>> ignoredExpressions = new HashSet<>();

	public Supplier<Boolean> iteratingCondition;
	public TokenStorage tokenStorage;
	public Tokenizer tokenizer;

	public TokenParser(TokenStorage tokenStorage)
	{
		this.tokenStorage = tokenStorage;
		this.iteratingCondition = () -> true;
	}

	public void prefixParslet(Token token, PrefixParselet<R> parselet)
	{
		prefixParslets.put(token, parselet);
	}

	public void infixParslet(Token token, InfixParslet<R> parselet)
	{
		infixParslets.put(token, parselet);
	}

	public TokenParser<R> tokenize(String expression)
	{
		tokenizer = new Tokenizer(tokenStorage, expression);

		return this;
	}

	public void setIteratingCondition(Supplier<Boolean> iteratingCondition)
	{
		this.iteratingCondition = iteratingCondition;
	}

	public R parse(Precedence precedence)
	{
		Tokenizer.SmallToken token = tokenizer.consumeToken(null);

		if (token.type() == MainTokens.EOF)
			return null;

		PrefixParselet<R> parslet = prefixParslets.get(token.type());
		if (parslet == null)
		{
			StringBuilder sb = new StringBuilder();
			for (int i = Math.max(0, tokenizer.getCurrentTokenIndex() - 10); i < Math.min(tokenizer.getCurrentTokenIndex() + 10, tokenizer.getTokens().size() - 1); i++)
			{
				sb.append(tokenizer.getTokens().get(i).string());
			}

			throw new RuntimeException("Parslet not found for '" + token.string() + "' around ... " + sb + " ... (line: " + token.line() + ", column: " + token.column() + ")");
		}

		return parse(precedence, parslet);
	}

	public R parse(Precedence precedence, PrefixParselet<R> parslet)
	{
		R result = parslet.parse(tokenizer, this);

		if (ignoredExpressions.contains(result.getClass()))
		{
			return parse(precedence);
		}

        return parseInfixExpression(result, precedence);
	}

	public R parseInfixExpression(R left, Precedence precedence)
	{
		Tokenizer.SmallToken token;

		while (precedence.ordinal() < getPrecedence().ordinal())
		{
			token = tokenizer.consumeToken(null);
			InfixParslet<R> infixParselet = infixParslets.get(token.type());
			left = infixParselet.parse(tokenizer, left);
		}
		return left;
	}

	private Precedence getPrecedence()
	{
		Tokenizer.SmallToken token = tokenizer.peekToken();

		if (token != null)
		{
			InfixParslet<R> parselet = infixParslets.get(token.type());

			if (parselet != null)
			{
				return parselet.getPrecedence();
			}
		}

		return MIN_PRECEDENCE;
	}

	public List<R> parseAll()
	{
		List<R> expressions = new ArrayList<>();
		boolean lastIgnored = false;
		do
		{
			R next = parse(MIN_PRECEDENCE);

			if (next != null)
			{
				if (!ignoredExpressions.contains(next.getClass()))
				{
					addExpression(next, expressions);
				} else {
					lastIgnored = true;
				}
			} else
			{
				break;
			}
		} while (iteratingCondition.get() || lastIgnored);
		return expressions;
	}

	protected void addExpression(R next, List<R> expressions)
	{
		expressions.add(next);
	}

	/*
	 * Util
	 */

	public <T extends R> T parse(Class<T> expected)
	{
		R parse = parse(MIN_PRECEDENCE);
		if (parse.getClass().isAssignableFrom(expected))
			return expected.cast(parse);
		else
			throw new RuntimeException("Parsing expected " + expected.getSimpleName() + ", got " + parse.getClass().getSimpleName());
	}
}
