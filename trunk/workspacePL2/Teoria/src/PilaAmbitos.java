import java.util.Stack;

import javax.swing.JPanel;

import antlr.CommonAST;
import antlr.collections.AST;
import antlr.debug.misc.ASTFrame;

public class PilaAmbitos extends Stack {

	public PilaAmbitos(){
		super();
	}
	public void apilarAmbito(Ambito a){
		this.push(a);
	}
	public void desapilarAmbito(){
		this.pop();
		}
	
	public Ambito ambitoActual(){
		if(this.isEmpty())	
			return null;
		return (Ambito)this.peek();
		
	}
	
	public Simbolo buscarSimbolo(String s){
		if(this.isEmpty())
			return null;
	
		Ambito amb=this.ambitoActual();
		Simbolo simbolo=amb.getDeclaracion(s);
		
		while(simbolo==null && !amb.getTipo().equals("PROGRAMA"))
		{
			amb=amb.getContenedor();
			simbolo=amb.getDeclaracion(s);
			
			
		}
		
		return simbolo;
	}
	
	public void muestraPila(){
		Ambito amb=this.ambitoActual();
		System.out.println("-------------------------");
		if(amb==null)
			System.out.println("la pila está vacia");
		while(amb!=null)
		{
			System.out.print(amb.getNombre());
			if(amb.getContenedor()!=null)
				System.out.println("  ---- padre ---> "+amb.getContenedor().getNombre());
			else
				System.out.println();
			amb=amb.getContenedor();
		}
		System.out.println("-------------------------");
	}

}
