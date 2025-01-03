package cn.renlm.micro.constant;

/**
 * 公共常量
 * 
 * @author RenLiMing(任黎明)
 *
 */
public interface Constants {

	/**
	 * 请求头 [ 负载均衡标记 ]
	 */
	public static final String HINT_HEADER_NAME = "x-lb-hint";

	/**
	 * x-xsrf-token
	 */
	public static final String X_XSRF_TOKEN_HEADER_NAME = "x-xsrf-token";

	/**
	 * cookie
	 */
	public static final String COOKIE_HEADER_NAME = "cookie";

	/**
	 * 服务元数据 [ 负载均衡标记 ]
	 */
	public static final String HINT_KEY = "hint";

	/**
	 * 服务元数据 [ podIp ]
	 */
	public static final String POD_IP_KEY = "POD_IP";

	/**
	 * 服务元数据 [ serviceName ]
	 */
	public static final String POD_SERVICE_NAME_KEY = "POD_SERVICE_NAME";

	/**
	 * 服务元数据 [ namespace ]
	 */
	public static final String POD_NAMESPACE_KEY = "POD_NAMESPACE";

}
