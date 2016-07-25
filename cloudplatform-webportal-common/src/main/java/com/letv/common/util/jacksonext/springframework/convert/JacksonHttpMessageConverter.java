package com.letv.common.util.jacksonext.springframework.convert;

import java.io.IOException;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

import com.letv.common.util.jacksonext.helper.ThreadJacksonMixInHolder;

/**
 * Json格式化Converter
 * 
 * @author linzhanbo .
 * @since 2016年7月8日, 下午2:07:55 .
 * @version 1.0 .
 */
public class JacksonHttpMessageConverter extends
		MappingJacksonHttpMessageConverter {
	private ObjectMapper objectMapper = new ObjectMapper();
	private boolean prefixJson = false;

	@Override
	protected void writeInternal(Object object, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		// 判断是否需要重写objectMapper
		ObjectMapper objectMapper = this.objectMapper;// 本地化ObjectMapper，防止方法级别的ObjectMapper改变全局ObjectMapper
		if (ThreadJacksonMixInHolder.isContainsMixIn()) {
			objectMapper = ThreadJacksonMixInHolder.builderMapper();
		}

		JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders()
				.getContentType());
		JsonGenerator jsonGenerator = objectMapper.getJsonFactory()
				.createJsonGenerator(outputMessage.getBody(), encoding);

		if (objectMapper.getJsonFactory().isEnabled(
				JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS)) {
			jsonGenerator.useDefaultPrettyPrinter();
		}

		try {
			if (this.prefixJson) {
				jsonGenerator.writeRaw("{} && ");
			}
			objectMapper.writeValue(jsonGenerator, object);
		} catch (JsonProcessingException ex) {
			throw new HttpMessageNotWritableException("Could not write JSON: "
					+ ex.getMessage(), ex);
		}
	}

	public boolean isPrefixJson() {
		return prefixJson;
	}

	public void setPrefixJson(boolean prefixJson) {
		this.prefixJson = prefixJson;
	}

}
