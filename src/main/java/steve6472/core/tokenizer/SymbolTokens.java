package steve6472.core.tokenizer;

/**
 * Created by steve6472
 * Date: 9/3/2022
 * Project: ScriptIt
 */
public enum SymbolTokens implements Token
{
	// Mathematics / Binary
	ADD("+"),
	SUB("-"),
	MUL("*"),
	DIV("/"),
	MOD("%"),
	NEG("~"),
	BIT_AND("&"),
	BIT_OR("|"),
	BIT_XOR("^"),
	LSH("<<", true),
	RSH(">>", true),

	ASSIGN_ADD("+=", true),
	ASSIGN_SUB("-=", true),
	ASSIGN_MUL("*=", true),
	ASSIGN_DIV("/=", true),
	ASSIGN_MOD("%=", true),
	ASSIGN_BIT_AND("&=", true),
	ASSIGN_BIT_OR("|=", true),
	ASSIGN_BIT_XOR("^=", true),

	SINGLE_LINE_COMMENT("//", true),
	MULTI_LINE_COMMENT_BEGIN("/*", true),
	MULTI_LINE_COMMENT_END("*/", true),

	/* FIXME
	 * The merging in MiniTokenizer/TokenParser would die cause it checks only the first TWO characters
	 * and as you can see these SHITS have THREE characters
	 * AAAAAAAAAAAAAAA
	 *
	 * I don't wanna deal with this
	 * It's just... not gonna get implemented for a while
	 */
	//ASSIGN_LSH("<<=", true),
	//ASSIGN_RSH(">>=", true),

	// Equality
	EQUALS("==", true),
	NOT_EQUAL("!=", true),

	// Relativity
	LESS_THAN_EQUAL("<=", true),
	GREATER_THAN_EQUAL(">=", true),
	LESS_THAN("<"),
	GREATER_THAN(">"),

	QUESTION("?"),
	COLON(":"),

	// Boolean
	NOT("!"),
	AND("&&", true),
	OR("||", true),

	PRE_INC("++", true),
	PRE_DEC("--", true),

	// Special
	COMMA(","),
	EQUAL("="),
	DOT("."),
	SEMICOLON(";"),
	OCTOTHORP("#"),

	// Brackets
	BRACKET_LEFT("("),
	BRACKET_RIGHT(")"),
	BRACKET_CURLY_LEFT("{"),
	BRACKET_CURLY_RIGHT("}"),
	BRACKET_SQUARE_LEFT("["),
	BRACKET_SQUARE_RIGHT("]"),
	INDEX("[]", true)

	;

	private final String symbol;
	private final boolean isMerge;

	SymbolTokens()
	{
		this("", false);
	}

	SymbolTokens(String symbol)
	{
		this(symbol, false);
	}

	SymbolTokens(String symbol, boolean isMerge)
	{
		this.symbol = symbol;
		this.isMerge = isMerge;
	}

	@Override
	public String getSymbol()
	{
		return symbol;
	}

	@Override
	public boolean isMerge()
	{
		return isMerge;
	}
}
