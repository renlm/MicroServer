package cn.renlm.micro.core.sdk.user.client;

import static cn.renlm.micro.constant.ServiceNameConstants.CORE_SERVICE;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import cn.renlm.micro.core.model.user.UserDetails;

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
	 * @param userName
	 * @return
	 */
	@GetMapping(value = "/loadUserByUserName")
	UserDetails loadUserByUserName(String userName);

}
