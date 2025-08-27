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
	private static final boolean DEBUG_TOKENIZER = false;
	private static final int MAX_SYMBOL_MERGE = 4;
	private final List<SmallToken> tokens;
	private final Set<Integer> newLines;
	private int currentToken = -1;

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

		List<SmallToken> readBacklog = new ArrayList<>(MAX_SYMBOL_MERGE);

		w: while (true)
		{
			int token;
			String sval;

			int line = tokenizer.lineno();
			int column;

			if (!readBacklog.isEmpty())
			{
				SmallToken remove = readBacklog.removeFirst();
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
					// Find the max biggest possible symbol
					// If none match, throw exception for first symbol
					if (sval == null || sval.isEmpty())
					{
						readBacklog.add(new SmallToken(null, null, line, column, token));
						for (int i = 0; i < MAX_SYMBOL_MERGE - 1 - readBacklog.size(); i++)
						{
							int nextToken;
                            try
                            {
	                            nextToken = tokenizer.nextToken();
                            } catch (IOException e)
                            {
                                throw new RuntimeException(e);
                            }
							readBacklog.add(new SmallToken(null, tokenizer.sval, tokenizer.lineno(), column, nextToken));
							if (nextToken == StreamTokenizer.TT_EOF)
								break;
						}

						if (DEBUG_TOKENIZER)
							System.out.println("Backlog: " + readBacklog);

						boolean hasFoundToken = false;
						// iterate token-count times
                        for (int i = 0; i < readBacklog.size(); i++)
                        {
							// From count > 0 create svalues (meaning from largest to smallest token)
	                        StringBuilder svalue = new StringBuilder();
							int mergeCount = readBacklog.size() - i - 1;
	                        for (int j = mergeCount; j >= 0; j--)
	                        {
		                        SmallToken smallToken = readBacklog.get(j);
		                        svalue.append(smallToken.string());
	                        }
							// See if token exists
	                        Token foundToken = tokenStorage.fromSymbol(svalue.toString());

							// If token exists, add it to tokens
							if (foundToken != null)
							{
								if (DEBUG_TOKENIZER)
									System.out.println("Found token: " + foundToken + ", removing " + (mergeCount + 1) + " entries");
								tokens.add(new SmallToken(foundToken, foundToken.getSymbol(), line, column));
								// remove entries from backlog that were used to create the merged token
								for (int j = 0; j < mergeCount + 1; j++)
								{
									readBacklog.removeFirst();
								}
								if (DEBUG_TOKENIZER)
									System.out.println("After removal: " + readBacklog);
								hasFoundToken = true;
								break;
							}
                        }
						if (!hasFoundToken)
						{
							if (DEBUG_TOKENIZER)
								System.out.println("Before not found: " + readBacklog);
							SmallToken first = readBacklog.getFirst();
							throw new RuntimeException("Unknown symbol " + first._token + " '" + (char) first._token + "', sval: " + first.sval + ", line: " + first.line + ", column: " + first.column);
						}
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
			if (_token == 0)
			{
				return "SmallToken{" + "type=" + type + ", sval='" + sval + '\'' + '}';
			}
			return "SmallToken{" + "type=" + type + ", sval='" + sval + '\'' + ", token=" + _token + '}';
		}

		public String string()
		{
			return sval == null ? "" + (char) _token : sval;
		}
	}
}
