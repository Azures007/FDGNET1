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
package org.thingsboard.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.rule.engine.api.MailService;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.mes.ncWarehouse.NcWarehouse;
import org.thingsboard.server.common.data.mes.sys.TSysRoleUser;
import org.thingsboard.server.common.data.mes.sys.TSysUserDetail;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.edge.EdgeEventActionType;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.mes.ncOrg.NcOrganization;
import org.thingsboard.server.common.data.mes.ncWorkline.NcWorkline;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.common.data.security.UserCredentials;
import org.thingsboard.server.common.data.security.event.UserAuthDataChangedEvent;
import org.thingsboard.server.common.data.security.model.JwtToken;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.mes.dto.PageUserDto;
import org.thingsboard.server.dao.mes.dto.UpdateAndSaveDto;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.dao.mes.vo.UserClassVo;
import org.thingsboard.server.dao.mes.vo.UserStatusVo;
import org.thingsboard.server.dao.mes.vo.UserVo;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.auth.jwt.RefreshTokenRepository;
import org.thingsboard.server.service.security.model.JwtTokenPair;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.model.UserPrincipal;
import org.thingsboard.server.service.security.model.token.JwtTokenFactory;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;
import org.thingsboard.server.service.security.system.SystemSecurityService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static org.thingsboard.server.controller.ControllerConstants.*;

@RequiredArgsConstructor
@RestController
@Slf4j
@TbCoreComponent
@RequestMapping("/api")
@Api(value = "用户模块接口", tags = "用户模块接口")
public class UserController extends BaseController {

    public static final String USER_ID = "userId";
    public static final String YOU_DON_T_HAVE_PERMISSION_TO_PERFORM_THIS_OPERATION = "你没有权限执行该操作!";
    public static final String ACTIVATE_URL_PATTERN = "%s/api/noauth/activate?activateToken=%s";

    @Value("${security.user_token_access_enabled}")
    @Getter
    private boolean userTokenAccessEnabled;

    private final MailService mailService;
    private final JwtTokenFactory tokenFactory;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SystemSecurityService systemSecurityService;
    private final ApplicationEventPublisher eventPublisher;

    @ApiOperation(value = "Get User (getUserById)",
            notes = "Fetch the User object based on the provided User Id. " +
                    "If the user has the authority of 'SYS_ADMIN', the server does not perform additional checks. " +
                    "If the user has the authority of 'TENANT_ADMIN', the server checks that the requested user is owned by the same tenant. " +
                    "If the user has the authority of 'CUSTOMER_USER', the server checks that the requested user is owned by the same customer.")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public User getUserById(
            @ApiParam(value = USER_ID_PARAM_DESCRIPTION)
            @PathVariable(USER_ID) String strUserId) throws ThingsboardException {
        checkParameter(USER_ID, strUserId);
        try {
            UserId userId = new UserId(toUUID(strUserId));
            User user = checkUserId(userId, Operation.READ);
            if (user.getAdditionalInfo().isObject()) {
                ObjectNode additionalInfo = (ObjectNode) user.getAdditionalInfo();
                processDashboardIdFromAdditionalInfo(additionalInfo, DEFAULT_DASHBOARD);
                processDashboardIdFromAdditionalInfo(additionalInfo, HOME_DASHBOARD);
                UserCredentials userCredentials = userService.findUserCredentialsByUserId(user.getTenantId(), user.getId());
                if (userCredentials.isEnabled() && !additionalInfo.has("userCredentialsEnabled")) {
                    additionalInfo.put("userCredentialsEnabled", true);
                }
            }
            return user;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Check Token Access Enabled (isUserTokenAccessEnabled)",
            notes = "Checks that the system is configured to allow administrators to impersonate themself as other users. " +
                    "If the user who performs the request has the authority of 'SYS_ADMIN', it is possible to login as any tenant administrator. " +
                    "If the user who performs the request has the authority of 'TENANT_ADMIN', it is possible to login as any customer user. ")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/user/tokenAccessEnabled", method = RequestMethod.GET)
    @ResponseBody
    public boolean isUserTokenAccessEnabled() {
        return userTokenAccessEnabled;
    }

    @ApiOperation(value = "Get User Token (getUserToken)",
            notes = "Returns the token of the User based on the provided User Id. " +
                    "If the user who performs the request has the authority of 'SYS_ADMIN', it is possible to get the token of any tenant administrator. " +
                    "If the user who performs the request has the authority of 'TENANT_ADMIN', it is possible to get the token of any customer user that belongs to the same tenant. ")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/user/{userId}/token", method = RequestMethod.GET)
    @ResponseBody
    public JwtTokenPair getUserToken(
            @ApiParam(value = USER_ID_PARAM_DESCRIPTION)
            @PathVariable(USER_ID) String strUserId) throws ThingsboardException {
        checkParameter(USER_ID, strUserId);
        try {
            if (!userTokenAccessEnabled) {
                throw new ThingsboardException(YOU_DON_T_HAVE_PERMISSION_TO_PERFORM_THIS_OPERATION,
                        ThingsboardErrorCode.PERMISSION_DENIED);
            }
            UserId userId = new UserId(toUUID(strUserId));
            SecurityUser authUser = getCurrentUser();
            User user = checkUserId(userId, Operation.READ);
            UserPrincipal principal = new UserPrincipal(UserPrincipal.Type.USER_NAME, user.getEmail());
            UserCredentials credentials = userService.findUserCredentialsByUserId(authUser.getTenantId(), userId);
            SecurityUser securityUser = new SecurityUser(user, credentials.isEnabled(), principal);
            JwtToken accessToken = tokenFactory.createAccessJwtToken(securityUser);
            JwtToken refreshToken = refreshTokenRepository.requestRefreshToken(securityUser);
            return new JwtTokenPair(accessToken.getToken(), refreshToken.getToken());
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Save Or update User (saveUser)",
            notes = "Create or update the User. When creating user, platform generates User Id as " + UUID_WIKI_LINK +
                    "The newly created User Id will be present in the response. " +
                    "Specify existing User Id to update the device. " +
                    "Referencing non-existing User Id will cause 'Not Found' error." +
                    "\n\nDevice email is unique for entire platform setup.")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    @ResponseBody
    public User saveUser(
            @ApiParam(value = "A JSON value representing the User.", required = true)
            @RequestBody User user,
            @ApiParam(value = "Send activation email (or use activation link)", defaultValue = "true")
            @RequestParam(required = false, defaultValue = "true") boolean sendActivationMail, HttpServletRequest request) throws ThingsboardException {
        try {
            if (Authority.TENANT_ADMIN.equals(getCurrentUser().getAuthority())) {
                user.setTenantId(getCurrentUser().getTenantId());
            }

            checkEntity(user.getId(), user, Resource.USER);

            boolean sendEmail = user.getId() == null && sendActivationMail;
            User savedUser = checkNotNull(userService.saveUser(user));
            if (sendEmail) {
                SecurityUser authUser = getCurrentUser();
                UserCredentials userCredentials = userService.findUserCredentialsByUserId(authUser.getTenantId(), savedUser.getId());
                String baseUrl = systemSecurityService.getBaseUrl(getTenantId(), getCurrentUser().getCustomerId(), request);
                String activateUrl = String.format(ACTIVATE_URL_PATTERN, baseUrl,
                        userCredentials.getActivateToken());
                String email = savedUser.getEmail();
                try {
                    mailService.sendActivationEmail(activateUrl, email);
                } catch (ThingsboardException e) {
                    userService.deleteUser(authUser.getTenantId(), savedUser.getId());
                    throw e;
                }
            }

            logEntityAction(savedUser.getId(), savedUser,
                    savedUser.getCustomerId(),
                    user.getId() == null ? ActionType.ADDED : ActionType.UPDATED, null);

            sendEntityNotificationMsg(getTenantId(), savedUser.getId(),
                    user.getId() == null ? EdgeEventActionType.ADDED : EdgeEventActionType.UPDATED);

            return savedUser;
        } catch (Exception e) {

            logEntityAction(emptyId(EntityType.USER), user,
                    null, user.getId() == null ? ActionType.ADDED : ActionType.UPDATED, e);

            throw handleException(e);
        }
    }

    @ApiOperation(value = "Send or re-send the activation email",
            notes = "Force send the activation email to the user. Useful to resend the email if user has accidentally deleted it. " + SYSTEM_OR_TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/user/sendActivationMail", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void sendActivationEmail(
            @ApiParam(value = "Email of the user", required = true)
            @RequestParam(value = "email") String email,
            HttpServletRequest request) throws ThingsboardException {
        try {
            User user = checkNotNull(userService.findUserByEmail(getCurrentUser().getTenantId(), email));

            accessControlService.checkPermission(getCurrentUser(), Resource.USER, Operation.READ,
                    user.getId(), user);

            UserCredentials userCredentials = userService.findUserCredentialsByUserId(getCurrentUser().getTenantId(), user.getId());
            if (!userCredentials.isEnabled() && userCredentials.getActivateToken() != null) {
                String baseUrl = systemSecurityService.getBaseUrl(getTenantId(), getCurrentUser().getCustomerId(), request);
                String activateUrl = String.format(ACTIVATE_URL_PATTERN, baseUrl,
                        userCredentials.getActivateToken());
                mailService.sendActivationEmail(activateUrl, email);
            } else {
                throw new ThingsboardException("User is already activated!", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Get the activation link (getActivationLink)",
            notes = "Get the activation link for the user. " +
                    "The base url for activation link is configurable in the general settings of system administrator. " + SYSTEM_OR_TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/user/{userId}/activationLink", method = RequestMethod.GET, produces = "text/plain")
    @ResponseBody
    public String getActivationLink(
            @ApiParam(value = USER_ID_PARAM_DESCRIPTION)
            @PathVariable(USER_ID) String strUserId,
            HttpServletRequest request) throws ThingsboardException {
        checkParameter(USER_ID, strUserId);
        try {
            UserId userId = new UserId(toUUID(strUserId));
            User user = checkUserId(userId, Operation.READ);
            SecurityUser authUser = getCurrentUser();
            UserCredentials userCredentials = userService.findUserCredentialsByUserId(authUser.getTenantId(), user.getId());
            if (!userCredentials.isEnabled() && userCredentials.getActivateToken() != null) {
                String baseUrl = systemSecurityService.getBaseUrl(getTenantId(), getCurrentUser().getCustomerId(), request);
                String activateUrl = String.format(ACTIVATE_URL_PATTERN, baseUrl,
                        userCredentials.getActivateToken());
                return activateUrl;
            } else {
                throw new ThingsboardException("User is already activated!", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Delete User (deleteUser)",
            notes = "Deletes the User, it's credentials and all the relations (from and to the User). " +
                    "Referencing non-existing User Id will cause an error. " + SYSTEM_OR_TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteUser(
            @ApiParam(value = USER_ID_PARAM_DESCRIPTION)
            @PathVariable(USER_ID) String strUserId) throws ThingsboardException {
        checkParameter(USER_ID, strUserId);
        try {
            UserId userId = new UserId(toUUID(strUserId));
            User user = checkUserId(userId, Operation.DELETE);

            List<EdgeId> relatedEdgeIds = findRelatedEdgeIds(getTenantId(), userId);

            userService.deleteUser(getCurrentUser().getTenantId(), userId);

            logEntityAction(userId, user,
                    user.getCustomerId(),
                    ActionType.DELETED, null, strUserId);

            sendDeleteNotificationMsg(getTenantId(), userId, relatedEdgeIds);

        } catch (Exception e) {
            logEntityAction(emptyId(EntityType.USER),
                    null,
                    null,
                    ActionType.DELETED, e, strUserId);
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Get Users (getUsers)",
            notes = "Returns a page of users owned by tenant or customer. The scope depends on authority of the user that performs the request." +
                    PAGE_DATA_PARAMETERS + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/users", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<User> getUsers(
            @ApiParam(value = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @ApiParam(value = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @ApiParam(value = USER_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = USER_SORT_PROPERTY_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortProperty,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        try {
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
            SecurityUser currentUser = getCurrentUser();
            if (Authority.TENANT_ADMIN.equals(currentUser.getAuthority())) {
                return checkNotNull(userService.findUsersByTenantId(currentUser.getTenantId(), pageLink));
            } else {
                return checkNotNull(userService.findCustomerUsers(currentUser.getTenantId(), currentUser.getCustomerId(), pageLink));
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Get Tenant Users (getTenantAdmins)",
            notes = "Returns a page of users owned by tenant. " + PAGE_DATA_PARAMETERS + SYSTEM_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/tenant/{tenantId}/users", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<User> getTenantAdmins(
            @ApiParam(value = TENANT_ID_PARAM_DESCRIPTION, required = true)
            @PathVariable(TENANT_ID) String strTenantId,
            @ApiParam(value = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @ApiParam(value = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @ApiParam(value = USER_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = USER_SORT_PROPERTY_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortProperty,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        checkParameter("tenantId", strTenantId);
        try {
            TenantId tenantId = new TenantId(toUUID(strTenantId));
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
            return checkNotNull(userService.findTenantAdmins(tenantId, pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Get Customer Users (getCustomerUsers)",
            notes = "Returns a page of users owned by customer. " + PAGE_DATA_PARAMETERS + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/customer/{customerId}/users", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<User> getCustomerUsers(
            @ApiParam(value = CUSTOMER_ID_PARAM_DESCRIPTION, required = true)
            @PathVariable(CUSTOMER_ID) String strCustomerId,
            @ApiParam(value = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @ApiParam(value = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @ApiParam(value = USER_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = USER_SORT_PROPERTY_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortProperty,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        checkParameter("customerId", strCustomerId);
        try {
            CustomerId customerId = new CustomerId(toUUID(strCustomerId));
            checkCustomerId(customerId, Operation.READ);
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
            TenantId tenantId = getCurrentUser().getTenantId();
            return checkNotNull(userService.findCustomerUsers(tenantId, customerId, pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Enable/Disable User credentials (setUserCredentialsEnabled)",
            notes = "Enables or Disables user credentials. Useful when you would like to block user account without deleting it. " + PAGE_DATA_PARAMETERS + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/user/{userId}/userCredentialsEnabled", method = RequestMethod.POST)
    @ResponseBody
    public void setUserCredentialsEnabled(
            @ApiParam(value = USER_ID_PARAM_DESCRIPTION)
            @PathVariable(USER_ID) String strUserId,
            @ApiParam(value = "Disable (\"true\") or enable (\"false\") the credentials.", defaultValue = "true")
            @RequestParam(required = false, defaultValue = "true") boolean userCredentialsEnabled) throws ThingsboardException {
        checkParameter(USER_ID, strUserId);
        try {
            UserId userId = new UserId(toUUID(strUserId));
            User user = checkUserId(userId, Operation.WRITE);
            TenantId tenantId = getCurrentUser().getTenantId();
            userService.setUserCredentialsEnabled(tenantId, userId, userCredentialsEnabled);

            if (!userCredentialsEnabled) {
                eventPublisher.publishEvent(new UserAuthDataChangedEvent(userId));
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    @ApiOperation("新增/修改用户")
    @PostMapping("/user/updateAndSave")
    public ResponseResult updateAndSave(@RequestBody UpdateAndSaveDto updateAndSaveDto,
                                        HttpServletResponse resp,
                                        HttpServletRequest req) throws ThingsboardException, JsonProcessingException {
        SecurityUser currentUser = getCurrentUser();
        currentUser.setAuthority(Authority.TENANT_ADMIN);
        if (StringUtils.isBlank(updateAndSaveDto.getUser_id())) {
            //新增
            User user = GlobalConstant.createUser();
            String password;
            TSysRoleUser tSysRoleUser = new TSysRoleUser();
            tSysRoleUser.setCreatedName(currentUser.getName());
            tSysRoleUser.setCreatedTime(new Date());
            //账号验证
            if (StringUtils.isBlank(updateAndSaveDto.getUsername())) {
                throw new RuntimeException("账号不能为空");
            } else {
                User userByEmail = userService.findUserByEmail(user.getTenantId(), updateAndSaveDto.getUsername());
                if (userByEmail != null) {
                    throw new RuntimeException("已存在的账号，请设置新账号！");
                } else {
                    user.setEmail(updateAndSaveDto.getUsername());
                }
            }
            //密码验证
            password = updateAndSaveDto.getPassword();
            if (StringUtils.isBlank(password)) {
                throw new RuntimeException("密码不能为空");
            }
            //用户名验证
            if (StringUtils.isBlank(updateAndSaveDto.getFirst_name())) {
                throw new RuntimeException("用户名不能为空");
            } else {
                User userByName = userService.findUserByName(updateAndSaveDto.getFirst_name());
                if (userByName != null) {
                    throw new RuntimeException("用户名不能重复");
                } else {
                    user.setFirstName(updateAndSaveDto.getFirst_name());
                }
            }
            //状态处理
            if (StringUtils.isBlank(updateAndSaveDto.getUser_status()) || updateAndSaveDto.getUser_status().equals(GlobalConstant.enableTrue)) {
                tSysRoleUser.setUserStatus(GlobalConstant.enableTrue);
            } else {
                tSysRoleUser.setUserStatus(GlobalConstant.enableFalse);
            }
            User saveUser = saveUser(user, false, req);
            if (saveUser.getId() != null) {
                tSysRoleUser.setUserId(saveUser.getId().toString());
            } else {
                throw new RuntimeException("新增出错请重试");
            }
            if (updateAndSaveDto.getRole_id() != null) {
                tSysRoleUser.setRoleId(updateAndSaveDto.getRole_id());
            } else {
                tSysRoleUser.setRoleId(GlobalConstant.ROLE_DEFAULT_ID);
            }
            //保存角色用户关系
            roleService.saveRoleUser(tSysRoleUser);
            //添加用户详情
            userService.updateUserDetailList(saveUser.getId().toString(), updateAndSaveDto.getTSysUserDetailList());
            resetPas(req, password, String.valueOf(saveUser.getId()));
        } else {

            updateAndSaveDto.setUpdated_name(currentUser.getName());
            updateAndSaveDto.setUpdated_time(new Date());
            userService.update(updateAndSaveDto);
            //添加用户详情
            userService.updateUserDetailList(updateAndSaveDto.getUser_id(), updateAndSaveDto.getTSysUserDetailList());
        }
        return ResultUtil.success();
    }

    @ApiOperation("返回用户状态")
    @GetMapping("/user/getUserStatus")
    public ResponseResult<UserStatusVo> getUserStatus() throws Exception {
        SecurityUser currentUser = getCurrentUser();
        UserStatusVo userVo = new UserStatusVo();
        String useId = currentUser.getId().getId().toString();
        userVo.setUseId(useId);
        userVo.setUsername(currentUser.getName());
        userVo.setName(currentUser.getFirstName());
        userService.getUserStatus(userVo);
        UserClassVo userClass = tSysClassService.getUserClass(currentUser.getId().getId().toString());
        userVo.setUserClassVo(userClass);
        log.info(currentUser.getName() + "登陆成功！");
        userVo.setTSysRole(roleService.getByUserId(useId));
        String cwkid =userService.getUserCurrentCwkid(currentUser.getId().getId().toString());//登录的产线
        String pkOrg = userService.getUserCurrentPkOrg(currentUser.getId().getId().toString());//登录的基地
        List<NcWarehouse> ncWarehouses = userService.findNcWarehouseByUserIdAndPkOrgAndWorkline(currentUser.getId().getId().toString(),pkOrg,cwkid);
        if(ncWarehouses!=null&& !ncWarehouses.isEmpty()) {
            String wid = ncWarehouses.get(0).getPkStordoc();
            String wName = ncWarehouses.get(0).getName();
            userVo.setNcWarehouseId(wid);
            userVo.setNcWarehouseName(wName);
        }
        return ResultUtil.success(userVo);
    }


    /**
     * 重置密码
     *
     * @param req
     * @param password
     * @param userId
     * @throws ThingsboardException
     */
    public void resetPas(HttpServletRequest req, String password, String userId) throws ThingsboardException {
        //发起设置密码
        String activationLink = getActivationLink(userId, req);
        log.info("发起密码链接：" + activationLink);
        //密码保存
        String encodedPassword = passwordEncoder.encode(password);
        int i = activationLink.indexOf("activateToken=");
        String activateToken = activationLink.substring(i + 14);
        UserCredentials credentials = userService.activateUserCredentials(TenantId.SYS_TENANT_ID, activateToken, encodedPassword);
    }

    @ApiOperation("返回用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false)
    })
    @PostMapping("/user/pageUser")
    public ResponseResult<PageVo<UserVo>> pageUser(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                   @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                   @RequestBody PageUserDto pageUserDto) throws JsonProcessingException {
        PageVo<UserVo> users = userService.pageUser(current, size, pageUserDto);
        return ResultUtil.success(users);
    }

    @ApiOperation("返回用户列表(all)")
    @GetMapping("/user/pageUserByUserNameAndName")
    public ResponseResult<List<UserVo>> pageUserByUserNameAndName(
            @RequestParam("nameAndUsername") String nameAndUsername) throws JsonProcessingException {
        List<UserVo> users = userService.pageUserByUserNameAndName(nameAndUsername);
        return ResultUtil.success(users);
    }

    @ApiOperation("返回用户详细信息")
    @ApiImplicitParam(name = "userId", value = "用户id", readOnly = true)
    @GetMapping("/user/listUserDetail")
    public ResponseResult<List<TSysUserDetail>> listUserDetail(@RequestParam("userId") String userId) {
        List<TSysUserDetail> list = userService.listUserDetail(userId);
        return ResultUtil.success(list);
    }

    @ApiOperation("重置密码")
    @GetMapping("/user/resetPassword")
    public ResponseResult resetPassword(@RequestParam("userId") String userId,
                                        @RequestParam("password") String password) throws ThingsboardException, JsonProcessingException {

        String encode = passwordEncoder.encode(password);
        userService.resetPassword(userId, encode);
        return ResultUtil.success();
    }

    @ApiOperation("修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldPas", value = "旧密码", required = true),
            @ApiImplicitParam(name = "newPas", value = "新密码", required = true)
    })
    @GetMapping("/user/updatePas")
    public ResponseResult updatePass(@RequestParam("oldPas") String password,
                                     @RequestParam("newPas") String newPas) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        if (newPas.length() < 2) {
            int code = 1003;
            String msg = "新密码格式错误";
            return ResultUtil.error(code, msg);
        }
        UserCredentials userCredentials = userService.findUserCredentialsByUserId(GlobalConstant.createUser().getTenantId(), currentUser.getId());
        if (!passwordEncoder.matches(password, userCredentials.getPassword())) {
            int code = 1002;
            String msg = "原密码错误";
            return ResultUtil.error(code, msg);
        }
        userService.updatePas(userCredentials, passwordEncoder.encode(newPas));
        return ResultUtil.success();
    }

    @ApiOperation("禁用/启用控制接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true),
            @ApiImplicitParam(name = "enabled", value = "禁用标识 0：不可用 1：可用", required = true)
    })
    @GetMapping("/user/isEnabled")
    public ResponseResult isEnabled(@RequestParam("userId") String userId
            , @RequestParam("enabled") Integer enabled,
                                    HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        UpdateAndSaveDto updateAndSaveDto = new UpdateAndSaveDto();
        updateAndSaveDto.setUser_id(userId);
        updateAndSaveDto.setUser_status(enabled == 0 ? GlobalConstant.enableFalse : GlobalConstant.enableTrue);
        this.update(updateAndSaveDto);
        return ResultUtil.success();
    }

    @ApiOperation("删除用户接口")
    @Transactional
    @GetMapping("/user/delete")
    public ResponseResult delete(@RequestParam("userId") String userId) throws Exception {
        userService.deleteUser(GlobalConstant.createUser().getTenantId(), new UserId(UUID.fromString(userId)));
        userService.deleteRoleUser(userId);
        userService.deleteUserDetail(userId);
        return ResultUtil.success();
    }

    @ApiOperation("用户设置角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true),
            @ApiImplicitParam(name = "roleId", value = "角色id", required = true)
    })
    @GetMapping("/user/setRole")
    public ResponseResult setRole(@RequestParam("userId") String userId,
                                  @RequestParam("roleId") Integer roleId,
                                  HttpServletResponse response,
                                  HttpServletRequest request) throws Exception {
        UpdateAndSaveDto updateAndSaveDto = new UpdateAndSaveDto();
        updateAndSaveDto.setUser_id(userId);
        updateAndSaveDto.setRole_id(roleId);
        this.update(updateAndSaveDto);
        return ResultUtil.success();
    }

    @ApiOperation("根据用户名获取基地列表（无认证）")
    @GetMapping("/noauth/user/orgList")
    public ResponseResult<List<NcOrganization>> getBaseListByUserName(@RequestParam("username") String username) {
        List<NcOrganization> list = userService.findBaseListByUserName(username);
        return ResultUtil.success(list);
    }

    @ApiOperation("根据用户名和基地ID获取产线列表（无认证）")
    @GetMapping("/noauth/user/worklineList")
    public ResponseResult<List<NcWorkline>> getWorkLineListByUserNameAndPkOrg(
            @RequestParam("username") String username,
            @RequestParam("pkOrg") String pkOrg) {
        List<NcWorkline> list = userService.findLineListByUserNameAndPkOrg(username, pkOrg);
        return ResultUtil.success(list);
    }

    @ApiOperation("切换当前用户的基地和产线")
    @PostMapping("/user/switchOrgLine")
    public ResponseResult<Void> switchOrgLine(@RequestParam("pkOrg") String pkOrg,
                                              @RequestParam("cwkid") String cwkid) throws ThingsboardException {
        SecurityUser securityUser = getCurrentUser();
        if(securityUser != null&& securityUser.getPkOrg()!=null&&securityUser.getCwkid()!=null) {
            // 保存基地和产线到redis
            userService.saveUserCurrentOrgLine(securityUser.getId().toString(), pkOrg, cwkid);
        }

        return ResultUtil.success();
    }
    @ApiOperation("获取当前用户的基地和产线")
    @GetMapping("/user/currentOrgLine")
    public ResponseResult<Map<String, String>> getCurrentOrgLine() throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        String userId = user.getId().toString();
        String pkOrg = userService.getUserCurrentPkOrg(userId);
        String cwkid = userService.getUserCurrentCwkid(userId);
        Map<String, String> result = new HashMap<>();
        result.put("pkOrg", pkOrg);
        result.put("cwkid", cwkid);
        return ResultUtil.success(result);
    }

    public ResponseResult update(@RequestBody UpdateAndSaveDto updateAndSaveDto) throws ThingsboardException, JsonProcessingException {
        SecurityUser currentUser = getCurrentUser();
        currentUser.setAuthority(Authority.TENANT_ADMIN);
        if (StringUtils.isBlank(updateAndSaveDto.getUser_id())) {
            // 禁止为空，报错处理
            return ResultUtil.error("更新用户，用户id不能为空");
        } else {
            updateAndSaveDto.setUpdated_name(currentUser.getName());
            updateAndSaveDto.setUpdated_time(new Date());
            userService.update(updateAndSaveDto);
        }
        return ResultUtil.success();
    }
}
