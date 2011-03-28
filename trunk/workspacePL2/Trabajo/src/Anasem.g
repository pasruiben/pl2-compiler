header
{
	import java.util.*;
	import javax.swing.JOptionPane;
	import javax.swing.JPanel;
}

class Anasem extends TreeParser;

//tipos rango.. pasan a INTEGER o CHAR en Atr_Expr

options
{
	importVocab = Anasint;
	buildAST = true;
}

{
	JPanel jContentPanel = new JPanel();
	
	//As
	//Asigna tipo, Lval y Rval segn el tipo de declaracin del acceso simple
	Atr_Expr AS_Acceso_Simple(AST dec) throws RecognitionException
	{
		Atr_Expr res = new Atr_Expr();
		AST t = null;
				
		switch(dec.getType())
		{
			case VARIABLE_RESULTADO: case VARIABLE:
				t = dec.getFirstChild().getNextSibling();
				if (t.getType() == RANGO)
					t = ajustaTipoRango(t);
				res.setLval(true);
				res.setRval(true);
				break;
			case CONSTANTE:
				t = expresion(dec.getFirstChild().getNextSibling()).getTipo();
				res.setLval(false);
				res.setRval(true);
				break;
			case PROCEDIMIENTO: case FUNCION:
				t = dec.getFirstChild();
				res.setLval(false);
				res.setRval(false);
				break;
			case PARAMETRO:
				t = dec.getFirstChild().getNextSibling();
				if (t.getType() == RANGO)
					t = ajustaTipoRango(t);
				res.setLval(false);
				res.setRval(true);
				break;			
			case REGISTRO:
				{
		 			JOptionPane.showMessageDialog(
							jContentPanel,
							"ERROR, ACCESO SIMPLE AL NOMBRE DE UN REGISTRO",
							"Error",
							JOptionPane.INFORMATION_MESSAGE);
		 		 	System.exit(1);
				}
				break;
			default:
				System.out.println("Acceso simple no contemplado");
		}
		
		res.setTipo(t);

		return res;
	}
	
	//Anndices sean de tipo entero
	Atr_Expr AS_Acceso_Array(Atr_Expr atr_raiz, AST indices) throws RecognitionException
	{
		Atr_Expr res = new Atr_Expr();
		AST t = null, aux;
		
		if (atr_raiz.getTipo().getType() != ARRAY)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, ACCESO A ARRAY SOBRE ALGO QUE NO ES UN ARRAY",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
		
		aux = indices.getFirstChild();
		
		if (indices.getNumberOfChildren() != atr_raiz.getTipo().getFirstChild().getNumberOfChildren())
		{
			JOptionPane.showMessageDialog(
								jContentPanel,
								" ERROR, EL NUMERO DE INDICES EN EL ACCESO A ARRAY NO ES EL ADECUADO",
								"Error",
								JOptionPane.INFORMATION_MESSAGE);
							System.exit(1); 	
		}
		
		while(aux != null)
		{		
			if(expresion(aux).getTipo().getType() != INTEGER)
			{
				JOptionPane.showMessageDialog(
									jContentPanel,
									" ERROR, ALGUNO DE LOS NDICES EN EL ACCESO A ARRAY NO ES ENTERO",
									"Error",
									JOptionPane.INFORMATION_MESSAGE);
 				System.exit(1); 	
			}
			
			aux = aux.getNextSibling();
		}

		//tipo base
		t = atr_raiz.getTipo().getFirstChild().getNextSibling();
		if (t.getType() == RANGO)
			t = ajustaTipoRango(t);
		
		res.setTipo(t);
		
		//lval y rval los hereda de la ra
		res.setLval(atr_raiz.getLval());
		res.setRval(atr_raiz.getRval());

		return res;
	}
	
	//Recibe un tipo rango (entero o caracter) y devuelve entero o caracter
	AST ajustaTipoRango(AST tipo_rango)
	{
		if (tipo_rango.getFirstChild().getType() == LIT_CAR)
			return #(#[CHAR,"CHAR"]);
		else
			return #(#[INTEGER,"INTEGER"]);
	}
	
	//Anntico de los accesos a registros
	//Comprueba que la rasea de tipo registro
	//Comprueba que existe el campo al que se pretende acceder
	//Realiza las llamadas recursivas si el acceso contina
	Atr_Expr AS_Acceso_Registro(Atr_Expr atr_raiz, AST acceso) throws RecognitionException
	{
		Atr_Expr res = new Atr_Expr();
		AST aux, t = null, campo = null;
		
		switch(acceso.getType())
		{
			case ACCESO_REGISTRO: case ACCESO_ARRAY_REGISTRO: case ACCESO_ARRAY:
				campo = acceso.getFirstChild();
				break;
			case IDENT:
				campo = acceso;
				break;
		}
		
		if (atr_raiz.getTipo().getType() != REGISTRO)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, ACCESO A REGISTRO SOBRE ALGO QUE NO ES UN REGISTRO",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}

		aux = atr_raiz.getTipo().getFirstChild().getNextSibling();

		while (aux != null)
		{
			//si encuentra el campo
			if (aux.getFirstChild().equals(campo))
				break;
			
			aux = aux.getNextSibling();
		}
		
		if (aux == null)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, ACCESO A CAMPO " + campo.getText() + " INEXISTENTE EN EL REGISTRO " + atr_raiz.getTipo().getFirstChild().getText(),
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
		
		t = aux.getFirstChild().getNextSibling(); //el tipo del campo
		
		if (t.getType() == RANGO)
			t = ajustaTipoRango(t);
		
		res.setTipo(t);
		res.setLval(true);
		res.setRval(true);
		
		//llamadas recursivas si no era un caso base
		switch(acceso.getType())
		{
			case ACCESO_ARRAY:
				res = AS_Acceso_Array(res, acceso.getFirstChild().getNextSibling());
				break;
			case ACCESO_REGISTRO:
				res = AS_Acceso_Registro(res, acceso.getFirstChild().getNextSibling());
				break;
			case ACCESO_ARRAY_REGISTRO: 
				res = AS_Acceso_Array_Registro(res, acceso.getFirstChild().getNextSibling(), acceso.getFirstChild().getNextSibling().getNextSibling());
				break;			
		}

		return res;
	}
	
	//Antico de los accesos a array seguidos de acceso a registro
	Atr_Expr AS_Acceso_Array_Registro(Atr_Expr atr_raiz, AST indices, AST acceso) throws RecognitionException
	{
		Atr_Expr res = new Atr_Expr();
		
		res = AS_Acceso_Array(atr_raiz, indices);
		
		res = AS_Acceso_Registro(res, acceso);
		
		return res;
	}
	
	//Funcin auxiliar que devuelve el tipo de un AST representando a un literal
	AST tipoLiteral(AST li)
	{
		AST tipo = null;				
		
		switch (li.getType())
		{
			case LIT_ENTERO: tipo = #(#[INTEGER,"INTEGER"]); break;
			case LIT_REAL: tipo = #(#[REAL,"REAL"]); break;
			case LIT_CAR: tipo = #(#[CHAR,"CHAR"]); break;
			case CADENA: tipo = #(#[STRING, "STRING"]); break;
			case TRUE: case FALSE: tipo = #(#[BOOLEAN,"BOOLEAN"]); break;
			default: System.out.println("tipoLiteral sin tipo conocido");		
		}
		
		return tipo;
	}
	
	//Anntico de literales
	Atr_Expr AS_Literal(AST li)
	{
		Atr_Expr result = new Atr_Expr();
		
		result.setTipo(tipoLiteral(li));
		result.setLval(false);
		result.setRval(true);
		
		return result;
	}
	
	//A
	//Comprueba que e1 tiene Lval
	//Comprueba que e2 tiene Rval
	//Comprueba equivalencia de tipos
	void AS_Asignacion(Atr_Expr e1, Atr_Expr e2)
	{
		if (!e1.getLval())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA EXPRESION A LA IZQUIERDA EN LA ASIGNACION NO TIENE LVAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}		
			
		if (!e2.getRval())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA EXPRESINo A LA DERECHA EN LA ASIGNACIiN NO TIENE RVAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}			
			
		if (!tiposEquivalentes(e1.getTipo(), e2.getTipo()))
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LAS EXPRESIONES EN LA ASIGNACIN NO TIENEN IGUAL TIPO, UNA ES DE TIPO " + e1.getTipo().getText() + " Y LA OTRA DE TIPO " + e2.getTipo().getText(),
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}	
	}
	
	//Funci.n auxiliar para comprobar equivalencia de tipos
	boolean tiposEquivalentes(AST tipo1, AST tipo2)
	{
		//por nombre
		if (!tipo1.equals(tipo2))
			return false;
		
		//si son registros, por nombre del registro
		if (tipo1.getType() == REGISTRO)
			return tipo1.getFirstChild().equals(tipo2.getFirstChild());
			
		return true;
	}
	
	//An.lisis sem.ntico de la cabecera del for
	//Comprueba que a tenga Lval
	//Comprueba que e1 y e2 tengan Rval
	//Comprueba que a, e1 y e2 sean de tipo Integer
	void AS_Cabecera_For(Atr_Expr a, Atr_Expr e1, Atr_Expr e2)
	{
		if (!a.getLval())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, EL ACCESO EN LA CABECERA DEL FOR NO TIENE LVAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}		
			
		if (!e1.getRval())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA PRIMERA EXPRESI.N EN LA CABECERA DEL FOR NO TIENE RVAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}			
			
		if (!e2.getRval())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA SEGUNDA EXPRESI.N EN LA CABECERA DEL FOR NO TIENE RVAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}	
			
		if (a.getTipo().getType() != INTEGER)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, EL ACCESO EN LA CABECERA DEL FOR NO ES DE TIPO ENTERO",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}	
		
		if (e1.getTipo().getType() != INTEGER)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA PRIMERA EXPRESI.N EN LA CABECERA DEL FOR NO ES DE TIPO ENTERO",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}	
		
		if (e2.getTipo().getType() != INTEGER)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA SEGUNDA EXPRESI.N EN LA CABECERA DEL FOR NO ES DE TIPO ENTERO",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}	
	}
	
	//An.lisis sem.ntico de las llamadas a rutinas
	//Comprueba que la ra.z sea de tipo cabecera
	//Comprueba que los par.metros reales se correspondan con los formales
	Atr_Expr AS_Llamada(Atr_Expr atr_raiz, LinkedList expresiones)
	{
		Atr_Expr res = new Atr_Expr();		
		Atr_Expr par_r; //par.metro real
		AST par_f, tipo; //par.metro formal
		Iterator it = expresiones.iterator();	
		
		if (atr_raiz.getTipo().getType() != CABECERA)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, SE HACE UNA LLAMADA SOBRE ALGO QUE NO ES UNA RUTINA",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}	
		
		String nombreRutina = atr_raiz.getTipo().getFirstChild().getText();		
		
		//nos situamos en el primer par.metro formal
		par_f = atr_raiz.getTipo().getFirstChild().getNextSibling().getFirstChild();
		
		while (par_f != null && it.hasNext())
		{
			par_r = (Atr_Expr)it.next();
			
			AST tipo_r = par_r.getTipo();
			if (tipo_r.getType() == RANGO)
				tipo_r = ajustaTipoRango(tipo_r);
			
			AST tipo_f = par_f.getFirstChild().getNextSibling();
			if (tipo_f.getType() == RANGO)
				tipo_f = ajustaTipoRango(tipo_f);
			
			if (!tipo_r.equals(tipo_f))
			{
	 			JOptionPane.showMessageDialog(
						jContentPanel,
						"ERROR, LOS PAR.METROS FORMALES NO COINCIDEN CON LOS REALES EN TIPO EN LA LLAMADA A " + nombreRutina,
						"Error",
						JOptionPane.INFORMATION_MESSAGE);
	 		 	System.exit(1);
			}
			
			par_f = par_f.getNextSibling();
		}
		
		if (par_f != null && !it.hasNext())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA LLAMADA NO TIENE SUFICIENTES PAR.METROS REALES",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
		
		if (par_f == null && it.hasNext())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA LLAMADA TIENE DEMASIADOS PAR.METROS REALES",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
		
		tipo = atr_raiz.getTipo().getFirstChild().getNextSibling().getNextSibling();
		if (tipo.getType() == RANGO)
			tipo = ajustaTipoRango(tipo);
		
		res.setTipo(tipo);
		res.setLval(false);
		res.setRval(true);
		
		return res;
	}
	
	//An.lisis sem.ntico de la instrucci.n de entrada
	//Comprueba que la expresi.n tenga LVal
	void AS_Entrada(Atr_Expr e)
	{
		if (!e.getLval())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA EXPRESI.N EN 'READ' NO TIENE LVAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
	}
	
	//An.lisis sem.ntico de la instrucci.n de salida
	//Comprueba que las expresiones tengan Rval
	void AS_Salida(LinkedList l)
	{
		Iterator it = l.iterator();
		Atr_Expr aux;
		
		while(it.hasNext())
		{
			aux = (Atr_Expr)it.next();
			
			if (!aux.getRval())
			{
	 			JOptionPane.showMessageDialog(
						jContentPanel,
						"ERROR, ALGUNA EXPRESI.N EN 'WRITE' NO TIENE RVAL",
						"Error",
						JOptionPane.INFORMATION_MESSAGE);
	 		 	System.exit(1);
			}
		}
		
	}
		
	//An.lisis sem.ntico de las condiciones
	//Comprueba que e sea de tipo Boolean
	void AS_Condicion(Atr_Expr e)
	{
		if (!e.getRval())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA CONDICION NO TIENE RVAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
			
		if (e.getTipo().getType() != BOOLEAN)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA CONDICION NO ES DE TIPO BOOLEAN",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
	}
	
	//An.lisis sem.ntico de las expresiones binarias aritm.ticas
	//Comprueba que las dos expresiones tengan Rval
	//Comprueba (por nombre) la igualdad de tipos de e1 y e2
	//Comprueba que el tipo sea integer, real o string
	//Si es string comprueba que la operaci.n sea la de concatenaci.n
	//Si son operadores que s.lo admiten un tipo comprueba que el tipo sea el correcto
	Atr_Expr AS_Exp_Bin_Arit(Atr_Expr e1, Atr_Expr e2, AST op)
	{
		Atr_Expr result = new Atr_Expr();
		
		if (!e1.getRval() || !e2.getRval())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, UNA DE LAS EXPRESIONES EN LA EXPR_BIN_ARIT CON OPERADOR " + op.getText() + " NO TIENE RVAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
			
		if (!e1.getTipo().equals(e2.getTipo()))
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LAS EXPRESIONES EN LA EXPR_BIN_ARIT CON OPERADOR " + op.getText() + " NO TIENEN IGUAL TIPO",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
			
		if (e1.getTipo().getType() != INTEGER && e1.getTipo().getType() != REAL && e1.getTipo().getType() != STRING)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LAS EXPRESIONES EN LA EXPR_BIN_ARIT NO SON NI DE TIPO ENTERO NI REAL NI CADENA",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
		
		if (e1.getTipo().getType() == STRING && op.getType() != MAS)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, A LAS EXPRESIONES DE TIPO CADENA S.LO SE LES PUEDE APLICAR EL OPERADOR +",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
			
		switch(op.getType())
		{
			case DIVISION_REAL:
				if (e1.getTipo().getType() != REAL)
				{
		 			JOptionPane.showMessageDialog(
							jContentPanel,
							"ERROR, EL OPERADOR " + op.getText() + " NO ES APLICABLE A EXPRESIONES DE TIPO DISTINTO A REAL",
							"Error",
							JOptionPane.INFORMATION_MESSAGE);
		 		 	System.exit(1);
				}
				break;
			case DIVISION_ENTERA: case MOD:
				if (e1.getTipo().getType() != INTEGER)
				{
		 			JOptionPane.showMessageDialog(
							jContentPanel,
							"ERROR, EL OPERADOR " + op.getText() + " NO ES APLICABLE A EXPRESIONES DE TIPO DISTINTO A ENTERO",
							"Error",
							JOptionPane.INFORMATION_MESSAGE);
		 		 	System.exit(1);
				}
				break;			
		}
			
		result.setTipo(e1.getTipo());
		result.setLval(false);
		result.setRval(true);	
		
		return result;	
	}
		
	//An.lisis sem.ntico de las expresiones binarias l.gicas
	//Comprueba que ambas sean de tipo boolean
	Atr_Expr AS_Exp_Bin_Log(Atr_Expr e1, Atr_Expr e2, AST op)
	{
		Atr_Expr result = new Atr_Expr();
		
		if (!e1.getRval() || !e2.getRval())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, UNA DE LAS EXPRESIONES EN LA EXPR_BIN_LOG CON OPERADOR " + op.getText() + " NO TIENE RVAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}

		if (e1.getTipo().getType() != BOOLEAN || e2.getTipo().getType() != BOOLEAN)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, UNA DE LAS EXPRESIONES EN LA EXPR_BIN_LOG CON OPERADOR " + op.getText() + " NO ES DE TIPO BOOLEAN",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
			
		result.setTipo(e1.getTipo());
		result.setLval(false);
		result.setRval(true);	
		
		return result;	
	}	
	
	//An.lisis sem.ntico de las expresiones binarias relacionales
	//Comprueba que e1 y e2 tengan rval
	//Comprueba que e1 y e2 sean de tipo integer, real, char o string
	//Comprueba que e1 y e2 sean del mismo tipo
	Atr_Expr AS_Exp_Bin_Rel(Atr_Expr e1, Atr_Expr e2, AST op)
	{
		Atr_Expr result = new Atr_Expr();
		
		if (!e1.getRval() || !e2.getRval())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, UNA DE LAS EXPRESIONES EN LA EXPR_BIN_REL CON OPERADOR " + op.getText() + " NO TIENE RVAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}

		if (!e1.getTipo().equals(e2.getTipo()))
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LAS EXPRESIONES EN LA EXPR_BIN_REL CON OPERADOR " + op.getText() + " NO TIENEN IGUAL TIPO",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}												
		
		if (e1.getTipo().getType() != INTEGER && e1.getTipo().getType() != REAL && e1.getTipo().getType() != CHAR && e1.getTipo().getType() != STRING)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LAS EXPRESIONES EN LA EXPR_BIN_REL CON OPERADOR " + op.getText() + " NO TIENEN TIPO ADECUADO",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}		
		
		result.setTipo(#(#[BOOLEAN, "BOOLEAN"]));
		result.setLval(false);
		result.setRval(true);	
		
		return result;	
	}		
	
	//An.lisis sem.ntico de las expresiones unarias aritm.ticas
	//Comprueba que e sea de tipo integer o real
	Atr_Expr AS_Exp_Una_Arit(Atr_Expr e, AST op)
	{
		Atr_Expr result = new Atr_Expr();
		
		if (!e.getRval())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA EXPRESI.N EN LA EXPR_UNA_ARIT CON OPERADOR " + op.getText() + " NO TIENE RVAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}			

		if (e.getTipo().getType() != INTEGER && e.getTipo().getType() != REAL)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA EXPRESI.N EN LA EXPR_UNA_ARIT CON OPERADOR " + op.getText() + " NO ES DE TIPO INTEGER NI REAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}		
			
		result.setTipo(e.getTipo());
		result.setLval(false);
		result.setRval(true);	
		
		return result;	
	}		
	
	//An.lisis sem.ntico de la expresi.n unaria l.gica
	//Comprueba que e sea de tipo boolean
	Atr_Expr AS_Exp_Una_Log(Atr_Expr e, AST op)
	{
		Atr_Expr result = new Atr_Expr();
		
		if (!e.getRval())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA EXPRESI.N EN LA EXPR_UNA_LOG CON OPERADOR " + op.getText() + " NO TIENE RVAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}						

		if (e.getTipo().getType() != BOOLEAN)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA EXPRESI.N EN LA EXPR_UNA_LOG CON OPERADOR " + op.getText() + " NO ES DE TIPO BOOLEAN",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}		
			
		result.setTipo(e.getTipo());
		result.setLval(false);
		result.setRval(true);	
		
		return result;	
	}	
	
	//An.lisis sem.ntico de los registros
	//Comprueba que no haya varios campos con el mismo nombre
	void AS_Registro(LinkedList nombresCampos)
	{
		Set set = new HashSet();
		set.addAll(nombresCampos);
		
		if (nombresCampos.size() != set.size())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, HAY CAMPOS CON IGUAL NOMBRE EN UN MISMO REGISTRO",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}	
	}
	
	//An.lisis sem.ntico de los extremos de los rangos enteros
	//Comprueba que ambas sean de tipo entero
	void AS_Rango_Entero(Atr_Expr limite_inferior, Atr_Expr limite_superior)
	{
		if (limite_inferior.getTipo().getType() != INTEGER)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA EXPRESI.N EN EL EXTREMO INFERIOR DEL RANGO NO ES DE TIPO INTEGER",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
		
		if (limite_superior.getTipo().getType() != INTEGER)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA EXPRESI.N EN EL EXTREMO SUPERIOR DEL RANGO NO ES DE TIPO INTEGER",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}		
	}
}
	
programa: #(PROGRAMA cabecera declaraciones cuerpo)
	;
	
cabecera: IDENT;

declaraciones: #(DECLARACIONES dec_constantes dec_registros dec_variables dec_rutinas)
	;
	
dec_constantes: #(CONST (dec_constante)+)
	|
	;
	
dec_constante {Atr_Expr e1;}: #(CONSTANTE IDENT e1=expresion)
	;
	
dec_registros: #(TYPE (dec_registro)+)
	|
	;
	
dec_registro {String s; LinkedList l = new LinkedList();}: #(REGISTRO IDENT (s=dec_campo {l.add(s);})+) {AS_Registro(l);}
	;
	
dec_campo returns [String s = null]: #(CAMPO i:IDENT {s = i.getText();} tipo)
	;
	
dec_variables: #(VAR (dec_variable)+)
	|
	;
	
dec_variable: #(VARIABLE IDENT tipo)
	;
	
dec_rutinas: #(RUTINAS (dec_rutina)+)
	|
	;
	
dec_rutina: dec_procedimiento
	| dec_funcion
	;
	
dec_parametros: #(PARAMETROS (dec_parametro)*)
	;
	
dec_parametro: #(PARAMETRO IDENT tipo)
	;
	
dec_procedimiento: #(PROCEDIMIENTO cabecera_procedimiento dec_const_var cuerpo)
	;
	
cabecera_procedimiento: #(CABECERA IDENT dec_parametros VACIO)
	;
	
dec_funcion: #(FUNCION cabecera_funcion dec_const_var cuerpo)
	;
	
cabecera_funcion: #(CABECERA IDENT dec_parametros tipo)
	;
	
dec_const_var: #(DECLARACIONES dec_constantes dec_variables)
	;
	
cuerpo: #(CUERPO (instruccion)*)
	;
	
bloque: instruccion_simple
	| cuerpo
	;
	
instruccion: instruccion_simple
	| instruccion_compuesta
	;
	
instruccion_simple: asignacion
	| llamada_rutina
	| salida
	| entrada
	;
	
instruccion_compuesta: condicional
	| iteracion
	| iteracion_acotada
	;
	
asignacion {Atr_Expr e1, e2;}: #(ASIGNACION e1=expresion e2=expresion)
	{AS_Asignacion(e1, e2);}
	;

llamada_rutina {Atr_Expr e1, res; LinkedList l;}: #(LLAMADA e1=acceso l=lista_expresiones)
	{
		res = AS_Llamada(e1,l);
		
		if (res.getTipo().getType() != VACIO)
		{
			JOptionPane.showMessageDialog(
				jContentPanel,
				"ERROR, LA LLAMADA FUERA DE UNA EXPRESI.N CORRESPONDE A UN M.TODO FUNCIONAL",
				"Error",
				JOptionPane.INFORMATION_MESSAGE);
	 		System.exit(1);
		}	
	}
	;
	
salida {LinkedList l;}: #(WRITE l=lista_expresiones)
	{AS_Salida(l);}
	;
	
entrada {Atr_Expr e1;}: #(READ e1=expresion)
	{AS_Entrada(e1);}
	;
	
condicional {Atr_Expr e1;}: #(IF e1=condicion #(BLOQUE_IF bloque) (#(BLOQUE_ELSE bloque))?)
	{AS_Condicion(e1);}
	;
	
iteracion {Atr_Expr e1;}: #(WHILE e1=condicion bloque)
	{AS_Condicion(e1);}
	;
	
iteracion_acotada: #(FOR cabecera_for #(BLOQUE_FOR bloque))
	;
	
cabecera_for {Atr_Expr a, e1, e2;}: #(CABECERA_FOR a=acceso e1=expresion (TO | DOWNTO) e2=expresion)
	{AS_Cabecera_For(a, e1, e2);}
	;

lista_expresiones returns [LinkedList l = new LinkedList()] {Atr_Expr e1;}: #(EXPRESIONES (e1=expresion {l.add(e1);})+)
	|
	;
	
condicion returns [Atr_Expr res = null]: #(CONDICION res=expresion)
	;

expresion returns [Atr_Expr res = null] {Atr_Expr e1, e2; LinkedList l;}: 
	  #(op1:OR e1=expresion e2=expresion) 											{res = AS_Exp_Bin_Log(e1,e2,op1);}
	| #(op2:AND e1=expresion e2=expresion)											{res = AS_Exp_Bin_Log(e1,e2,op2);}
	| #(op3:NOT e1=expresion)														{res = AS_Exp_Una_Log(e1,op3);}
	| #(op4:MAYOR e1=expresion e2=expresion)										{res = AS_Exp_Bin_Rel(e1,e2,op4);}
	| #(op5:MAYOR_IGUAL e1=expresion e2=expresion)									{res = AS_Exp_Bin_Rel(e1,e2,op5);}
	| #(op6:MENOR e1=expresion e2=expresion)										{res = AS_Exp_Bin_Rel(e1,e2,op6);}
	| #(op7:MENOR_IGUAL e1=expresion e2=expresion)									{res = AS_Exp_Bin_Rel(e1,e2,op7);}
	| #(op8:IGUAL e1=expresion e2=expresion)										{res = AS_Exp_Bin_Rel(e1,e2,op8);}
	| #(op9:DISTINTO e1=expresion e2=expresion)										{res = AS_Exp_Bin_Rel(e1,e2,op9);}
	| #(op10:MAS e1=expresion e2=expresion)											{res = AS_Exp_Bin_Arit(e1,e2,op10);}
	| (#(MENOS expresion expresion)) => #(op11:MENOS e1=expresion e2=expresion)		{res = AS_Exp_Bin_Arit(e1,e2,op11);}
	| #(op12:MENOS e1=expresion)													{res = AS_Exp_Una_Arit(e1,op12);}
	| #(op13:POR e1=expresion e2=expresion)											{res = AS_Exp_Bin_Arit(e1,e2,op13);}
	| #(op14:DIVISION_ENTERA e1=expresion e2=expresion)								{res = AS_Exp_Bin_Arit(e1,e2,op14);}
	| #(op15:DIVISION_REAL e1=expresion e2=expresion)								{res = AS_Exp_Bin_Arit(e1,e2,op15);}
	| #(op16:MOD e1=expresion e2=expresion)											{res = AS_Exp_Bin_Arit(e1,e2,op16);}
	| #(LLAMADA e1=acceso l=lista_expresiones)										
		{
			res = AS_Llamada(e1,l); //hay que comprobar que sea un m.todo funcional
		
			if (res.getTipo().getType() == VACIO)
			{
 				JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA LLAMADA DENTRO DE UNA EXPRESI.N NO CORRESPONDE A UN M.TODO FUNCIONAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 		System.exit(1);
			}																					
		}
	| e1=acceso																		{res = e1;}
	| i:LIT_ENTERO																	{res = AS_Literal(i);}
	| j:LIT_REAL																	{res = AS_Literal(j);}
	| k:LIT_CAR																		{res = AS_Literal(k);}
	| m:CADENA																		{res = AS_Literal(m);}
	| n:TRUE																		{res = AS_Literal(n);}
	| o:FALSE																		{res = AS_Literal(o);}
	;

acceso returns [Atr_Expr res = new Atr_Expr()]: res=acceso_registro
	| res=acceso_array_registro
	| res=acceso_array
	| res=acceso_simple
	;
	
acceso_registro returns [Atr_Expr res = new Atr_Expr()] {Atr_Expr e;}: #(ACCESO_REGISTRO e=acceso_simple a:acceso_ext)
	{res = AS_Acceso_Registro(e,a);}	
	;

acceso_array_registro returns [Atr_Expr res = new Atr_Expr()] {Atr_Expr e;}: #(ACCESO_ARRAY_REGISTRO e=acceso_simple i:indices a:acceso_ext)
	{res = AS_Acceso_Array_Registro(e, i, a);}
	;
	
acceso_array returns [Atr_Expr res = new Atr_Expr()] {Atr_Expr e; LinkedList l;}: #(ACCESO_ARRAY e=acceso_simple i:indices)
	{res = AS_Acceso_Array(e,i);}
	;
	
acceso_simple returns [Atr_Expr res = new Atr_Expr()]: #(ACCESO_SIMPLE IDENT d:declaracion_acceso) {res = AS_Acceso_Simple(d);}
	;
	
declaracion_acceso:
	  dec_variable_resultado_1
	| dec_variable_1
	| dec_registro_1
	| dec_constante_1
	| dec_procedimiento_1
	| dec_funcion_1
	| dec_parametro_1
	;
	
acceso_ext: acceso_registro_ext
	| acceso_array_registro_ext
	| acceso_array_ext
	| acceso_simple_ext
	;
	
acceso_registro_ext: #(ACCESO_REGISTRO IDENT acceso_ext)
	;	
	
acceso_array_registro_ext: #(ACCESO_ARRAY_REGISTRO IDENT indices acceso_ext)
	;
	
acceso_array_ext: #(ACCESO_ARRAY IDENT indices)
	;
	
acceso_simple_ext: IDENT
	;
	
indices {Atr_Expr e;}: #(INDICES (e=expresion)+)
	;
	
tipo: simple
	| definido
	| compuesto
	| otro 
			{
 				JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, TIPO INV.LIDO",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 		System.exit(1);
			}
	;
	
otro: dec_funcion_1
	| dec_procedimiento_1
	| dec_variable_1
	| dec_constante_1
	;
	
tipo_array: simple
	| definido
	;
	
simple: INTEGER
	| REAL
	| BOOLEAN
	| CHAR
	| tipo_rango
	;
	
definido: dec_registro_1
	;
	
compuesto: array
	| string
	;
	
array: #(ARRAY dimensiones_array tipo_array)
	;
	
dimensiones_array: #(DIMENSIONES (dimension)+)
	;
	
dimension {Atr_Expr e;}: rango_entero
	| e=expresion
	;
	
string: #(STRING LIT_ENTERO)
	;
	
tipo_rango: (#(RANGO LIT_CAR)) => rango_caracter
	| rango_entero
	;
	
rango_caracter: #(RANGO LIT_CAR LIT_CAR)
	;
	
rango_entero {Atr_Expr e1, e2;}: #(RANGO e1=expresion e2=expresion) {AS_Rango_Entero(e1, e2);}
	;	

//rango_entero: #(RANGO extremo_rango extremo_rango)
	//;
	
//extremo_rango: #(MENOS LIT_ENTERO)
	//| #(MAS LIT_ENTERO)
	//;
	
//DECLARACI.N DE CLASES EN ACCESOS SIMPLES E IDENTIFICADORES DE TIPO.
//NO ES NECESARIO EL AN.LISIS SEM.NTICO EN ESTOS .RBOLES.

dec_constantes_1: #(CONST (dec_constante_1)+)
	|
	;

dec_constante_1: #(CONSTANTE IDENT expresion_1)
	;
	
dec_registro_1: #(REGISTRO IDENT (dec_campo_1)+)
	;
	
dec_campo_1: #(CAMPO IDENT tipo_1)
	;
	
dec_variables_1: #(VAR (dec_variable_1)+)
	|
	;
	
dec_variable_1: #(VARIABLE IDENT tipo_1)
	;
	
dec_variable_resultado_1: #(VARIABLE_RESULTADO IDENT tipo_1)
	;
	
dec_rutina_1: dec_procedimiento_1
	| dec_funcion_1
	;
	
dec_parametros_1: #(PARAMETROS (dec_parametro_1)*)
	;
	
dec_parametro_1: #(PARAMETRO IDENT tipo_1)
	;
	
dec_procedimiento_1: #(PROCEDIMIENTO cabecera_procedimiento_1 dec_const_var_1 cuerpo_1)
	;
	
cabecera_procedimiento_1: #(CABECERA IDENT dec_parametros_1)
	;
	
dec_funcion_1: #(FUNCION cabecera_funcion_1 dec_const_var_1 cuerpo_1)
	;
	
cabecera_funcion_1: #(CABECERA IDENT dec_parametros_1 tipo_1)
	;
	
dec_const_var_1: #(DECLARACIONES dec_constantes_1 dec_variables_1)
	;
	
cuerpo_1: #(CUERPO (instruccion_1)*)
	;
	
bloque_1: instruccion_simple_1
	| cuerpo_1
	;
	
instruccion_1: instruccion_simple_1
	| instruccion_compuesta_1
	;
	
instruccion_simple_1: asignacion_1
	| llamada_rutina_1
	| salida_1
	| entrada_1
	;
	
instruccion_compuesta_1: condicional_1
	| iteracion_1
	| iteracion_acotada_1
	;
	
asignacion_1: #(ASIGNACION expresion_1 expresion_1)
	;

llamada_rutina_1: #(LLAMADA acceso_1 lista_expresiones_1)
	;
	
salida_1: #(WRITE lista_expresiones_1)
	;
	
entrada_1: #(READ expresion_1)
	;
	
condicional_1: #(IF condicion_1 #(BLOQUE_IF bloque_1) #(BLOQUE_ELSE bloque_1))
	;
	
iteracion_1: #(WHILE condicion_1 bloque_1)
	;
	
iteracion_acotada_1: #(FOR cabecera_for_1 #(BLOQUE_FOR bloque_1))
	;
	
cabecera_for_1: #(CABECERA_FOR acceso_1 expresion_1 (TO | DOWNTO) expresion_1)
	;

lista_expresiones_1: #(EXPRESIONES (expresion_1)+)
	|
	;
	
condicion_1: #(CONDICION expresion_1)
	;

expresion_1: 
	  #(OR expresion_1 expresion_1) 											
	| #(AND expresion_1 expresion_1)											
	| #(NOT expresion_1)														
	| #(MAYOR expresion_1 expresion_1)										
	| #(MAYOR_IGUAL expresion_1 expresion_1)									
	| #(MENOR expresion_1 expresion_1)										
	| #(MENOR_IGUAL expresion_1 expresion_1)									
	| #(MAS expresion_1 expresion_1)											
	| (#(MENOS expresion_1 expresion_1)) => #(MENOS expresion_1 expresion_1)		
	| #(MENOS expresion_1)														
	| #(POR expresion_1 expresion_1)											
	| #(DIVISION_ENTERA expresion_1 expresion_1)								
	| #(DIVISION_REAL expresion_1 expresion_1)								
	| #(MOD expresion_1 expresion_1)											
	| #(LLAMADA acceso_1 lista_expresiones_1)									
	| acceso_1																	
	| LIT_ENTERO																
	| LIT_REAL																
	| LIT_CAR																	
	| CADENA																	
	| TRUE																
	| FALSE						
	;
	
acceso_1: acceso_registro_1
	| acceso_array_registro_1
	| acceso_array_1
	| acceso_simple_1
	;
	
acceso_registro_1: #(ACCESO_REGISTRO acceso_simple_1 acceso_ext_1)	
	;

acceso_array_registro_1: #(ACCESO_ARRAY_REGISTRO acceso_simple_1 indices_1 acceso_ext_1)
	;
	
acceso_array_1: #(ACCESO_ARRAY acceso_simple_1 indices_1)
	;
	
acceso_simple_1: #(ACCESO_SIMPLE IDENT declaracion_acceso_1)
	;

declaracion_acceso_1: 
	  dec_variable_resultado_1
	| dec_variable_1
	| dec_registro_1
	| dec_constante_1
	| dec_procedimiento_1
	| dec_funcion_1
	| dec_parametro_1
	;
	
acceso_ext_1: acceso_registro_ext_1
	| acceso_array_registro_ext_1
	| acceso_array_ext_1
	| acceso_simple_ext_1
	;
	
acceso_registro_ext_1: #(ACCESO_REGISTRO IDENT acceso_ext_1)
	;	
	
acceso_array_registro_ext_1: #(ACCESO_ARRAY_REGISTRO IDENT indices_1 acceso_ext_1)
	;
	
acceso_array_ext_1: #(ACCESO_ARRAY IDENT indices_1)
	;
	
acceso_simple_ext_1: IDENT
	;
	
indices_1: #(INDICES (expresion_1)+)
	;
	
tipo_1: simple_1
	| definido_1
	| compuesto_1
	| otro_1
	;
	
otro_1: dec_funcion_1
	| dec_procedimiento_1
	| dec_constante_1
	| dec_variable_1
	;
	
tipo_array_1: simple_1
	| definido_1
	;
	
simple_1: INTEGER
	| REAL
	| BOOLEAN
	| CHAR
	| tipo_rango_1
	;
	
definido_1: dec_registro_1
	;
	
compuesto_1: array_1
	| string_1
	;
	
array_1: #(ARRAY dimensiones_array_1 tipo_array_1)
	;
	
dimensiones_array_1: #(DIMENSIONES (dimension_1)+)
	;
	
dimension_1: rango_entero_1
	| expresion_1
	;
	
string_1: #(STRING (LIT_ENTERO)?)
	;
	
tipo_rango_1: (#(RANGO LIT_CAR)) => rango_caracter_1
	| rango_entero_1
	;
	
rango_caracter_1: #(RANGO LIT_CAR LIT_CAR)
	;
	
rango_entero_1: #(RANGO expresion_1 expresion_1)
	;
	
//rango_entero_1: #(RANGO extremo_rango_1 extremo_rango_1)
	//;
	
//extremo_rango_1: #(MENOS LIT_ENTERO)
	//| #(MAS LIT_ENTERO)
	//;
	