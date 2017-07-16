package com.github.dbgso.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class StringUtils {

	public static String encode(byte[] data) {

		Parser parser = new AutoDetectParser();

		ContentHandler handler = new BodyContentHandler(-1);
		Metadata metadata = new Metadata();
		try {
			parser.parse(new ByteArrayInputStream(data), handler, metadata, new ParseContext());

			System.out.println(metadata);
			return handler.toString();
		} catch (IOException | SAXException | TikaException e) {
			e.printStackTrace();
		}

		return new String(data);
	}

}
