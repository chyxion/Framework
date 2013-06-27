package com.shs.framework.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.log4j.Logger;
import org.joor.Reflect;

public class ClassSearcher {
	
    protected static final Logger logger = Logger.getLogger(ClassSearcher.class);
    static URL classPath = ClassSearcher.class.getResource("/");
    static String lib = new File(classPath.getFile()).getParent() + "/lib/";

    /**
     * 递归查找文件
     * 
     * @param baseDirName
     *            查找的文件夹路径
     * @param targetFileName
     *            需要查找的文件名
     */
    private static List<String> findFiles(File baseDir, String targetFileName) {
        /**
         * 算法简述： 从某个给定的需查找的文件夹出发，搜索该文件夹的所有子文件夹及文件， 若为文件，则进行匹配，匹配成功则加入结果集，若为子文件夹，则进队列。 队列不空，重复上述操作，队列为空，程序结束，返回结果。
         */
        List<String> classFiles = new LinkedList<String>();
        // 判断目录是否存在
        if (!baseDir.exists() || !baseDir.isDirectory()) {
        	logger.error("class search error[" + baseDir.getName() + "]is not a dir！");
        } else {
            for (String fileName : baseDir.list()) {
                File file = new File(baseDir, fileName);
                if (file.isDirectory()) {
                    classFiles.addAll(findFiles(file, targetFileName));
                } else {
                    if (ClassSearcher.wildcardMatch(targetFileName, file.getName())) {
                        String fileFullName = file.getAbsoluteFile().toString().replaceAll("\\\\", "/");
                        String className;
                        className = fileFullName.substring(fileFullName.indexOf("/classes") + "/classes".length(), fileFullName.indexOf(".class"));
                        if (className.startsWith("/")) {
                            className = className.substring(className.indexOf("/") + 1);
                        }
                        classFiles.add(className(className));
                    }
                }
            }
        }
        return classFiles;
    }

    /**
     * 查找jar包中的class
     * 
     * @param baseDirName
     *            jar路径
     * @param jarNames
     * @param jarFileURL
     *            jar文件地址 
     */
    public static List<String> findJARFiles(File baseDir, final List<String> jarNames) {
        List<String> classFiles = new ArrayList<String>();
        try {
            // 判断目录是否存在
            if (!baseDir.exists() || !baseDir.isDirectory()) {
                logger.error("file serach error[" + baseDir.getName() + "]is not a dir！");
            } else {
                String[] jarFiles = baseDir.list(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return jarNames.contains(name);
                    }
                });
                for (String fileName : jarFiles) {
                    JarFile jarFile = new JarFile(new File(baseDir, fileName));
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String entryName = entry.getName();
                        if (!entry.isDirectory() && entryName.endsWith(".class")) {
                            String className = entryName.replaceAll("/", ".").substring(0, entryName.length() - 6);
                            classFiles.add(className);
                        }
                    }
                    jarFile.close();
				}
            }

        } catch (IOException e) {
            logger.error(e);
        }
        return classFiles;
    }

    private static List<String> findAllJARs(File baseDir) {
        /**
         * 算法简述： 从某个给定的需查找的文件夹出发，搜索该文件夹的所有子文件夹及文件， 若为文件，则进行匹配，匹配成功则加入结果集，若为子文件夹，则进队列。 队列不空，重复上述操作，队列为空，程序结束，返回结果。
         */
        List<String> classFiles = new LinkedList<String>();
        try {
        // 判断目录是否存在
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            logger.error("class search error[" + baseDir.getName() + "]is not a dir！");
        } else {
            for (String fileName : baseDir.list()) {
                File file = new File(baseDir, fileName);
                if (file.isDirectory()) {
                    classFiles.addAll(findAllJARs(file));
                } else {
                	if (fileName.endsWith(".jar")) {
	                    JarFile localJarFile = new JarFile(file);
	                    Enumeration<JarEntry> entries = localJarFile.entries();
	                    while (entries.hasMoreElements()) {
	                        JarEntry jarEntry = entries.nextElement();
	                        String entryName = jarEntry.getName();
	                        if (!jarEntry.isDirectory() && entryName.endsWith(".class")) {
	                            String className = entryName.replaceAll("/", ".").substring(0, entryName.length() - 6);
	                            classFiles.add(className);
	                        }
	                    }
	                    localJarFile.close();
                	}
                }
			}
        }
        } catch (Exception e) {
        	logger.error("scan jar classes error!!!");
        	logger.error(e);
		}
        return classFiles;
    }
    public static <T> List<Class<? extends T>> findAllJARs(Class<T> clazz) {
        return extraction(clazz, findAllJARs(new File(lib)));
    }
    public static <T> List<Class<? extends T>> findInClasspathAndJars(Class<T> clazz, List<String> jars) {
        List<String> classFileList = findFiles(new File(classPath.getFile()), "*.class");
        classFileList.addAll(findJARFiles(new File(lib), jars));
        return extraction(clazz, classFileList);
    }

    @SuppressWarnings("unchecked")
    private static <T> List<Class<? extends T>> extraction(Class<T> clazz, List<String> classFileList) {
        List<Class<? extends T>> classList = new LinkedList<Class<? extends T>>();
        for (String classFile : classFileList) {
            Class<?> classInFile = Reflect.on(classFile).get();
            if (clazz.isAssignableFrom(classInFile) && clazz != classInFile) {
                classList.add((Class<? extends T>) classInFile);
            }
        }

        return classList;
    }

    private static String className(String name) {
        return name.replaceAll("\\\\", "/").replaceAll("/", ".");
    }

    /**
     * 通配符匹配
     * 
     * @param pattern
     *            通配符模式
     * @param str
     *            待匹配的字符串 <a href="http://my.oschina.net/u/556800" target="_blank" rel="nofollow">@return</a>
     *            匹配成功则返回true，否则返回false
     */
    private static boolean wildcardMatch(String pattern, String str) {
        int patternLength = pattern.length();
        int strLength = str.length();
        int strIndex = 0;
        char ch;
        for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {
            ch = pattern.charAt(patternIndex);
            if (ch == '*') {
                // 通配符星号*表示可以匹配任意多个字符
                while (strIndex < strLength) {
                    if (wildcardMatch(pattern.substring(patternIndex + 1), str.substring(strIndex))) {
                        return true;
                    }
                    strIndex++;
                }
            } else if (ch == '?') {
                // 通配符问号?表示匹配任意一个字符
                strIndex++;
                if (strIndex > strLength) {
                    // 表示str中已经没有字符匹配?了。
                    return false;
                }
            } else {
                if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {
                    return false;
                }
                strIndex++;
            }
        }
        return strIndex == strLength;
    }

    public static <T> List<Class<? extends T>> findInClassPath(Class<T> clazz) {
        List<String> classFileList = findFiles(
        		new File(classPath.getFile()), "*.class");
        return extraction(clazz, classFileList);
    }
}
