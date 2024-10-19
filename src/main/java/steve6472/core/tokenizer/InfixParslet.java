package steve6472.core.tokenizer;

/**********************
 * Created by steve6472
 * Date: 9/3/2022
 * Project: ScriptIt
 *
 ***********************/
public interface InfixParslet<R>
{
	R parse(Tokenizer tokenizer, R left);

	Precedence getPrecedence();
}
