package com.github.dbgso.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.tika.Tika;
import org.apache.tika.detect.NameDetector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class StringUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(StringUtils.class);

	public static String encode(byte[] data) {

		ContentHandler handler = new BodyContentHandler(-1);
		Metadata metadata = new Metadata();

		try {
			String detect = new Tika().detect(data);

			if ("application/octet-stream".equals(detect)) {
				return nameDetectorEncode(data);
			}

			Parser parser = new AutoDetectParser();

			parser.parse(new ByteArrayInputStream(data), handler, metadata, new ParseContext());
			return handler.toString();
		} catch (IOException | SAXException | TikaException e) {
			LOGGER.warn(e.getMessage());
		}

		return new String(data);
	}

	private static String nameDetectorEncode(byte[] data) throws IOException, SAXException, TikaException {
		BodyContentHandler handler = new BodyContentHandler(-1);

		Metadata metadata = new Metadata();
		metadata.set(Metadata.RESOURCE_NAME_KEY, "*");

		Map<Pattern, MediaType> map = new HashMap<>();
		map.put(Pattern.compile(".*"), MediaType.TEXT_PLAIN);

		AutoDetectParser parser = new AutoDetectParser();
		parser.setDetector(new NameDetector(map));
		parser.parse(new ByteArrayInputStream(data), handler, metadata);
		return handler.toString();
	}

}
