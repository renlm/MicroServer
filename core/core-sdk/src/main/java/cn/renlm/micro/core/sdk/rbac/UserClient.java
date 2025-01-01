package cn.renlm.micro.core.sdk.rbac;

import static cn.renlm.micro.constant.ServiceNameConstants.CORE_SERVICE;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.renlm.micro.common.Resp;
import cn.renlm.micro.core.model.rbac.UserInfo;

/**
 * 用户
 * 
 * @author RenLiMing(任黎明)
 *
 */
@FeignClient(contextId = "userClient", path = "/user", name = CORE_SERVICE)
public interface UserClient {

	/**
	 * 根据登录账号获取用户信息
	 * 
	 * @param username
	 * @return
	 */
	@GetMapping(value = "/loadUserByUsername")
	Resp<UserInfo> loadUserByUsername(@RequestParam("username") String username);

	/**
	 * 根据用户ID获取用户信息
	 * 
	 * @param userId
	 * @return
	 */
	@GetMapping(value = "/getByUserId")
	Resp<UserInfo> getByUserId(@RequestParam("userId") String userId);

}
