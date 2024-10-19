package steve6472.core.tokenizer.exceptions;

import steve6472.core.tokenizer.Token;
import steve6472.core.tokenizer.Tokenizer;

/**
 * Created by steve6472
 * Date: 9/3/2022
 * Project: ScriptIt
 */
public class WrongTokenException extends RuntimeException
{
	public WrongTokenException(Token expected, Token actual)
	{
		super("Expected token " + expected + ", got " + actual);
	}

	public WrongTokenException(Token expected, Tokenizer.SmallToken actual)
	{
		super("Expected token " + expected + ", got " + actual.type());
	}
}
