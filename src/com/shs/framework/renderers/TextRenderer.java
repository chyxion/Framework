package com.shs.framework.renderers;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;
import javax.servlet.ServletOutputStream;
/**
 * @version 0.1
 * @author chyxion
 * @describe: 文本渲染输出
 * @date created: Apr 9, 2013 11:00:01 AM
 * @support: chyxion@163.com
 * @date modified: 
 * @modified by: 
 * @copyright: Shenghang Soft All Right Reserved.
 */
public class TextRenderer extends Renderer {
	
	private static final long serialVersionUID = -5264892635310241831L;
	private static final String defaultContentType = "text/plain;charset=" + encoding;
	private String text;
	private String contentType;
	
	public TextRenderer(String text) {
		this.text = text;
	}
	
	public TextRenderer(String text, String contentType) {
		this.text = text;
		this.contentType = contentType;
	}
	
	public void render() {
		PrintWriter writer = null;
		try {
	        
	        if (contentType == null) {
	        	response.setContentType(defaultContentType);
	        }
	        else {
	        	response.setContentType(contentType);
				response.setCharacterEncoding(getEncoding());
	        }
	        
			// 如果浏览器支持gzip压缩，则进行gzip压缩输出
			String encoding = request.getHeader("Accept-Encoding");		
			if (encoding != null && encoding.toLowerCase().contains("gzip")) {  
			    ServletOutputStream sos = response.getOutputStream();
			    ByteArrayOutputStream baos = new ByteArrayOutputStream();
			    GZIPOutputStream gos = new GZIPOutputStream(baos);
				gos.write(text.getBytes(getEncoding()));
				gos.finish();
				gos.close();
				byte[] bytes = baos.toByteArray();
				response.addHeader("Content-Length", Integer.toString(bytes.length));
				response.addHeader("Content-Encoding", "gzip");
				sos.write(bytes);
				sos.flush();
				sos.close();
			} else {  //	
				writer = response.getWriter();
				writer.write(text);
				writer.flush();
			}	
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			if (writer != null)
				writer.close();
		}
	}
}




