package com.shs.framework.renderers;
import static com.shs.framework.core.Constant.DEFAULT_FILE_CONTENT_TYPE;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import javax.servlet.ServletContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.shs.framework.utils.PathUtils;

/**
 * FileRender.
 */
public class FileRenderer extends Renderer {
	
	private static final long serialVersionUID = 1L;
	
	private File file;
	private String name;
	private static String fileDownloadPath;
	private static ServletContext servletContext;
	private static String webRootPath;
	
	public FileRenderer(File file) {
		this.file = file;
		this.name = file.getName();
	}
	public FileRenderer(File file, String name) {
		this.file = file;
		this.name = name;
	}
	public FileRenderer(String filePath) {
		this(filePath, null);
	}
	public FileRenderer(String filePath, String name) {
		if (filePath.startsWith("/"))
			file = new File(webRootPath, filePath.substring(1));
		else
			file = new File(fileDownloadPath, filePath);
		
		if (StringUtils.isEmpty(name)) {
			this.name = file.getName();
		} else {
			this.name = name;
		}
	}
	static void init(String fileDownloadPath, ServletContext servletContext) {
		FileRenderer.fileDownloadPath = fileDownloadPath;
		FileRenderer.servletContext = servletContext;
		webRootPath = PathUtils.getWebRootPath();
	}
	
	public void render() {
		
		if (file == null || !file.isFile() || file.length() > Integer.MAX_VALUE) {
			RendererFactory.me().getError404Renderer().setContext(request, response).render();
			return ;
        }
		
        String contentType = servletContext.getMimeType(name);
        if (contentType == null) {
        	contentType = DEFAULT_FILE_CONTENT_TYPE;		// "application/octet-stream";
        }
        
        response.setContentType(contentType);
        response.setContentLength((int)file.length());
        OutputStream outputStream = null;
        try {
        	response.addHeader("Content-Disposition", 
        			"attachment;filename=" + URLEncoder.encode(name, "UTF-8"));
            outputStream = response.getOutputStream();
            FileUtils.copyFile(file, outputStream);
            outputStream.flush();
        }
        catch (Exception e) {
        	throw new RuntimeException(e);
        }
        finally {
            if (outputStream != null) {
            	try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
	}
}


