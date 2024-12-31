package cn.renlm.micro.core.security;

import static org.apache.commons.lang.StringUtils.EMPTY;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import com.netflix.appinfo.EurekaInstanceConfig;

import cn.renlm.micro.common.Resp;
import cn.renlm.micro.constant.Constants;
import cn.renlm.micro.core.dto.UserDetails;
import cn.renlm.micro.core.model.rbac.UserInfo;
import cn.renlm.micro.core.sdk.rbac.UserClient;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 用户信息
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

	@Resource
	private EurekaInstanceConfig eurekaInstanceConfig;

	@Resource
	private UserClient userClient;

	@Resource
	private SecurityContextRepository securityContextRepository;

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
	
	/**
	 * 更新当前登录用户信息
	 * 
	 * @param request
	 * @param response
	 * @param principal
	 * @return
	 */
	public UserDetails updateCurrentUser(HttpServletRequest request, HttpServletResponse response, Consumer<UserDetails> principal) {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		principal.accept(userDetails);
		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
		context.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, EMPTY, authorities));
		securityContextRepository.saveContext(context, request, response);
		return userDetails;
	}

}
