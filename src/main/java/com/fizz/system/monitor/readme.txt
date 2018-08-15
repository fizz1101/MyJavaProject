1、Sigar获取系统内存、CPU等参数需要以下文件支持
Window环境：JDK/bin/目录下添加文件：sigar-amd64-winnt.dll(64位)、sigar-x86-winnt.dll(32位)、sigar-x86-winnt.lib(32位)
Linux环境：/usr/lib64/目录下添加文件：sigar-amd64-linux.so(64位)、libsigar-x86-linux.so(32位)、sigar-x86-winnt.lib(32位)
注：实际目录路径获取方式：System.getProperty("java.library.path");