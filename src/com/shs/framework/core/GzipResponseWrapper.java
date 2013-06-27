package com.shs.framework.core;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
/**
 * @version 0.1
 * @author chyxion
 * @describe: GZIP响应包装器
 * @date created: Apr 8, 2013 3:59:13 PM
 * @support: chyxion@163.com
 * @date modified: 
 * @modified by: 
 * @copyright: Shenghang Soft All Right Reserved.
 */
public class GzipResponseWrapper extends HttpServletResponseWrapper {
	protected HttpServletResponse response = null;
	protected ServletOutputStream outputStream = null;
	protected PrintWriter writer = null;

	public GzipResponseWrapper(HttpServletResponse response) {
		super(response);
		this.response = response;
	}
	private ServletOutputStream newOutputStream() {
		return new GzipResponseStream(response);
	}
	/**
	 * 响应完毕
	 */
	public void finish() {
		try {
			if (writer != null) {
				writer.close();
			} 
			if (outputStream != null) {
				outputStream.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void flushBuffer() throws IOException {
		outputStream.flush();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (writer != null) {
			throw new IllegalStateException("getWriter() 已经被调用！");
		}

		if (outputStream == null)
			outputStream = newOutputStream();
		return outputStream;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (writer != null) {
			return writer;
		}

		if (outputStream != null) {
			throw new IllegalStateException("getOutputStream() 已经被调用！");
		}

		outputStream = newOutputStream();
		writer = new PrintWriter(new OutputStreamWriter(outputStream));
		return writer;
	}

	private class GzipResponseStream extends ServletOutputStream {
		protected ByteArrayOutputStream byteArrayOutputStream;
		protected GZIPOutputStream gzipOutputStream;
		protected boolean closed = false;
		protected HttpServletResponse response;
		protected ServletOutputStream outputStream;

		public GzipResponseStream(HttpServletResponse response) {
			byteArrayOutputStream = new ByteArrayOutputStream();
			this.response = response;
			try {
				this.outputStream = response.getOutputStream();
				gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void close() throws IOException {
			if (closed) {
				throw new IOException("输出流已经关闭！");
			}
			gzipOutputStream.finish();

			byte[] bytes = byteArrayOutputStream.toByteArray();
			response.addHeader("Content-Length", Integer.toString(bytes.length));
			response.addHeader("Content-Encoding", "gzip");
			outputStream.write(bytes);
			outputStream.flush();
			outputStream.close();
			closed = true;
		}

		@Override
		public void flush() throws IOException {
			if (closed) {
				throw new IOException("不能刷新已关闭的输出流！");
			}
			gzipOutputStream.flush();
		}

		@Override
		public void write(int b) throws IOException {
			if (closed) {
				throw new IOException("不能写入已关闭的输出流！");
			}
			gzipOutputStream.write((byte) b);
		}

		@Override
		public void write(byte b[]) throws IOException {
			write(b, 0, b.length);
		}
		@Override
		public void write(byte b[], int off, int len) throws IOException {
			if (closed) {
				throw new IOException("不能写入已关闭的输出流！");
			}
			gzipOutputStream.write(b, off, len);
		}
	}
}
