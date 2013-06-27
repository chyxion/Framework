package com.shs.framework.upload;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

/**
 * MultipartRequest.
 */
@SuppressWarnings("unchecked")
public class MultipartRequest extends HttpServletRequestWrapper {
	private static String saveDirectory;
	private static int maxPostSize;
	private static String encoding;
	private static boolean isMultipartSupported = false;
	private static final DefaultFileRenamePolicy fileRenamePolicy = new DefaultFileRenamePolicy();
	
	private List<UploadFile> uploadFiles;
	private com.oreilly.servlet.MultipartRequest multipartRequest;
	
	static void init(String saveDirectory, int maxPostSize, String encoding) {
		MultipartRequest.saveDirectory = saveDirectory;
		MultipartRequest.maxPostSize = maxPostSize;
		MultipartRequest.encoding = encoding;
		MultipartRequest.isMultipartSupported = true;	// 在OreillyCos.java中保障了, 只要被初始化就一定为 true
	}
	
	public MultipartRequest(HttpServletRequest request, String saveDirectory, int maxPostSize, String encoding) {
		super(request);
		wrapMultipartRequest(request, saveDirectory, maxPostSize, encoding);
	}
	
	public MultipartRequest(HttpServletRequest request, String saveDirectory, int maxPostSize) {
		super(request);
		wrapMultipartRequest(request, saveDirectory, maxPostSize, encoding);
	}
	
	public MultipartRequest(HttpServletRequest request, String saveDirectory) {
		super(request);
		wrapMultipartRequest(request, saveDirectory, maxPostSize, encoding);
	}
	
	public MultipartRequest(HttpServletRequest request) {
		super(request);
		wrapMultipartRequest(request, saveDirectory, maxPostSize, encoding);
	}
	
	/**
	 * 添加对相对路径的支持
	 * 1: 以 "/" 开头或者以 "x:开头的目录被认为是绝对路径
	 * 2: 其它路径被认为是相对路径, 需要 JFinalConfig.uploadedFileSaveDirectory 结合
	 */
	private String handleSaveDirectory(String saveDirectory) {
		if (saveDirectory.startsWith("/") || saveDirectory.indexOf(":") == 1)
			return saveDirectory;
		else 
			return MultipartRequest.saveDirectory + saveDirectory;
	}
	
	private void wrapMultipartRequest(HttpServletRequest request, String saveDirectory, int maxPostSize, String encoding) {
		if (!isMultipartSupported)
			throw new RuntimeException("Oreilly cos.jar is not found, Multipart post can not be supported.");
		
		saveDirectory = handleSaveDirectory(saveDirectory);
		
		File dir = new File(saveDirectory);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new RuntimeException("Directory " + saveDirectory + " not exists and can not create directory.");
			}
		}
		
        uploadFiles = new LinkedList<UploadFile>();
		
		try {
			multipartRequest = new com.oreilly.servlet.MultipartRequest(request, saveDirectory, maxPostSize, encoding, fileRenamePolicy);
			Enumeration<String> fileNames = multipartRequest.getFileNames();
			while (fileNames.hasMoreElements()) {
				String name = fileNames.nextElement();
				String fsName = multipartRequest.getFilesystemName(name);
				
				// 文件没有上传则不生成 UploadFile, 这与 cos的解决方案不一样
				if (fsName != null) {
					UploadFile uploadFile = new UploadFile(name, 
							saveDirectory, 
							fsName, 
							multipartRequest.getOriginalFileName(name),
							multipartRequest.getContentType(name));
					if (isSafeFile(uploadFile))
						uploadFiles.add(uploadFile);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private boolean isSafeFile(UploadFile uploadFile) {
		if (uploadFile.getFileName().toLowerCase().endsWith(".jsp")) {
			uploadFile.getFile().delete();
			return false;
		}
		return true;
	}
	
	public List<UploadFile> getFiles() {
		return uploadFiles;
	}
	
	/**
	 * Methods to replace HttpServletRequest methods
	 */
	public Enumeration<String> getParameterNames() {
		return multipartRequest.getParameterNames();
	}
	
	public String getParameter(String name) {
		return multipartRequest.getParameter(name);
	}
	
	public String[] getParameterValues(String name) {
		return multipartRequest.getParameterValues(name);
	}
	
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> map = new HashMap<String, String[]>();
		Enumeration<String> enumm = getParameterNames();
		while (enumm.hasMoreElements()) {
			String name = enumm.nextElement();
			map.put(name, multipartRequest.getParameterValues(name));
		}
		return map;
	}
}






