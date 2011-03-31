// $ANTLR : "Analex.g" -> "Analex.java"$

import java.io.InputStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.ANTLRException;
import java.io.Reader;
import java.util.Hashtable;
import antlr.CharScanner;
import antlr.InputBuffer;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.Token;
import antlr.CommonToken;
import antlr.RecognitionException;
import antlr.NoViableAltForCharException;
import antlr.MismatchedCharException;
import antlr.TokenStream;
import antlr.ANTLRHashString;
import antlr.LexerSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.SemanticException;

public class Analex extends antlr.CharScanner implements AnalexTokenTypes, TokenStream
 {
public Analex(InputStream in) {
	this(new ByteBuffer(in));
}
public Analex(Reader in) {
	this(new CharBuffer(in));
}
public Analex(InputBuffer ib) {
	this(new LexerSharedInputState(ib));
}
public Analex(LexerSharedInputState state) {
	super(state);
	caseSensitiveLiterals = true;
	setCaseSensitive(true);
	literals = new Hashtable();
	literals.put(new ANTLRHashString("BEGIN", this), new Integer(39));
	literals.put(new ANTLRHashString("ENTERO", this), new Integer(40));
	literals.put(new ANTLRHashString("CARACTER", this), new Integer(43));
	literals.put(new ANTLRHashString("REAL", this), new Integer(41));
	literals.put(new ANTLRHashString("ESCRIBIR", this), new Integer(55));
	literals.put(new ANTLRHashString("SINO", this), new Integer(57));
	literals.put(new ANTLRHashString("LEER", this), new Integer(54));
	literals.put(new ANTLRHashString("ARRAY", this), new Integer(48));
	literals.put(new ANTLRHashString("CONST", this), new Integer(27));
	literals.put(new ANTLRHashString("SI", this), new Integer(56));
	literals.put(new ANTLRHashString("DESDE", this), new Integer(60));
	literals.put(new ANTLRHashString("REGISTRO", this), new Integer(32));
	literals.put(new ANTLRHashString("VAR", this), new Integer(34));
	literals.put(new ANTLRHashString("NOT", this), new Integer(65));
	literals.put(new ANTLRHashString("FUNCION", this), new Integer(36));
	literals.put(new ANTLRHashString("AND", this), new Integer(64));
	literals.put(new ANTLRHashString("MIENTRAS", this), new Integer(58));
	literals.put(new ANTLRHashString("HASTA", this), new Integer(61));
	literals.put(new ANTLRHashString("END", this), new Integer(33));
	literals.put(new ANTLRHashString("BOOLEANO", this), new Integer(42));
	literals.put(new ANTLRHashString("ATRAS", this), new Integer(62));
	literals.put(new ANTLRHashString("CIERTO", this), new Integer(75));
	literals.put(new ANTLRHashString("CADENA", this), new Integer(52));
	literals.put(new ANTLRHashString("OF", this), new Integer(51));
	literals.put(new ANTLRHashString("MOD", this), new Integer(78));
	literals.put(new ANTLRHashString("OR", this), new Integer(63));
	literals.put(new ANTLRHashString("DIV", this), new Integer(77));
	literals.put(new ANTLRHashString("FALSO", this), new Integer(76));
	literals.put(new ANTLRHashString("TYPE", this), new Integer(31));
	literals.put(new ANTLRHashString("HACER", this), new Integer(59));
}

public Token nextToken() throws TokenStreamException {
	Token theRetToken=null;
tryAgain:
	for (;;) {
		Token _token = null;
		int _ttype = Token.INVALID_TYPE;
		resetText();
		try {   // for char stream error handling
			try {   // for lexical error handling
				switch ( LA(1)) {
				case '\t':  case ' ':
				{
					mBT(true);
					theRetToken=_returnToken;
					break;
				}
				case '\r':
				{
					mSL(true);
					theRetToken=_returnToken;
					break;
				}
				case '(':
				{
					mPARENTESIS_ABIERTO(true);
					theRetToken=_returnToken;
					break;
				}
				case ')':
				{
					mPARENTESIS_CERRADO(true);
					theRetToken=_returnToken;
					break;
				}
				case '{':
				{
					mLLAVE_ABIERTA(true);
					theRetToken=_returnToken;
					break;
				}
				case '}':
				{
					mLLAVE_CERRADA(true);
					theRetToken=_returnToken;
					break;
				}
				case '[':
				{
					mCORCHETE_ABIERTO(true);
					theRetToken=_returnToken;
					break;
				}
				case ']':
				{
					mCORCHETE_CERRADO(true);
					theRetToken=_returnToken;
					break;
				}
				case ',':
				{
					mCOMA(true);
					theRetToken=_returnToken;
					break;
				}
				case ';':
				{
					mPUNTO_Y_COMA(true);
					theRetToken=_returnToken;
					break;
				}
				case '.':
				{
					mPUNTO(true);
					theRetToken=_returnToken;
					break;
				}
				case '+':
				{
					mMAS(true);
					theRetToken=_returnToken;
					break;
				}
				case '-':
				{
					mMENOS(true);
					theRetToken=_returnToken;
					break;
				}
				case '*':
				{
					mPOR(true);
					theRetToken=_returnToken;
					break;
				}
				case '/':
				{
					mDIVISION(true);
					theRetToken=_returnToken;
					break;
				}
				case '=':
				{
					mIGUAL(true);
					theRetToken=_returnToken;
					break;
				}
				case '!':
				{
					mDISTINTO(true);
					theRetToken=_returnToken;
					break;
				}
				case '0':  case '1':  case '2':  case '3':
				case '4':  case '5':  case '6':  case '7':
				case '8':  case '9':
				{
					mNUMERO(true);
					theRetToken=_returnToken;
					break;
				}
				case '"':  case '\'':
				{
					mCADENA(true);
					theRetToken=_returnToken;
					break;
				}
				case 'A':  case 'B':  case 'C':  case 'D':
				case 'E':  case 'F':  case 'G':  case 'H':
				case 'I':  case 'J':  case 'K':  case 'L':
				case 'M':  case 'N':  case 'O':  case 'P':
				case 'Q':  case 'R':  case 'S':  case 'T':
				case 'U':  case 'V':  case 'W':  case 'X':
				case 'Y':  case 'Z':  case 'a':  case 'b':
				case 'c':  case 'd':  case 'e':  case 'f':
				case 'g':  case 'h':  case 'i':  case 'j':
				case 'k':  case 'l':  case 'm':  case 'n':
				case 'o':  case 'p':  case 'q':  case 'r':
				case 's':  case 't':  case 'u':  case 'v':
				case 'w':  case 'x':  case 'y':  case 'z':
				{
					mIDENT(true);
					theRetToken=_returnToken;
					break;
				}
				default:
					if ((LA(1)=='<') && (LA(2)=='=')) {
						mMENOR_IGUAL(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='>') && (LA(2)=='=')) {
						mMAYOR_IGUAL(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)==':') && (LA(2)=='=')) {
						mASIGNACION(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)==':') && (true)) {
						mDOS_PUNTOS(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='<') && (true)) {
						mMENOR(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='>') && (true)) {
						mMAYOR(true);
						theRetToken=_returnToken;
					}
				else {
					if (LA(1)==EOF_CHAR) {uponEOF(); _returnToken = makeToken(Token.EOF_TYPE);}
				else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				}
				if ( _returnToken==null ) continue tryAgain; // found SKIP token
				_ttype = _returnToken.getType();
				_returnToken.setType(_ttype);
				return _returnToken;
			}
			catch (RecognitionException e) {
				throw new TokenStreamRecognitionException(e);
			}
		}
		catch (CharStreamException cse) {
			if ( cse instanceof CharStreamIOException ) {
				throw new TokenStreamIOException(((CharStreamIOException)cse).io);
			}
			else {
				throw new TokenStreamException(cse.getMessage());
			}
		}
	}
}

	public final void mBT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = BT;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case ' ':
		{
			match(' ');
			break;
		}
		case '\t':
		{
			match('\t');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			_ttype = Token.SKIP;
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SL;
		int _saveIndex;
		
		match("\r\n");
		if ( inputState.guessing==0 ) {
			newline();_ttype = Token.SKIP;
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mDOS_PUNTOS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DOS_PUNTOS;
		int _saveIndex;
		
		match(':');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPARENTESIS_ABIERTO(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PARENTESIS_ABIERTO;
		int _saveIndex;
		
		match('(');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPARENTESIS_CERRADO(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PARENTESIS_CERRADO;
		int _saveIndex;
		
		match(')');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLLAVE_ABIERTA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LLAVE_ABIERTA;
		int _saveIndex;
		
		match('{');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLLAVE_CERRADA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LLAVE_CERRADA;
		int _saveIndex;
		
		match('}');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCORCHETE_ABIERTO(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CORCHETE_ABIERTO;
		int _saveIndex;
		
		match('[');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCORCHETE_CERRADO(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CORCHETE_CERRADO;
		int _saveIndex;
		
		match(']');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCOMA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = COMA;
		int _saveIndex;
		
		match(',');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPUNTO_Y_COMA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PUNTO_Y_COMA;
		int _saveIndex;
		
		match(';');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPUNTO(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PUNTO;
		int _saveIndex;
		
		match('.');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mMAS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MAS;
		int _saveIndex;
		
		match('+');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mMENOS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MENOS;
		int _saveIndex;
		
		match('-');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = POR;
		int _saveIndex;
		
		match('*');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mDIVISION(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DIVISION;
		int _saveIndex;
		
		match('/');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mMENOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MENOR;
		int _saveIndex;
		
		match('<');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mMENOR_IGUAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MENOR_IGUAL;
		int _saveIndex;
		
		match("<=");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mMAYOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MAYOR;
		int _saveIndex;
		
		match('>');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mMAYOR_IGUAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MAYOR_IGUAL;
		int _saveIndex;
		
		match(">=");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mIGUAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = IGUAL;
		int _saveIndex;
		
		match('=');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mDISTINTO(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DISTINTO;
		int _saveIndex;
		
		match("!=");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mASIGNACION(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ASIGNACION;
		int _saveIndex;
		
		match(":=");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mDIGITO(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DIGITO;
		int _saveIndex;
		
		{
		matchRange('0','9');
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mLETRA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LETRA;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case 'a':  case 'b':  case 'c':  case 'd':
		case 'e':  case 'f':  case 'g':  case 'h':
		case 'i':  case 'j':  case 'k':  case 'l':
		case 'm':  case 'n':  case 'o':  case 'p':
		case 'q':  case 'r':  case 's':  case 't':
		case 'u':  case 'v':  case 'w':  case 'x':
		case 'y':  case 'z':
		{
			matchRange('a','z');
			break;
		}
		case 'A':  case 'B':  case 'C':  case 'D':
		case 'E':  case 'F':  case 'G':  case 'H':
		case 'I':  case 'J':  case 'K':  case 'L':
		case 'M':  case 'N':  case 'O':  case 'P':
		case 'Q':  case 'R':  case 'S':  case 'T':
		case 'U':  case 'V':  case 'W':  case 'X':
		case 'Y':  case 'Z':
		{
			matchRange('A','Z');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mNUMERO(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NUMERO;
		int _saveIndex;
		
		boolean synPredMatched1505 = false;
		if ((((LA(1) >= '0' && LA(1) <= '9')) && (_tokenSet_0.member(LA(2))))) {
			int _m1505 = mark();
			synPredMatched1505 = true;
			inputState.guessing++;
			try {
				{
				{
				int _cnt1502=0;
				_loop1502:
				do {
					if (((LA(1) >= '0' && LA(1) <= '9'))) {
						mDIGITO(false);
					}
					else {
						if ( _cnt1502>=1 ) { break _loop1502; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
					}
					
					_cnt1502++;
				} while (true);
				}
				match('.');
				{
				int _cnt1504=0;
				_loop1504:
				do {
					if (((LA(1) >= '0' && LA(1) <= '9'))) {
						mDIGITO(false);
					}
					else {
						if ( _cnt1504>=1 ) { break _loop1504; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
					}
					
					_cnt1504++;
				} while (true);
				}
				}
			}
			catch (RecognitionException pe) {
				synPredMatched1505 = false;
			}
			rewind(_m1505);
inputState.guessing--;
		}
		if ( synPredMatched1505 ) {
			{
			int _cnt1507=0;
			_loop1507:
			do {
				if (((LA(1) >= '0' && LA(1) <= '9'))) {
					mDIGITO(false);
				}
				else {
					if ( _cnt1507>=1 ) { break _loop1507; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt1507++;
			} while (true);
			}
			match('.');
			{
			int _cnt1509=0;
			_loop1509:
			do {
				if (((LA(1) >= '0' && LA(1) <= '9'))) {
					mDIGITO(false);
				}
				else {
					if ( _cnt1509>=1 ) { break _loop1509; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt1509++;
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				_ttype = LIT_REAL;
			}
		}
		else {
			boolean synPredMatched1513 = false;
			if ((((LA(1) >= '0' && LA(1) <= '9')) && (true))) {
				int _m1513 = mark();
				synPredMatched1513 = true;
				inputState.guessing++;
				try {
					{
					{
					int _cnt1512=0;
					_loop1512:
					do {
						if (((LA(1) >= '0' && LA(1) <= '9'))) {
							mDIGITO(false);
						}
						else {
							if ( _cnt1512>=1 ) { break _loop1512; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
						}
						
						_cnt1512++;
					} while (true);
					}
					}
				}
				catch (RecognitionException pe) {
					synPredMatched1513 = false;
				}
				rewind(_m1513);
inputState.guessing--;
			}
			if ( synPredMatched1513 ) {
				{
				int _cnt1515=0;
				_loop1515:
				do {
					if (((LA(1) >= '0' && LA(1) <= '9'))) {
						mDIGITO(false);
					}
					else {
						if ( _cnt1515>=1 ) { break _loop1515; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
					}
					
					_cnt1515++;
				} while (true);
				}
				if ( inputState.guessing==0 ) {
					_ttype = LIT_ENTERO;
				}
			}
			else {
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		}
		
	public final void mCADENA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CADENA;
		int _saveIndex;
		
		boolean synPredMatched1519 = false;
		if (((LA(1)=='"') && (_tokenSet_1.member(LA(2))))) {
			int _m1519 = mark();
			synPredMatched1519 = true;
			inputState.guessing++;
			try {
				{
				match('"');
				{
				matchNot('"');
				}
				match('"');
				}
			}
			catch (RecognitionException pe) {
				synPredMatched1519 = false;
			}
			rewind(_m1519);
inputState.guessing--;
		}
		if ( synPredMatched1519 ) {
			match('"');
			{
			matchNot('"');
			}
			match('"');
			if ( inputState.guessing==0 ) {
				_ttype = LIT_CARACTER;
			}
		}
		else if ((LA(1)=='"') && ((LA(2) >= '\u0000' && LA(2) <= '\u007f'))) {
			match('"');
			{
			_loop1522:
			do {
				if ((_tokenSet_1.member(LA(1)))) {
					matchNot('"');
				}
				else {
					break _loop1522;
				}
				
			} while (true);
			}
			match('"');
		}
		else {
			boolean synPredMatched1525 = false;
			if (((LA(1)=='\'') && (_tokenSet_2.member(LA(2))))) {
				int _m1525 = mark();
				synPredMatched1525 = true;
				inputState.guessing++;
				try {
					{
					match("\'");
					{
					matchNot('\'');
					}
					match("\'");
					}
				}
				catch (RecognitionException pe) {
					synPredMatched1525 = false;
				}
				rewind(_m1525);
inputState.guessing--;
			}
			if ( synPredMatched1525 ) {
				match("\'");
				{
				matchNot('\'');
				}
				match("\'");
				if ( inputState.guessing==0 ) {
					_ttype = LIT_CARACTER;
				}
			}
			else if ((LA(1)=='\'') && ((LA(2) >= '\u0000' && LA(2) <= '\u007f'))) {
				match("\'");
				{
				_loop1528:
				do {
					if ((_tokenSet_2.member(LA(1)))) {
						matchNot('\'');
					}
					else {
						break _loop1528;
					}
					
				} while (true);
				}
				match("\'");
			}
			else {
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		}
		
	public final void mIDENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = IDENT;
		int _saveIndex;
		
		mLETRA(false);
		{
		_loop1531:
		do {
			switch ( LA(1)) {
			case 'A':  case 'B':  case 'C':  case 'D':
			case 'E':  case 'F':  case 'G':  case 'H':
			case 'I':  case 'J':  case 'K':  case 'L':
			case 'M':  case 'N':  case 'O':  case 'P':
			case 'Q':  case 'R':  case 'S':  case 'T':
			case 'U':  case 'V':  case 'W':  case 'X':
			case 'Y':  case 'Z':  case 'a':  case 'b':
			case 'c':  case 'd':  case 'e':  case 'f':
			case 'g':  case 'h':  case 'i':  case 'j':
			case 'k':  case 'l':  case 'm':  case 'n':
			case 'o':  case 'p':  case 'q':  case 'r':
			case 's':  case 't':  case 'u':  case 'v':
			case 'w':  case 'x':  case 'y':  case 'z':
			{
				mLETRA(false);
				break;
			}
			case '0':  case '1':  case '2':  case '3':
			case '4':  case '5':  case '6':  case '7':
			case '8':  case '9':
			{
				mDIGITO(false);
				break;
			}
			default:
			{
				break _loop1531;
			}
			}
		} while (true);
		}
		_ttype = testLiteralsTable(_ttype);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 288019269919178752L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { -17179869185L, -1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { -549755813889L, -1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	
	}
