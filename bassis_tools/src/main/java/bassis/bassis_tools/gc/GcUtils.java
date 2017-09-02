package bassis.bassis_tools.gc;

public class GcUtils {
	private static class LazyHolder {
		private static final GcUtils INSTANCE = new GcUtils();
	}

	private GcUtils() {
	}

	public static final GcUtils getInstance() {
		add();
		return LazyHolder.INSTANCE;
	}
	private static long index=0;
	/**
	 * 计数上限
	 */
	private static final long  COUNT=10;
	/**
	 * 计数器
	 * @return
	 */
	private static long add() {
		index++;
		if(index>=COUNT){
			gc();
		}
		return index;
	}
	private static void gc() {
		System.gc();
		index=0;
	}
}
