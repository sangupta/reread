package com.sangupta.reread.utils;

import java.io.UnsupportedEncodingException;

import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sangupta.jerry.http.WebResponse;
import com.sangupta.jerry.util.AssertUtils;

/**
 * Utility class to parse response from the web using
 * the correct encoding.
 * 
 * @author sangupta
 *
 */
public class WebResponseParser {
	
	/**
	 * My logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(WebResponseParser.class);
	
	/**
	 * Read contents using the proper encoding specified in response header
	 * or by detecting one using the {@link UniversalDetector} from Mozilla.
	 * 
	 * @param webResponse
	 * @return
	 */
	public static String getContentWithProperEncoding(WebResponse webResponse) {
		byte[] bytes = webResponse.asBytes();
		if(AssertUtils.isEmpty(bytes)) {
			return null;
		}
		
		if(webResponse.getCharSet() != null) {
			return new String(bytes, webResponse.getCharSet());
		}
		
		// encoding not known - detect encoding
		UniversalDetector detector = new UniversalDetector(null);
		detector.handleData(bytes, 0, bytes.length);
		detector.dataEnd();
		
		String encoding = detector.getDetectedCharset();
		if(AssertUtils.isNotEmpty(encoding)) {
			try {
				return new String(bytes, encoding);
			} catch (UnsupportedEncodingException e) {
				LOGGER.error("Unable to read feed content in detected encoding of: {}", encoding);
			}
		}
		
		// return in platform default - nothing we can do
		return new String(bytes);
	}

}
