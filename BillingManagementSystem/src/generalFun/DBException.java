package generalFun;
public class DBException extends RuntimeException {
	private static final long serialVersionUID = -6594680435648999981L;
	public DBException(String message, Throwable cause) {
        super(message, cause);
    }
    public DBException(String message) { 
    	super(message); 
    }
}

