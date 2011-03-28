import java.util.*;

public class TablaSimbolos {
	private List listaSimbolos;
	
	public TablaSimbolos(){
		listaSimbolos=new LinkedList();
	}
	
	
	/*devuelve el simbolo si lo encuentra y null en caso contrario*/
	public Simbolo getSimbolo(String nombre){
		Iterator it=listaSimbolos.iterator();
		while(it.hasNext()){
			Simbolo s=(Simbolo)it.next();
			if(s.getNombre().equals(nombre))	{
			
				return s;
			}
		}
		return null;
	}
	
	/*devuelve -1 si hay un símbolo con el mismo nombre y 
	 0 si todo ha ido correctamente*/
	public int setSimbolo(Simbolo s){
		Iterator it=listaSimbolos.iterator();
		while(it.hasNext()){
			Simbolo simb=(Simbolo)it.next();
			if(simb.getNombre().equals(s.getNombre()))
				return -1;
		}
		listaSimbolos.add(s);
		return 0;
	}
	
	public String toString(){
		String t="";
		Iterator it=listaSimbolos.iterator();
		while(it.hasNext()){
			Simbolo s=(Simbolo)it.next();
			t=t+s.getDeclaracion().toStringTree()+"\n";
		}
		return t;
	}
}
