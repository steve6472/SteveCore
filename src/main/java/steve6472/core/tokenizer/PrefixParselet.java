package steve6472.core.tokenizer;

/**********************
 * Created by steve6472
 * Date: 9/3/2022
 * Project: ScriptIt
 *
 ***********************/
public interface PrefixParselet<R>
{
	R parse(Tokenizer tokenizer, TokenParser<R> parser);
}
