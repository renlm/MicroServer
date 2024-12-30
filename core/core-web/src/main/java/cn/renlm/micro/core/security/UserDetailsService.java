package cn.renlm.micro.core.security;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.netflix.appinfo.EurekaInstanceConfig;

import cn.renlm.micro.core.dto.UserDetails;
import cn.renlm.micro.core.model.rbac.UserInfo;
import cn.renlm.micro.core.sdk.rbac.UserClient;
import jakarta.annotation.Resource;

/**
 * 用户信息
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

	@Resource
	private UserClient userClient;

	@Resource
	private EurekaInstanceConfig eurekaInstanceConfig;

	@Override
	public UserDetails loadUserByUsername(String username) {
		UserInfo userInfo = userClient.loadUserByUsername(username);
		Map<String, String> metadataMap = eurekaInstanceConfig.getMetadataMap();
		UserDetails userDetails = UserDetails.of(userInfo);
		userDetails.setHint(metadataMap.get("hint"));
		return userDetails;
	}

}
