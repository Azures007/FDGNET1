/**
 * Copyright © 2016-2021 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.dao.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserCredentialsId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.security.UserCredentials;
import org.thingsboard.server.dao.dto.PageUserDto;
import org.thingsboard.server.dao.dto.UpdateAndSaveDto;
import org.thingsboard.server.dao.vo.PageVo;
import org.thingsboard.server.dao.vo.UserStatusVo;
import org.thingsboard.server.dao.vo.UserVo;

import java.util.List;

public interface UserService {
	
	User findUserById(TenantId tenantId, UserId userId);

	ListenableFuture<User> findUserByIdAsync(TenantId tenantId, UserId userId);

	User findUserByEmail(TenantId tenantId, String email);

	User saveUser(User user);

	UserCredentials findUserCredentialsByUserId(TenantId tenantId, UserId userId);
	
	UserCredentials findUserCredentialsByActivateToken(TenantId tenantId, String activateToken);

	UserCredentials findUserCredentialsByResetToken(TenantId tenantId, String resetToken);

	UserCredentials saveUserCredentials(TenantId tenantId, UserCredentials userCredentials);
	
	UserCredentials activateUserCredentials(TenantId tenantId, String activateToken, String password);
	
	UserCredentials requestPasswordReset(TenantId tenantId, String email);

    UserCredentials requestExpiredPasswordReset(TenantId tenantId, UserCredentialsId userCredentialsId);

    UserCredentials replaceUserCredentials(TenantId tenantId, UserCredentials userCredentials);

	void deleteUser(TenantId tenantId, UserId userId);

    PageData<User> findUsersByTenantId(TenantId tenantId, PageLink pageLink);

    PageData<User> findTenantAdmins(TenantId tenantId, PageLink pageLink);
	
	void deleteTenantAdmins(TenantId tenantId);

    PageData<User> findCustomerUsers(TenantId tenantId, CustomerId customerId, PageLink pageLink);
	    
	void deleteCustomerUsers(TenantId tenantId, CustomerId customerId);

	void setUserCredentialsEnabled(TenantId tenantId, UserId userId, boolean enabled);

	void onUserLoginSuccessful(TenantId tenantId, UserId userId);

	int onUserLoginIncorrectCredentials(TenantId tenantId, UserId userId);


	/**
	 * 编辑用户
	 * @param updateAndSaveDto
	 */
    void update(UpdateAndSaveDto updateAndSaveDto) throws JsonProcessingException;

	/**
	 * 用户分页列表
	 * @param current
	 * @param size
	 * @param pageUserDto
	 */
	PageVo<UserVo> pageUser(Integer current, Integer size, PageUserDto pageUserDto) throws JsonProcessingException;

	/**
	 * 重置密码
	 * @param userId
	 * @param password
	 */
    void resetPassword(String userId, String password) throws JsonProcessingException;

	/**
	 * 返回用户状态
	 * @param userVo
	 */
    void getUserStatus(UserStatusVo userVo);

	/**
	 * 修改密码
	 */
	void updatePas(UserCredentials userCredentials, String newPas) throws JsonProcessingException;

	/**
	 * 删除用户角色关系
	 * @param userId
	 */
    void deleteRoleUser(String userId);

	/**
	 * 获取用户信息通过姓名
	 * @param name
	 * @return
	 */
    User findUserByName(String name) throws JsonProcessingException;

    /**
	 * 判断用户是否为第一次登陆
	 * @param userId
	 * @return
	 */
//    Boolean isOneLogin(UserId userId);

	/**
	 * 人员信息管理绑定账号获取列表
	 * @return
	 */
	PageVo<UserVo> pageUserByNemeOrAccount(Integer current, Integer size, String nemeOrAccount) throws JsonProcessingException;

	List<UserVo> pageUserByUserNameAndName(String nameAndUsername) throws JsonProcessingException;
}
