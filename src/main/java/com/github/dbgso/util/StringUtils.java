package com.github.dbgso.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.tika.detect.EncodingDetector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.txt.UniversalEncodingDetector;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

public class StringUtils {

	public static String encode(byte[] data) {

		Parser parser = new AutoDetectParser();

		ContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		try {
			parser.parse(new ByteInputStream(data, data.length), handler, metadata, new ParseContext());

			System.out.println(metadata);
			return handler.toString();
		} catch (IOException | SAXException | TikaException e) {
			e.printStackTrace();
		}

		return new String(data);
	}

}
