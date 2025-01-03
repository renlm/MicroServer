package cn.renlm.micro.constant;

/**
 * 公共常量
 * 
 * @author RenLiMing(任黎明)
 *
 */
public interface Constants {

	/**
	 * 请求头负载均衡标记名称
	 */
	public static final String HINT_HEADER_NAME = "x-lb-hint";

	/**
	 * 服务元数据负载均衡标记名称
	 */
	public static final String HINT_METADATA_NAME = "hint";

	/**
	 * x-xsrf-token
	 */
	public static final String X_XSRF_TOKEN_HEADER_NAME = "x-xsrf-token";

	/**
	 * cookie
	 */
	public static final String COOKIE_HEADER_NAME = "cookie";

}
