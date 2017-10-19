package org.gms.bogavante.connector.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpHeader {

	public static final int MAX_LENGTH_LINE = 32768;
	public static final int INITIAL_LENGTH_LINE = 1204;
	public static final String OWS = "[ \t]";
	
	private String header_name;
	private String header_value;
//	private boolean isObs_fold;
//	
//	public boolean isObs_fold() {
//		return isObs_fold;
//	}
//
//	public void setObs_fold(boolean isObs_fold) {
//		this.isObs_fold = isObs_fold;
//	}

	public String getHeader_name() {
		return header_name;
	}

	public String getHeader_value() {
		return header_value;
	}

	/*
	 * RFC 7230 apartado 3.2 Header Fields
	 * header-field = field-name ":" OWS field-value OWS
	 * OWS          = *( SP / HTAB ) ; optional whitespace
	 */
	public void parseLine(char[] buf, int length){
		String header_field = new String(buf,0,length);
		/*
		 * RFC 7230 apartado 3.2.4-p2
		 * Un servidor debe rechazar cualquier mensaje de petición
		 * que contenga espacios-en-blanco entre en nombre-campo y 
		 * ":".
		 */
		Pattern pattern = Pattern.compile("\\s+:");
		Matcher matcher = pattern.matcher(header_field);
		if(matcher.find())
			throw new HttpRequestParseException(400, "Bad Request");
		
		String[] components = header_field.split(":",2);
		header_name = components[0];
		/*
		 * RFC 7230 apartado 3.2.4-p3
		 * OWS occurring before the first non-whitespace octet of the field 
		 * value or after the last non-whitespace octet of the field value 
		 * ought to be excluded by parsers when extracting the field value 
		 * from a header field.
		 */
		StringBuffer pat = new StringBuffer();
		pat.append("^").append(OWS).append("*|").append(OWS).append("*$");
		pattern = Pattern.compile(pat.toString());
		matcher = pattern.matcher(components[1]);
		header_value = matcher.replaceAll("");
	}
}
