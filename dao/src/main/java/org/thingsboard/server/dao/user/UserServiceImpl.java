/**
 * Copyright © 2016-2021 The Thingsboard Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.dao.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserCredentialsId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.common.data.security.UserCredentials;
import org.thingsboard.server.common.data.security.event.UserAuthDataChangedEvent;
import org.thingsboard.server.common.data.tenant.profile.DefaultTenantProfileConfiguration;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.customer.CustomerDao;
import org.thingsboard.server.dao.dto.PageUserDto;
import org.thingsboard.server.dao.dto.UpdateAndSaveDto;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.exception.IncorrectParameterException;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.service.PaginatedRemover;
import org.thingsboard.server.dao.sql.role.RoleUserRepository;
import org.thingsboard.server.dao.sql.user.UserDetailRepository;
import org.thingsboard.server.dao.tenant.TbTenantProfileCache;
import org.thingsboard.server.dao.tenant.TenantDao;
import org.thingsboard.server.dao.vo.PageVo;
import org.thingsboard.server.dao.vo.UserStatusVo;
import org.thingsboard.server.dao.vo.UserVo;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.thingsboard.server.dao.service.Validator.*;

@Service
@Slf4j
public class UserServiceImpl extends AbstractEntityService implements UserService {

    public static final String USER_PASSWORD_HISTORY = "userPasswordHistory";

//    private static ReadWriteHashMap readWriteHashMap=new ReadWriteHashMap();

    private static final String LAST_LOGIN_TS = "lastLoginTs";
    private static final String FAILED_LOGIN_ATTEMPTS = "failedLoginAttempts";

    private static final int DEFAULT_TOKEN_LENGTH = 30;
    public static final String INCORRECT_USER_ID = "Incorrect userId ";
    public static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";

    private static final String USER_CREDENTIALS_ENABLED = "userCredentialsEnabled";

    @Value("${security.user_login_case_sensitive:true}")
    private boolean userLoginCaseSensitive;

    private final UserDao userDao;
    private final UserCredentialsDao userCredentialsDao;
    private final TenantDao tenantDao;
    private final CustomerDao customerDao;
    private final TbTenantProfileCache tenantProfileCache;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    RoleUserRepository roleUserRepository;

    @Autowired
    UserDetailRepository userDetailRepository;

    public UserServiceImpl(UserDao userDao,
                           UserCredentialsDao userCredentialsDao,
                           TenantDao tenantDao,
                           CustomerDao customerDao,
                           @Lazy TbTenantProfileCache tenantProfileCache,
                           ApplicationEventPublisher eventPublisher) {
        this.userDao = userDao;
        this.userCredentialsDao = userCredentialsDao;
        this.tenantDao = tenantDao;
        this.customerDao = customerDao;
        this.tenantProfileCache = tenantProfileCache;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public User findUserByEmail(TenantId tenantId, String email) {
        log.trace("Executing findUserByEmail [{}]", email);
        validateString(email, "Incorrect email " + email);
        if (userLoginCaseSensitive) {
            return userDao.findByEmail(tenantId, email);
        } else {
            return userDao.findByEmail(tenantId, email.toLowerCase());
        }
    }

    @Override
    public User findUserById(TenantId tenantId, UserId userId) {
        log.trace("Executing findUserById [{}]", userId);
        validateId(userId, INCORRECT_USER_ID + userId);
        return userDao.findById(tenantId, userId.getId());
    }

    @Override
    public ListenableFuture<User> findUserByIdAsync(TenantId tenantId, UserId userId) {
        log.trace("Executing findUserByIdAsync [{}]", userId);
        validateId(userId, INCORRECT_USER_ID + userId);
        return userDao.findByIdAsync(tenantId, userId.getId());
    }

    @Override
    public User saveUser(User user) {
        log.trace("Executing saveUser [{}]", user);
        userValidator.validate(user, User::getTenantId);
        if (!userLoginCaseSensitive) {
            user.setEmail(user.getEmail().toLowerCase());
        }
        User savedUser = userDao.save(user.getTenantId(), user);
        if (user.getId() == null) {
            UserCredentials userCredentials = new UserCredentials();
            userCredentials.setEnabled(false);
            userCredentials.setActivateToken(RandomStringUtils.randomAlphanumeric(DEFAULT_TOKEN_LENGTH));
            userCredentials.setUserId(new UserId(savedUser.getUuidId()));
            saveUserCredentialsAndPasswordHistory(user.getTenantId(), userCredentials);
        }
        return savedUser;
    }

    @Override
    public UserCredentials findUserCredentialsByUserId(TenantId tenantId, UserId userId) {
        log.trace("Executing findUserCredentialsByUserId [{}]", userId);
        validateId(userId, INCORRECT_USER_ID + userId);
        UserCredentials byUserId = userCredentialsDao.findByUserId(tenantId, userId.getId());
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateStr = sdf1.format(new Date());
        GlobalConstant.readWriteHashMap.put(String.valueOf(userId), currentDateStr);
        return byUserId;
    }

    @Override
    public UserCredentials findUserCredentialsByActivateToken(TenantId tenantId, String activateToken) {
        log.trace("Executing findUserCredentialsByActivateToken [{}]", activateToken);
        validateString(activateToken, "Incorrect activateToken " + activateToken);
        return userCredentialsDao.findByActivateToken(tenantId, activateToken);
    }

    @Override
    public UserCredentials findUserCredentialsByResetToken(TenantId tenantId, String resetToken) {
        log.trace("Executing findUserCredentialsByResetToken [{}]", resetToken);
        validateString(resetToken, "Incorrect resetToken " + resetToken);
        return userCredentialsDao.findByResetToken(tenantId, resetToken);
    }

    @Override
    public UserCredentials saveUserCredentials(TenantId tenantId, UserCredentials userCredentials) {
        log.trace("Executing saveUserCredentials [{}]", userCredentials);
        userCredentialsValidator.validate(userCredentials, data -> tenantId);
        return saveUserCredentialsAndPasswordHistory(tenantId, userCredentials);
    }

    @Override
    public UserCredentials activateUserCredentials(TenantId tenantId, String activateToken, String password) {
        log.trace("Executing activateUserCredentials activateToken [{}], password [{}]", activateToken, password);
        validateString(activateToken, "Incorrect activateToken " + activateToken);
        validateString(password, "Incorrect password " + password);
        UserCredentials userCredentials = userCredentialsDao.findByActivateToken(tenantId, activateToken);
        if (userCredentials == null) {
            throw new IncorrectParameterException(String.format("Unable to find user credentials by activateToken [%s]", activateToken));
        }
        if (userCredentials.isEnabled()) {
            throw new IncorrectParameterException("User credentials already activated");
        }
        userCredentials.setEnabled(true);
        userCredentials.setActivateToken(null);
        userCredentials.setPassword(password);

        return saveUserCredentials(tenantId, userCredentials);
    }

    @Override
    public UserCredentials requestPasswordReset(TenantId tenantId, String email) {
        log.trace("Executing requestPasswordReset email [{}]", email);
        DataValidator.validateEmail(email);
        User user = findUserByEmail(tenantId, email);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("Unable to find user by email [%s]", email));
        }
        UserCredentials userCredentials = userCredentialsDao.findByUserId(tenantId, user.getUuidId());
        if (!userCredentials.isEnabled()) {
            throw new DisabledException(String.format("User credentials not enabled [%s]", email));
        }
        userCredentials.setResetToken(RandomStringUtils.randomAlphanumeric(DEFAULT_TOKEN_LENGTH));
        return saveUserCredentials(tenantId, userCredentials);
    }

    @Override
    public UserCredentials requestExpiredPasswordReset(TenantId tenantId, UserCredentialsId userCredentialsId) {
        UserCredentials userCredentials = userCredentialsDao.findById(tenantId, userCredentialsId.getId());
        if (!userCredentials.isEnabled()) {
            throw new IncorrectParameterException("Unable to reset password for inactive user");
        }
        userCredentials.setResetToken(RandomStringUtils.randomAlphanumeric(DEFAULT_TOKEN_LENGTH));
        return saveUserCredentials(tenantId, userCredentials);
    }

    @Override
    public UserCredentials replaceUserCredentials(TenantId tenantId, UserCredentials userCredentials) {
        log.trace("Executing replaceUserCredentials [{}]", userCredentials);
        userCredentialsValidator.validate(userCredentials, data -> tenantId);
        userCredentialsDao.removeById(tenantId, userCredentials.getUuidId());
        userCredentials.setId(null);
        return saveUserCredentialsAndPasswordHistory(tenantId, userCredentials);
    }

    @Override
    public void deleteUser(TenantId tenantId, UserId userId) {
        log.trace("Executing deleteUser [{}]", userId);
        validateId(userId, INCORRECT_USER_ID + userId);
        UserCredentials userCredentials = userCredentialsDao.findByUserId(tenantId, userId.getId());
        userCredentialsDao.removeById(tenantId, userCredentials.getUuidId());
        deleteEntityRelations(tenantId, userId);
        userDao.removeById(tenantId, userId.getId());
        eventPublisher.publishEvent(new UserAuthDataChangedEvent(userId));
    }

    @Override
    public PageData<User> findUsersByTenantId(TenantId tenantId, PageLink pageLink) {
        log.trace("Executing findUsersByTenantId, tenantId [{}], pageLink [{}]", tenantId, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validatePageLink(pageLink);
        return userDao.findByTenantId(tenantId.getId(), pageLink);
    }

    @Override
    public PageData<User> findTenantAdmins(TenantId tenantId, PageLink pageLink) {
        log.trace("Executing findTenantAdmins, tenantId [{}], pageLink [{}]", tenantId, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validatePageLink(pageLink);
        return userDao.findTenantAdmins(tenantId.getId(), pageLink);
    }

    @Override
    public void deleteTenantAdmins(TenantId tenantId) {
        log.trace("Executing deleteTenantAdmins, tenantId [{}]", tenantId);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        tenantAdminsRemover.removeEntities(tenantId, tenantId);
    }

    @Override
    public PageData<User> findCustomerUsers(TenantId tenantId, CustomerId customerId, PageLink pageLink) {
        log.trace("Executing findCustomerUsers, tenantId [{}], customerId [{}], pageLink [{}]", tenantId, customerId, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(customerId, "Incorrect customerId " + customerId);
        validatePageLink(pageLink);
        return userDao.findCustomerUsers(tenantId.getId(), customerId.getId(), pageLink);
    }

    @Override
    public void deleteCustomerUsers(TenantId tenantId, CustomerId customerId) {
        log.trace("Executing deleteCustomerUsers, customerId [{}]", customerId);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(customerId, "Incorrect customerId " + customerId);
        customerUsersRemover.removeEntities(tenantId, customerId);
    }

    @Override
    public void setUserCredentialsEnabled(TenantId tenantId, UserId userId, boolean enabled) {
        log.trace("Executing setUserCredentialsEnabled [{}], [{}]", userId, enabled);
        validateId(userId, INCORRECT_USER_ID + userId);
        UserCredentials userCredentials = userCredentialsDao.findByUserId(tenantId, userId.getId());
        userCredentials.setEnabled(enabled);
        saveUserCredentials(tenantId, userCredentials);

        User user = findUserById(tenantId, userId);
        JsonNode additionalInfo = user.getAdditionalInfo();
        if (!(additionalInfo instanceof ObjectNode)) {
            additionalInfo = JacksonUtil.newObjectNode();
        }
        ((ObjectNode) additionalInfo).put(USER_CREDENTIALS_ENABLED, enabled);
        user.setAdditionalInfo(additionalInfo);
        if (enabled) {
            resetFailedLoginAttempts(user);
        }
        userDao.save(user.getTenantId(), user);
    }


    @Override
    public void onUserLoginSuccessful(TenantId tenantId, UserId userId) {
        log.trace("Executing onUserLoginSuccessful [{}]", userId);
        User user = findUserById(tenantId, userId);
        setLastLoginTs(user);
        resetFailedLoginAttempts(user);
        saveUser(user);
    }

    private void setLastLoginTs(User user) {
        JsonNode additionalInfo = user.getAdditionalInfo();
        if (!(additionalInfo instanceof ObjectNode)) {
            additionalInfo = JacksonUtil.newObjectNode();
        }
        ((ObjectNode) additionalInfo).put(LAST_LOGIN_TS, System.currentTimeMillis());
        user.setAdditionalInfo(additionalInfo);
    }

    private void resetFailedLoginAttempts(User user) {
        JsonNode additionalInfo = user.getAdditionalInfo();
        if (!(additionalInfo instanceof ObjectNode)) {
            additionalInfo = JacksonUtil.newObjectNode();
        }
        ((ObjectNode) additionalInfo).put(FAILED_LOGIN_ATTEMPTS, 0);
        user.setAdditionalInfo(additionalInfo);
    }

    @Override
    public int onUserLoginIncorrectCredentials(TenantId tenantId, UserId userId) {
        log.trace("Executing onUserLoginIncorrectCredentials [{}]", userId);
        User user = findUserById(tenantId, userId);
        int failedLoginAttempts = increaseFailedLoginAttempts(user);
        saveUser(user);
        return failedLoginAttempts;
    }

    @Override
    public void update(UpdateAndSaveDto updateAndSaveDto) throws JsonProcessingException {
        //通过账号获取用户id
        User user = GlobalConstant.createUser();
        User userDaoById = userDao.findById(user.getTenantId(), UUID.fromString(updateAndSaveDto.getUser_id()));
        if (userDaoById == null) {
            throw new RuntimeException("编辑用户不存在");
        } else {
            if (StringUtils.isNotEmpty(updateAndSaveDto.getUsername())) {
                User byEmail = userDao.findByEmail(user.getTenantId(), updateAndSaveDto.getUsername());
                if (byEmail != null && !(byEmail.getId().toString().equals(updateAndSaveDto.getUser_id()))) {
                    throw new RuntimeException("修改的账号不能和其他人重复");
                }
            }
            if (StringUtils.isNotEmpty(updateAndSaveDto.getFirst_name())) {
                User byFirstName = userDao.findByFirstName(user.getTenantId(), updateAndSaveDto.getFirst_name());
                if (byFirstName != null && !(byFirstName.getId().toString().equals(updateAndSaveDto.getUser_id()))) {
                    throw new RuntimeException("修改的用户名不能和其他人重复");
                }
            }
        }
        TSysRoleUser tSysRoleUser = roleUserRepository.getByUserId(updateAndSaveDto.getUser_id());
        updateUserVerify(updateAndSaveDto, userDaoById, tSysRoleUser);
        userDao.save(user.getTenantId(), userDaoById);
        roleUserRepository.saveAndFlush(tSysRoleUser);
        // 用户信息表
        List<TSysUserDetail> tSysUserDetailListSave = updateAndSaveDto.getTSysUserDetailList();
        List<TSysUserDetail> byUserId = userDetailRepository.findByUserId(updateAndSaveDto.getUser_id());
        // 判断byUserId是否存在保存的记录集合中，不存在则删除记录
        for (TSysUserDetail tSysUserDetail : byUserId) {
            if (!tSysUserDetailListSave.contains(tSysUserDetail)) {
                userDetailRepository.delete(tSysUserDetail);
            }
        }
        // 保存新的用户详细信息列表
        userDetailRepository.saveAll(tSysUserDetailListSave);
    }

    @Override
    public PageVo<UserVo> pageUser(Integer current, Integer size, PageUserDto pageUserDto) throws JsonProcessingException {
        PageVo<UserVo> pageVo = new PageVo(size, current);
        current = current * size;
        List<UserVo> userVos = new ArrayList<>();
        int total = roleUserRepository.pageUserCount(pageUserDto);
        List<Map> maps = roleUserRepository.pageUser(current, size, pageUserDto);
        UserVo userVo;
        ObjectMapper objectMapper = new ObjectMapper();
        for (Map map : maps) {
            String string = objectMapper.writeValueAsString(map);
            userVo = objectMapper.readValue(string, UserVo.class);
            userVos.add(userVo);
        }
        pageVo.setTotal(total);
        pageVo.setList(userVos);
        return pageVo;
    }

    @Override
    public List<UserVo> pageUserByUserNameAndName(String nameAndUsername) throws JsonProcessingException {
        nameAndUsername = "%" + nameAndUsername + "%";
        List<Map> maps = roleUserRepository.pageUserByUserNameAndName(nameAndUsername);
        UserVo userVo;
        List<UserVo> userVos = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for (Map map : maps) {
            String string = objectMapper.writeValueAsString(map);
            userVo = objectMapper.readValue(string, UserVo.class);
            userVos.add(userVo);
        }
        return userVos;
    }

    @Override
    public List<TSysUserDetail> listUserDetail(String userId) {
        return userDetailRepository.findByUserId(userId);
    }

    @Override
    public void resetPassword(String userId, String password) throws JsonProcessingException {
        User user = GlobalConstant.createUser();
        UserCredentials byUserId = userCredentialsDao.findByUserId(user.getTenantId(), UUID.fromString(userId));
        byUserId.setPassword(password);
        userCredentialsDao.save(user.getTenantId(), byUserId);
    }

    @Override
    public void getUserStatus(UserStatusVo userVo) {
        //1.判断是否第一次登陆
        TSysRoleUser roleUser = roleUserRepository.getByUserId(userVo.getUseId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String formatDate = sdf.format(date);
        userVo.setCurrentDate(formatDate);
        if (roleUser.getLastTime() == null) {
            //第一次登陆
            userVo.setIsOneLogin(true);
            userVo.setFrontDate("");
        } else {
            userVo.setIsOneLogin(false);
            userVo.setFrontDate(sdf.format(roleUser.getLastTime()));
            roleUser.setLastTime(date);
            roleUserRepository.save(roleUser);
        }

        //2.判断是否禁用
        TSysRoleUser byUserId = roleUserRepository.getByUserId(userVo.getUseId());
        if (byUserId != null && byUserId.getUserStatus().equals("0")) {
            userVo.setByEnabled(false);
        } else {
            userVo.setByEnabled(true);
        }

    }

    @Override
    public void updatePas(UserCredentials userCredentials, String newPas) throws JsonProcessingException {
        TenantId tenantId = GlobalConstant.createUser().getTenantId();
        userCredentials.setPassword(newPas);
        userCredentialsDao.save(tenantId, userCredentials);
        String userId = userCredentials.getUserId().getId().toString();
        TSysRoleUser roleUser = roleUserRepository.getByUserId(userId);
        if (roleUser.getLastTime() == null) {
            roleUser.setLastTime(new Date());
            roleUserRepository.save(roleUser);
        }
    }

    @Override
    public void deleteRoleUser(String userId) {
        roleUserRepository.deleteByUserId(userId);
    }

    @Override
    public User findUserByName(String name) throws JsonProcessingException {
        User byLastName = userDao.findByFirstName(GlobalConstant.createUser().getTenantId(), name);
        return byLastName;
    }

    @Override
    public PageVo<UserVo> pageUserByNemeOrAccount(Integer current, Integer size, String nemeOrAccount) throws JsonProcessingException {
        PageVo<UserVo> pageVo = new PageVo(size, current);
        current = current * size;
        List<UserVo> userVos = new ArrayList<>();
        int total = roleUserRepository.pageUserCountByNemeOrAccount(nemeOrAccount);
        List<Map> maps = roleUserRepository.pageUserByNemeOrAccount(current, size, nemeOrAccount);
        UserVo userVo;
        ObjectMapper objectMapper = new ObjectMapper();
        for (Map map : maps) {
            String string = objectMapper.writeValueAsString(map);
            userVo = objectMapper.readValue(string, UserVo.class);
            userVos.add(userVo);
        }
        pageVo.setTotal(total);
        pageVo.setList(userVos);
        return pageVo;
    }


    /**
     * 修改用户---数据有效性验证
     *
     * @param updateAndSaveDto
     * @param user
     * @param tSysRoleUser
     */
    private void updateUserVerify(UpdateAndSaveDto updateAndSaveDto, User user, TSysRoleUser tSysRoleUser) {
        if (StringUtils.isNoneBlank(updateAndSaveDto.getUsername())) {
            user.setEmail(updateAndSaveDto.getUsername());
        }
        if (StringUtils.isNoneBlank(updateAndSaveDto.getFirst_name())) {
            user.setFirstName(updateAndSaveDto.getFirst_name());
        }

        if (StringUtils.isNoneBlank(updateAndSaveDto.getUser_status())) {
            tSysRoleUser.setUserStatus(updateAndSaveDto.getUser_status());
        }
        if (updateAndSaveDto.getRole_id() != null) {
            tSysRoleUser.setRoleId(updateAndSaveDto.getRole_id());
        }
        tSysRoleUser.setUpdatedName(updateAndSaveDto.getUpdated_name());
        tSysRoleUser.setUpdatedTime(updateAndSaveDto.getUpdated_time());
    }

//
//    @Override
//    public Boolean isOneLogin(UserId userId) {
//        if(readWriteHashMap.get(userId.getId())==null){
//            return true;
//        }else {
//            return false;
//        }
//
//    }

    private int increaseFailedLoginAttempts(User user) {
        JsonNode additionalInfo = user.getAdditionalInfo();
        if (!(additionalInfo instanceof ObjectNode)) {
            additionalInfo = JacksonUtil.newObjectNode();
        }
        int failedLoginAttempts = 0;
        if (additionalInfo.has(FAILED_LOGIN_ATTEMPTS)) {
            failedLoginAttempts = additionalInfo.get(FAILED_LOGIN_ATTEMPTS).asInt();
        }
        failedLoginAttempts = failedLoginAttempts + 1;
        ((ObjectNode) additionalInfo).put(FAILED_LOGIN_ATTEMPTS, failedLoginAttempts);
        user.setAdditionalInfo(additionalInfo);
        return failedLoginAttempts;
    }

    private UserCredentials saveUserCredentialsAndPasswordHistory(TenantId tenantId, UserCredentials userCredentials) {
        UserCredentials result = userCredentialsDao.save(tenantId, userCredentials);
        User user = findUserById(tenantId, userCredentials.getUserId());
        if (userCredentials.getPassword() != null) {
            updatePasswordHistory(user, userCredentials);
        }
        return result;
    }

    private void updatePasswordHistory(User user, UserCredentials userCredentials) {
        JsonNode additionalInfo = user.getAdditionalInfo();
        if (!(additionalInfo instanceof ObjectNode)) {
            additionalInfo = JacksonUtil.newObjectNode();
        }
        Map<String, String> userPasswordHistoryMap = null;
        JsonNode userPasswordHistoryJson;
        if (additionalInfo.has(USER_PASSWORD_HISTORY)) {
            userPasswordHistoryJson = additionalInfo.get(USER_PASSWORD_HISTORY);
            userPasswordHistoryMap = JacksonUtil.convertValue(userPasswordHistoryJson, new TypeReference<>() {
            });
        }
        if (userPasswordHistoryMap != null) {
            userPasswordHistoryMap.put(Long.toString(System.currentTimeMillis()), userCredentials.getPassword());
            userPasswordHistoryJson = JacksonUtil.valueToTree(userPasswordHistoryMap);
            ((ObjectNode) additionalInfo).replace(USER_PASSWORD_HISTORY, userPasswordHistoryJson);
        } else {
            userPasswordHistoryMap = new HashMap<>();
            userPasswordHistoryMap.put(Long.toString(System.currentTimeMillis()), userCredentials.getPassword());
            userPasswordHistoryJson = JacksonUtil.valueToTree(userPasswordHistoryMap);
            ((ObjectNode) additionalInfo).set(USER_PASSWORD_HISTORY, userPasswordHistoryJson);
        }
        user.setAdditionalInfo(additionalInfo);
        saveUser(user);
    }

    private final DataValidator<User> userValidator =
            new DataValidator<>() {
                @Override
                protected void validateCreate(TenantId tenantId, User user) {
                    if (!user.getTenantId().getId().equals(ModelConstants.NULL_UUID)) {
                        DefaultTenantProfileConfiguration profileConfiguration =
                                (DefaultTenantProfileConfiguration) tenantProfileCache.get(tenantId).getProfileData().getConfiguration();
                        long maxUsers = profileConfiguration.getMaxUsers();
                        validateNumberOfEntitiesPerTenant(tenantId, userDao, maxUsers, EntityType.USER);
                    }
                }

                @Override
                protected void validateDataImpl(TenantId requestTenantId, User user) {
//                    if (StringUtils.isEmpty(user.getEmail())) {
//                        throw new DataValidationException("User email should be specified!");
//                    }
//
//                    validateEmail(user.getEmail());

                    Authority authority = user.getAuthority();
                    if (authority == null) {
                        throw new DataValidationException("User authority isn't defined!");
                    }
                    TenantId tenantId = user.getTenantId();
                    if (tenantId == null) {
                        tenantId = new TenantId(ModelConstants.NULL_UUID);
                        user.setTenantId(tenantId);
                    }
                    CustomerId customerId = user.getCustomerId();
                    if (customerId == null) {
                        customerId = new CustomerId(ModelConstants.NULL_UUID);
                        user.setCustomerId(customerId);
                    }

                    switch (authority) {
                        case SYS_ADMIN:
                            if (!tenantId.getId().equals(ModelConstants.NULL_UUID)
                                    || !customerId.getId().equals(ModelConstants.NULL_UUID)) {
                                throw new DataValidationException("System administrator can't be assigned neither to tenant nor to customer!");
                            }
                            break;
                        case TENANT_ADMIN:
                            if (tenantId.getId().equals(ModelConstants.NULL_UUID)) {
                                throw new DataValidationException("Tenant administrator should be assigned to tenant!");
                            } else if (!customerId.getId().equals(ModelConstants.NULL_UUID)) {
                                throw new DataValidationException("Tenant administrator can't be assigned to customer!");
                            }
                            break;
                        case CUSTOMER_USER:
                            if (tenantId.getId().equals(ModelConstants.NULL_UUID)
                                    || customerId.getId().equals(ModelConstants.NULL_UUID)) {
                                throw new DataValidationException("Customer user should be assigned to customer!");
                            }
                            break;
                        default:
                            break;
                    }

                    User existentUserWithEmail = findUserByEmail(tenantId, user.getEmail());
                    if (existentUserWithEmail != null && !isSameData(existentUserWithEmail, user)) {
                        throw new DataValidationException("User with email '" + user.getEmail() + "' "
                                + " already present in database!");
                    }
                    if (!tenantId.getId().equals(ModelConstants.NULL_UUID)) {
                        Tenant tenant = tenantDao.findById(tenantId, user.getTenantId().getId());
                        if (tenant == null) {
                            throw new DataValidationException("User is referencing to non-existent tenant!");
                        }
                    }
                    if (!customerId.getId().equals(ModelConstants.NULL_UUID)) {
                        Customer customer = customerDao.findById(tenantId, user.getCustomerId().getId());
                        if (customer == null) {
                            throw new DataValidationException("User is referencing to non-existent customer!");
                        } else if (!customer.getTenantId().getId().equals(tenantId.getId())) {
                            throw new DataValidationException("User can't be assigned to customer from different tenant!");
                        }
                    }
                }
            };

    private final DataValidator<UserCredentials> userCredentialsValidator =
            new DataValidator<>() {

                @Override
                protected void validateCreate(TenantId tenantId, UserCredentials userCredentials) {
                    throw new IncorrectParameterException("Creation of new user credentials is prohibited.");
                }

                @Override
                protected void validateDataImpl(TenantId tenantId, UserCredentials userCredentials) {
                    if (userCredentials.getUserId() == null) {
                        throw new DataValidationException("User credentials should be assigned to user!");
                    }
                    if (userCredentials.isEnabled()) {
                        if (StringUtils.isEmpty(userCredentials.getPassword())) {
                            throw new DataValidationException("Enabled user credentials should have password!");
                        }
                        if (StringUtils.isNotEmpty(userCredentials.getActivateToken())) {
                            throw new DataValidationException("Enabled user credentials can't have activate token!");
                        }
                    }
                    UserCredentials existingUserCredentialsEntity = userCredentialsDao.findById(tenantId, userCredentials.getId().getId());
                    if (existingUserCredentialsEntity == null) {
                        throw new DataValidationException("Unable to update non-existent user credentials!");
                    }
                    User user = findUserById(tenantId, userCredentials.getUserId());
                    if (user == null) {
                        throw new DataValidationException("Can't assign user credentials to non-existent user!");
                    }
                }
            };

    private final PaginatedRemover<TenantId, User> tenantAdminsRemover = new PaginatedRemover<>() {
        @Override
        protected PageData<User> findEntities(TenantId tenantId, TenantId id, PageLink pageLink) {
            return userDao.findTenantAdmins(id.getId(), pageLink);
        }

        @Override
        protected void removeEntity(TenantId tenantId, User entity) {
            deleteUser(tenantId, new UserId(entity.getUuidId()));
        }
    };

    private final PaginatedRemover<CustomerId, User> customerUsersRemover = new PaginatedRemover<>() {
        @Override
        protected PageData<User> findEntities(TenantId tenantId, CustomerId id, PageLink pageLink) {
            return userDao.findCustomerUsers(tenantId.getId(), id.getId(), pageLink);

        }

        @Override
        protected void removeEntity(TenantId tenantId, User entity) {
            deleteUser(tenantId, new UserId(entity.getUuidId()));
        }
    };

}
