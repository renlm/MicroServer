package cn.renlm.micro.core.sdk.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import static cn.renlm.micro.constant.ServiceNameConstants.CORE_SERVICE;

/**
 * 用户
 * 
 * @author RenLiMing(任黎明)
 *
 */
@FeignClient(contextId = "userClient", name = CORE_SERVICE)
public class UserClient {

}