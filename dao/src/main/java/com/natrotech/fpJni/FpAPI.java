package com.natrotech.fpJni;


public class FpAPI {

	// global defines -------------------------------------------------------
	public static final int FP_TEMPLATE_SIZE				= (768);



	// return codes ---------------------------------------------------------
	public static final int ERR_SUCCESS						= (0);
	public static final int ERR_LOAD_LIB_FAIL				= (-1);
	public static final int ERR_LOAD_FUNC_FAIL				= (-2);
	public static final int ERR_JNI_ERROR					= (-10);
	public static final int ERR_LIB_NOT_LOAD				= (-20);
	public static final int ERR_MEMORY_ERROR				= (-30);


	static {
		System.out.println("java.library.path:" + System.getProperty("java.library.path"));
		System.loadLibrary("FpMatchJNI_64");
	}

	private static FpAPI instance = new FpAPI();

	private FpAPI() {
		int w_nRet = init();
		if (w_nRet < 0)
		{
			System.out.println(String.format("Load library failed = %d", w_nRet));
			return;
		}
	}

	public static FpAPI getInst(){
		return instance;
	}
	public native int match(byte[] registeredTemplate, byte[] queryFeature, int nSecurityLevel, int[] pnResult);

	private native int init();
	public native int uninit();


}
