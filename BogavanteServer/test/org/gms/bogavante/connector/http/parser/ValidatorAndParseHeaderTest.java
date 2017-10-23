package org.gms.bogavante.connector.http.parser;

import static org.junit.Assert.*;

import org.gms.bogavante.connector.http.HttpRequestParseException;
import org.gms.bogavante.connector.http.header.parser.Http1ValidatorAndParseHeader;

import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

public class ValidatorAndParseHeaderTest {

	@Test
	public void testParseCommaDelimitedList() {
		String field_value = "foo,bar";
		
		String[] values = Http1ValidatorAndParseHeader.parseCommaDelimitedList(field_value, false);
		assertThat("foo,bar",values, is(new String[]{"foo","bar"}));
		
		field_value = "foo ,bar,";
		values = Http1ValidatorAndParseHeader.parseCommaDelimitedList(field_value, false);
		assertThat("foo ,bar,",values, is(new String[]{"foo","bar"}));
		
		field_value = "foo , ,bar,charlie  ";
		values = Http1ValidatorAndParseHeader.parseCommaDelimitedList(field_value, false);
		assertThat("foo , ,bar,charlie  ",values, is(new String[]{"foo","bar","charlie"}));
		
		field_value = "";
		try{
			values = Http1ValidatorAndParseHeader.parseCommaDelimitedList(field_value, true);
		}catch(HttpRequestParseException e){
			assertThat("(vacio)",e.getCodeError(), is(400));
		}
		
		field_value = ",";
		try{
			values = Http1ValidatorAndParseHeader.parseCommaDelimitedList(field_value, true);
		}catch(HttpRequestParseException e){
			assertThat(",",e.getCodeError(), is(400));
		}
		
		field_value = ", ,";
		try{
			values = Http1ValidatorAndParseHeader.parseCommaDelimitedList(field_value, true);
		}catch(HttpRequestParseException e){
			assertThat(", ,",e.getCodeError(), is(400));
		}
	}

}
