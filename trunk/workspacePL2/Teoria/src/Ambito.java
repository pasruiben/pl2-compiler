import java.util.*;

public class Ambito {

private String nombre;
private String tipo;
private TablaSimbolos declaracionesContenidas;
private Ambito contenedor;

public Ambito(String nom,String t,TablaSimbolos ts,Ambito cont){
	nombre=nom;
	tipo=t;
	declaracionesContenidas=ts;
	contenedor=cont;
}

public Ambito getContenedor() {
	return contenedor;
}

public void setContenedor(Ambito contenedor) {
	this.contenedor = contenedor;
}

public String getNombre() {
	return nombre;
}

public void setNombre(String nombre) {
	this.nombre = nombre;
}

public String getTipo() {
	return tipo;
}

public void setTipo(String tipo) {
	this.tipo = tipo;
}

public Simbolo getDeclaracion(String n){
	if(declaracionesContenidas==null)
		return null;
	return declaracionesContenidas.getSimbolo(n);
}

/*devuelve -1 si hay un símbolo con el mismo nombre en las declaraciones contenidas y 
0 si todo ha ido correctamente*/
public int setDeclaracion(Simbolo s){
	if(declaracionesContenidas==null)
		declaracionesContenidas=new TablaSimbolos();
	return declaracionesContenidas.setSimbolo(s);
}


public String toString(){
	String j=nombre+"\n";
	if(declaracionesContenidas!=null)
		j+=declaracionesContenidas.toString();
	return j;
}
}
