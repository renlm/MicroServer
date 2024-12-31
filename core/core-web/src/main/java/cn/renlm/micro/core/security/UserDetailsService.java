package cn.renlm.micro.core.security;

import java.util.Map;
import java.util.Objects;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import com.netflix.appinfo.EurekaInstanceConfig;

import cn.renlm.micro.common.Resp;
import cn.renlm.micro.constant.Constants;
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
		Resp<UserInfo> resp = userClient.loadUserByUsername(username);
		UserInfo userInfo = resp.getData();
		if (Objects.isNull(userInfo)) {
			throw new BadCredentialsException("用户名或密码错误");
		}
		Map<String, String> metadataMap = eurekaInstanceConfig.getMetadataMap();
		UserDetails userDetails = UserDetails.of(userInfo);
		if (Objects.nonNull(userDetails)) {
			userDetails.setHint(metadataMap.get(Constants.HINT_METADATA_NAME));
		}
		{
			return userDetails;
		}
	}

}
