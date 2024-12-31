package cn.renlm.micro.core.sdk.rbac;

import static cn.renlm.micro.constant.ServiceNameConstants.CORE_WEB;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import cn.renlm.micro.common.Resp;
import cn.renlm.micro.core.model.rbac.UserClaim;

/**
 * 会话
 * 
 * @author RenLiMing(任黎明)
 *
 */
@FeignClient(contextId = "sessionClient", name = CORE_WEB)
public interface SessionClient {

	/**
	 * 获取当前登录用户信息
	 * 
	 * @return
	 */
	@GetMapping(value = "/getCurrentUser")
	Resp<UserClaim> getCurrentUser();

}
