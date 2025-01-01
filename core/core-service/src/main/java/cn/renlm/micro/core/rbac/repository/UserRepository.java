package cn.renlm.micro.core.rbac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.renlm.micro.core.rbac.entity.User;

/**
 * <p>
 * 用户
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2024-12-28
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

	/**
	 * 根据登录账号获取用户信息
	 * 
	 * @param username
	 * @return
	 */
	User findByUsername(String username);

	/**
	 * 根据用户ID获取用户信息
	 * 
	 * @param userId
	 * @return
	 */
	User findByUserId(String userId);

}
