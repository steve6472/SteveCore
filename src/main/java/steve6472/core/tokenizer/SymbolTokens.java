package steve6472.core.tokenizer;

/**
 * Created by steve6472
 * Date: 9/3/2022
 * Project: ScriptIt
 */
@SuppressWarnings("unused")
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
	LSH("<<"),
	RSH(">>"),

	ASSIGN_ADD("+="),
	ASSIGN_SUB("-="),
	ASSIGN_MUL("*="),
	ASSIGN_DIV("/="),
	ASSIGN_MOD("%="),
	ASSIGN_BIT_AND("&="),
	ASSIGN_BIT_OR("|="),
	ASSIGN_BIT_XOR("^="),

	SINGLE_LINE_COMMENT("//"),
	MULTI_LINE_COMMENT_BEGIN("/*"),
	MULTI_LINE_COMMENT_END("*/"),

	ASSIGN_LSH("<<="),
	ASSIGN_RSH(">>="),
	IDK_THE_NAME_OF_THIS_ONE(">>>="),

	// Equality
	EQUALS("=="),
	NOT_EQUAL("!="),

	// Relativity
	LESS_THAN_EQUAL("<="),
	GREATER_THAN_EQUAL(">="),
	LESS_THAN("<"),
	GREATER_THAN(">"),

	QUESTION("?"),
	COLON(":"),

	// Boolean
	NOT("!"),
	AND("&&"),
	OR("||"),

	PRE_INC("++"),
	PRE_DEC("--"),

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
	INDEX("[]")

	;

	private final String symbol;

	SymbolTokens(String symbol)
	{
		this.symbol = symbol;
	}

	@Override
	public String getSymbol()
	{
		return symbol;
	}
}
