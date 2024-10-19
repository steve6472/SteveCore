package steve6472.core.tokenizer;

/**
 * Created by steve6472
 * Date: 9/2/2022
 * Project: ScriptIt
 */
public interface Token
{
	String getSymbol();

	boolean isMerge();

	int ordinal();
}
