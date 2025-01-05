package cn.renlm.micro.constant;

/**
 * 公共常量
 * 
 * @author RenLiMing(任黎明)
 *
 */
public interface Constants {

	/**
	 * x-xsrf-token
	 */
	public static final String X_XSRF_TOKEN = "x-xsrf-token";

	/**
	 * _csrf
	 */
	public static final String _CSRF = "_csrf";

	/**
	 * cookie
	 */
	public static final String COOKIE = "cookie";

	/**
	 * 负载均衡默认配置
	 */
	public static final String HINT_DEFAULT_CONFIG = "default";

	/**
	 * 请求头 [ 负载均衡标记 ]
	 */
	public static final String X_LB_HINT = "x-lb-hint";

	/**
	 * 服务元数据 [ 负载均衡标记 ]
	 */
	public static final String METADATA_HINT = "hint";

	/**
	 * 服务元数据 [ serviceName ]
	 */
	public static final String POD_SERVICE_NAME_KEY = "POD_SERVICE_NAME";

	/**
	 * 服务元数据 [ podName ]
	 */
	public static final String POD_NAME_KEY = "POD_NAME";

	/**
	 * 服务元数据 [ podIp ]
	 */
	public static final String POD_IP_KEY = "POD_IP";

	/**
	 * 服务元数据 [ namespace ]
	 */
	public static final String POD_NAMESPACE_KEY = "POD_NAMESPACE";

}
