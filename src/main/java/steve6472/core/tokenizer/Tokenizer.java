package steve6472.core.tokenizer;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Pattern;

/**********************
 * Created by steve6472
 * Date: 9/3/2022
 * Project: ScriptIt
 *
 ***********************/
public final class Tokenizer
{
	private final List<SmallToken> tokens;
	private final Set<Integer> newLines;
	private int currentToken = -1;

	public int maxMergeDistance = 3;
	private final List<SmallToken> mergeStack;

	private static final Pattern IS_DECIMAL = Pattern.compile("([+-]?\\d*(\\.\\d+)?)+");
	private static final Pattern IS_INTEGER = Pattern.compile("([+-]?\\d)+");

	public static boolean isInteger(String text)
	{
		return IS_INTEGER.matcher(text).matches();
	}

	public static boolean isDecimal(String text)
	{
		return IS_DECIMAL.matcher(text).matches();
	}

	public Tokenizer(TokenStorage tokenStorage, String string)
	{
		tokens = new ArrayList<>();
		newLines = new HashSet<>();
		mergeStack = new ArrayList<>();

		MyStreamTokenizer tokenizer = createTokenizer(string);
		tokenize(tokenizer, tokenStorage);
	}

	private MyStreamTokenizer createTokenizer(String string)
	{
		StringReader r = new StringReader(string);
		MyStreamTokenizer tokenizer = new MyStreamTokenizer(r);
		tokenizer.ordinaryChar('.');
		tokenizer.wordChars('_', '_');
		tokenizer.eolIsSignificant(true);

		return tokenizer;
	}

	private void tokenize(MyStreamTokenizer tokenizer, TokenStorage tokenStorage)
	{
		int lastLine = 1;
		int lastColumn = 1;

		w: while (true)
		{
			int token;
			String sval;

			int line = tokenizer.lineno();
			int column;

			if (!mergeStack.isEmpty())
			{
				SmallToken remove = mergeStack.removeFirst();
				token = remove._token();
				sval = remove.sval();
				line = remove.line;
				lastColumn = remove.column;
			}
			else
			{
				try
				{
					token = tokenizer.nextToken();
					sval = tokenizer.sval;
				} catch (IOException e)
				{
					throw new RuntimeException(e);
				}
			}

			if (lastLine != line)
			{
				lastLine = line;
				lastColumn = 1;
				column = 1;
			} else
			{
				lastColumn++;
				column = lastColumn;
			}

			// Because the tokenizer is silly, I spent 50 minutes on this fucking condition
			if (token == '-')
				sval = "-";

			switch (token)
			{
				case StreamTokenizer.TT_EOF ->
				{
					break w;
				}
				case StreamTokenizer.TT_NUMBER -> tokens.add(new SmallToken(isInteger(sval) ? MainTokens.NUMBER_INT : MainTokens.NUMBER_DOUBLE, sval, line, column));
				case StreamTokenizer.TT_WORD -> tokens.add(new SmallToken(Objects.requireNonNullElse(tokenStorage.fromSymbol(sval), MainTokens.NAME), sval, line, column));
				case '"' -> tokens.add(new SmallToken(MainTokens.STRING, sval, line, column)); // String
				case '\'' -> tokens.add(new SmallToken(MainTokens.CHAR, sval, line, column)); // Char
				case '\n' -> newLines.add(tokens.size()); // New line
				default ->
				{
					if (sval == null)
					{
						Token merged = mergeTokens(tokenizer, tokenStorage);

						if (merged == null)
							merged = tokenStorage.fromSymbol("" + (char) token);
						else
							mergeStack.clear();

						if (merged == null)
							throw new RuntimeException("Unknown symbol " + token + " '" + (char) token + ", line: " + line + ", column: " + column);
						else
							tokens.add(new SmallToken(merged, merged.getSymbol(), line, column));
					} else
					{
						Token type = tokenStorage.fromSymbol(sval);

						if (type == null)
							throw new RuntimeException("Unknown symbol " + token + " '" + (char) token + "', sval: " + sval + ", line: " + line + ", column: " + column);
						else
							tokens.add(new SmallToken(type, sval, line, column));
					}
				}
			}
		}

		tokens.add(new SmallToken(MainTokens.EOF, "", tokenizer.lineno(), 0));
	}

	private Token mergeTokens(MyStreamTokenizer tokenizer, TokenStorage tokenStorage)
	{
		if (maxMergeDistance <= 1)
			return null;

		StringBuilder symbol = new StringBuilder("" + (char) tokenizer.ttype);

		for (int i = 0; i < maxMergeDistance - 1; i++)
		{
			try
			{
				symbol.append((char) tokenizer.nextToken());
				mergeStack.add(new SmallToken(null, tokenizer.sval, tokenizer.lineno(), -1, tokenizer.ttype));
			} catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}

		for (int i = 0; i < maxMergeDistance - 1; i++)
		{
			Token iToken = tokenStorage.fromSymbol(symbol.toString());
			if (iToken != null)
				return iToken;
			else
				symbol.setLength(symbol.length() - 1);
		}

		return null;
	}

	public boolean isNextTokenNewLine()
	{
		return newLines.contains(currentToken + 1);
	}

	public SmallToken nextToken()
	{
		currentToken++;
		return getCurrentToken();
	}

	public boolean hasMoreTokens()
	{
		return currentToken < tokens.size() - 1;
	}

	public SmallToken getCurrentToken()
	{
		return tokens.get(currentToken);
	}

	public int getCurrentTokenIndex()
	{
		return currentToken;
	}

	public List<SmallToken> getTokens()
	{
		return tokens;
	}

	public SmallToken peekToken(int peek)
	{
		if (currentToken + peek >= tokens.size())
			return null;
		return tokens.get(currentToken + peek);
	}

	public SmallToken peekToken()
	{
		return peekToken(1);
	}

	public boolean matchToken(Token expectedtype, boolean consume)
	{
		SmallToken token = peekToken();
		if (token == null || token.type != expectedtype)
		{
			return false;
		} else
		{
			if (consume)
				consumeToken(expectedtype);
			return true;
		}
	}

	public SmallToken consumeToken(Token expectedType)
	{
		nextToken();
		SmallToken token = getCurrentToken();
		if (expectedType == null)
			return token;
		if (expectedType != token.type)
		{
			StringBuilder sb = new StringBuilder();
			for (int i = Math.max(0, currentToken - 10); i < Math.min(currentToken + 10, tokens.size() - 1); i++)
			{
				sb.append(tokens.get(i).sval());
			}

			throw new RuntimeException("Expected token type '" + expectedType + "' got '" + token.type + "' around ... " + sb + " ...");
		}
		return token;
	}

	public void skip(int skip)
	{
		currentToken += skip;
	}

	public record SmallToken(Token type, String sval, int line, int column, int _token)
	{
		public SmallToken(Token type, String sval, int line, int column)
		{
			this(type, sval, line, column, 0);
		}

		@Override
		public String toString()
		{
			return "Token{" + "type=" + type + ", sval='" + sval + '\'' + '}';
		}

		public String string()
		{
			return sval == null ? "" + (char) _token : sval;
		}
	}
}
