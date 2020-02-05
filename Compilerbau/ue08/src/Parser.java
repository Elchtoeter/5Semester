

import java.util.ArrayList;
import java.util.HashMap;



public class Parser {
	public static final int _EOF = 0;
	public static final int _int = 1;
	public static final int _float = 2;
	public static final int _ident = 3;
	public static final int maxT = 11;

	static final boolean _T = true;
	static final boolean _x = false;
	static final int minErrDist = 2;

	public Token t;    // last recognized token
	public Token la;   // lookahead token
	int errDist = minErrDist;
	
	public Scanner scanner;
	public Errors errors;

	abstract class Thing{
    public String name;
    public abstract float getPrice();
}

class AtomicThing extends Thing{
    private final float price;
    public AtomicThing(float price){
        this.price = price;
    }
    @Override
    public float getPrice(){
        return price;
    }
}

class ComplexThing extends Thing{
    private final ArrayList<Thing> things = new ArrayList<>();
    private float price = 0;
    public void addThing(Thing thing){
        things.add(thing);
    }
    @Override
    public float getPrice(){
        if (price == 0){
            for (Thing thing: things) {
                if (thing != null)
                    price += thing.getPrice();
            }
            price *= 1.2;
        }
        return price;
    }
}

HashMap<String,Thing> thingsMap = new HashMap<>();



	public Parser(Scanner scanner) {
		this.scanner = scanner;
		errors = new Errors();
	}

	void SynErr (int n) {
		if (errDist >= minErrDist) errors.SynErr(la.line, la.col, n);
		errDist = 0;
	}

	public void SemErr (String msg) {
		if (errDist >= minErrDist) errors.SemErr(t.line, t.col, msg);
		errDist = 0;
	}
	
	void Get () {
		for (;;) {
			t = la;
			la = scanner.Scan();
			if (la.kind <= maxT) {
				++errDist;
				break;
			}

			la = t;
		}
	}
	
	void Expect (int n) {
		if (la.kind==n) Get(); else { SynErr(n); }
	}
	
	boolean StartOf (int s) {
		return set[s][la.kind];
	}
	
	void ExpectWeak (int n, int follow) {
		if (la.kind == n) Get();
		else {
			SynErr(n);
			while (!StartOf(follow)) Get();
		}
	}
	
	boolean WeakSeparator (int n, int syFol, int repFol) {
		int kind = la.kind;
		if (kind == n) { Get(); return true; }
		else if (StartOf(repFol)) return false;
		else {
			SynErr(n);
			while (!(set[syFol][kind] || set[repFol][kind] || set[0][kind])) {
				Get();
				kind = la.kind;
			}
			return StartOf(syFol);
		}
	}
	
	void Recipes() {
		if (la.kind == 3 || la.kind == 8) {
			Thing thing = Object();
			if (thing != null)thingsMap.put(thing.name,thing);
		} else if (la.kind == 10) {
			Price();
		} else SynErr(12);
		while (la.kind == 3 || la.kind == 8 || la.kind == 10) {
			if (la.kind == 3 || la.kind == 8) {
				Thing thing = Object();
				if (thing != null)thingsMap.put(thing.name,thing);
			} else {
				Price();
			}
		}
	}

	Thing  Object() {
		Thing  thing;
		thing = null;
		if (la.kind == 8) {
			thing = Atom();
		} else if (la.kind == 3) {
			thing = Complex();
		} else SynErr(13);
		return thing;
	}

	void Price() {
		Expect(10);
		Expect(3);
		String name = t.val;
		Thing thing = thingsMap.get(name);
		if (thing==null){
		SemErr("Object "+name+" not found!");
		} else {
		System.out.println(name+": "+thing.getPrice());
		}
	}

	Thing  Atom() {
		Thing  at;
		Expect(8);
		Expect(3);
		String name = t.val; 
		Expect(9);
		Expect(2);
		at = new AtomicThing(Float.parseFloat(t.val)); at.name = name;
		return at;
	}

	Thing  Complex() {
		Thing  outThing;
		Expect(3);
		outThing = null;
		ComplexThing ct = new ComplexThing();
		int errorcount = 0;
		ct.name = t.val;
		Expect(4);
		Thing thing = Recipe();
		int times = 1;
		if (la.kind == 5) {
			Get();
			Expect(1);
			times = Integer.parseInt(t.val);
		}
		if (thing != null){
		 for(int i = 0; i< times;i++){
		     ct.addThing(thing);}
		} else errorcount++;
		while (la.kind == 6) {
			Get();
			thing = Recipe();
			times = 1;
			if (la.kind == 5) {
				Get();
				Expect(1);
				times = Integer.parseInt(t.val);
			}
			if (thing != null){
			 for(int i = 0; i< times;i++){
			     ct.addThing(thing);}
			} else errorcount++;
		}
		Expect(7);
		if (errorcount==0)
		outThing = ct;
		return outThing;
	}

	Thing  Recipe() {
		Thing  thing;
		Expect(3);
		thing = thingsMap.get(t.val);
		if (thing == null)SemErr("no such recipe");
		return thing;
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		Recipes();
		Expect(0);

	}

	private static final boolean[][] set = {
		{_T,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x}

	};
} // end Parser


class Errors {
	public int count = 0;                                    // number of errors detected
	public java.io.PrintStream errorStream = System.out;     // error messages go to this stream
	public String errMsgFormat = "-- line {0} col {1}: {2}"; // 0=line, 1=column, 2=text
	
	protected void printMsg(int line, int column, String msg) {
		StringBuffer b = new StringBuffer(errMsgFormat);
		int pos = b.indexOf("{0}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, line); }
		pos = b.indexOf("{1}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, column); }
		pos = b.indexOf("{2}");
		if (pos >= 0) b.replace(pos, pos+3, msg);
		errorStream.println(b.toString());
	}
	
	public void SynErr (int line, int col, int n) {
		String s;
		switch (n) {
			case 0: s = "EOF expected"; break;
			case 1: s = "int expected"; break;
			case 2: s = "float expected"; break;
			case 3: s = "ident expected"; break;
			case 4: s = "\"{\" expected"; break;
			case 5: s = "\"x\" expected"; break;
			case 6: s = "\",\" expected"; break;
			case 7: s = "\"}\" expected"; break;
			case 8: s = "\"atom\" expected"; break;
			case 9: s = "\":\" expected"; break;
			case 10: s = "\"priceof\" expected"; break;
			case 11: s = "??? expected"; break;
			case 12: s = "invalid Recipes"; break;
			case 13: s = "invalid Object"; break;
			default: s = "error " + n; break;
		}
		printMsg(line, col, s);
		count++;
	}

	public void SemErr (int line, int col, String s) {	
		printMsg(line, col, s);
		count++;
	}
	
	public void SemErr (String s) {
		errorStream.println(s);
		count++;
	}
	
	public void Warning (int line, int col, String s) {	
		printMsg(line, col, s);
	}
	
	public void Warning (String s) {
		errorStream.println(s);
	}
} // Errors


class FatalError extends RuntimeException {
	public static final long serialVersionUID = 1L;
	public FatalError(String s) { super(s); }
}
