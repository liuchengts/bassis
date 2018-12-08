package bassis.bassis_bean.scan;

import org.apache.log4j.Logger;

import bassis.bassis_bean.ReferenceDeclaration;
import bassis.bassis_bean.annotation.impl.ComponentImpl;
import org.bassis.bassis_tools.string.StringUtils;

public class ScanConfig {
	private static Logger logger = Logger.getLogger(ScanTask.class);

	private static class LazyHolder {
		private static final ScanConfig INSTANCE = new ScanConfig();
	}

	private ScanConfig() {
	}

	public static final ScanConfig getInstance() {
		return LazyHolder.INSTANCE;
	}

	/**
	 * 扫描根路径 如果没有设置扫描根目录 默认从起点开始扫描
	 */
	private static String scanRoot;
	/**
	 * 开始扫描 扫描路径为ReferenceDeclaration.getScanRoot()
	 * 可以直接调用setScanRoot方法进行自定义扫描路径
	 */
    public static void scanStart() {
    	ScanTask.scanInit();
    	ComponentImpl.getInstance();
	}
	public static String getScanRoot() {
        if(StringUtils.isEmptyString(scanRoot))
        	scanRoot=ReferenceDeclaration.getScanRoot();
        	
		return scanRoot;
	}
    /**
     * 调用此方法后会立即执行扫描
     * @param scanRoot
     */
	public static void setScanRoot(String scanRoot) {
		ScanConfig.scanRoot = scanRoot;
		scanStart();
	}
}
