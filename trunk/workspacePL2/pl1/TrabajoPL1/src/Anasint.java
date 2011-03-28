// $ANTLR : "Anasint.g" -> "Anasint.java"$

	import java.util.Hashtable;	

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;

public class Anasint extends antlr.LLkParser       implements AnalexTokenTypes
 {
Hashtable variables = new Hashtable(); 
protected Anasint(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public Anasint(TokenBuffer tokenBuf) {
  this(tokenBuf,1);
}

protected Anasint(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public Anasint(TokenStream lexer) {
  this(lexer,1);
}

public Anasint(ParserSharedInputState state) {
  super(state,1);
  tokenNames = _tokenNames;
}

	public final void entrada() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			{
			_loop386:
			do {
				if ((LA(1)==NUMERO||LA(1)==PA||LA(1)==IDENT)) {
					instruccion();
				}
				else {
					break _loop386;
				}
				
			} while (true);
			}
			match(Token.EOF_TYPE);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
	}
	
	public final void instruccion() throws RecognitionException, TokenStreamException {
		
		int e;
		
		try {      // for error handling
			e=expr();
			match(PYC);
			System.out.println("Expresion: "+e);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void asignacion() throws RecognitionException, TokenStreamException {
		
		Token  i = null;
		int e;
		
		try {      // for error handling
			i = LT(1);
			match(IDENT);
			match(ASIG);
			e=expr();
			match(PYC);
			System.out.println("Asignacion: "+i.getText() + "vale " +e);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
	}
	
	public final int  expr() throws RecognitionException, TokenStreamException {
		int res=0;
		
		int e1,e2;
		
		try {      // for error handling
			e1=exp_mult();
			res=e1;
			{
			_loop393:
			do {
				switch ( LA(1)) {
				case MAS:
				{
					{
					match(MAS);
					e2=exp_mult();
					res=res+e2;
					}
					break;
				}
				case MENOS:
				{
					{
					match(MENOS);
					e2=exp_mult();
					res=res-e2;
					}
					break;
				}
				default:
				{
					break _loop393;
				}
				}
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		return res;
	}
	
	public final int  exp_mult() throws RecognitionException, TokenStreamException {
		int res=0;
		
		int e1,e2;
		
		try {      // for error handling
			e1=exp_base();
			res=e1;
			{
			_loop398:
			do {
				switch ( LA(1)) {
				case POR:
				{
					{
					match(POR);
					e2=exp_base();
					res=res*e2;
					}
					break;
				}
				case DIV:
				{
					{
					match(DIV);
					e2=exp_base();
					res=res/2;
					}
					break;
				}
				default:
				{
					break _loop398;
				}
				}
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		return res;
	}
	
	public final int  exp_base() throws RecognitionException, TokenStreamException {
		int res=0;
		
		Token  n = null;
		Token  i = null;
		int e;
		
		try {      // for error handling
			switch ( LA(1)) {
			case NUMERO:
			{
				n = LT(1);
				match(NUMERO);
				res = valorNumero (n);
				break;
			}
			case PA:
			{
				match(PA);
				e=expr();
				match(PC);
				res=e;
				break;
			}
			case IDENT:
			{
				i = LT(1);
				match(IDENT);
				res=valorVariable(i);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_4);
		}
		return res;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"NUEVA_LINEA",
		"BLANCO",
		"DIGITO",
		"NUMERO",
		"PYC",
		"MAS",
		"MENOS",
		"POR",
		"DIV",
		"PA",
		"PC",
		"IDENT",
		"ASIG"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 41090L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 16640L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 18176L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 24320L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	
	}
