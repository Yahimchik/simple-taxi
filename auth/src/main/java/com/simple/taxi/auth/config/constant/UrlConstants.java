package com.simple.taxi.auth.config.constant;

public class UrlConstants {

    // Base
    public static final String SECURITY_REQUIREMENT = "Bearer Authentication";
    public static final String BASE_API_URL = "/api/v1/";
    public static final String ID = "/{id}";

    // ActivationController
    public static final String ACTIVATION_CONTROLLER = BASE_API_URL + "activation";
    public static final String DEACTIVATE = "/deactivate";
    public static final String REACTIVATE = "/reactivate";
    public static final String RESEND_TOKEN = "/resend-token";
    public static final String VERIFY = "/verify";

    // AuthController
    public static final String AUTH_CONTROLLER = BASE_API_URL + "auth";
    public static final String CLIENT_ID = "/client-id";
    public static final String LOGIN = "/login";
    public static final String LOGIN_SOCIAL = "/login/social";
    public static final String LOGOUT = "/logout";
    public static final String LOGOUT_ALL = "/logout-all";

    // DeviceController
    public static final String DEVICE_CONTROLLER = BASE_API_URL + "devices";
    public static final String USER_DEVICES = "/user-devices";
    public static final String USER_DEVICES_ACTIVE = "/user-devices-active";

    // OtpController
    public static final String OTP_CONTROLLER = BASE_API_URL + "otp";
    public static final String DISABLE_2FA = "/disable-2fa";
    public static final String ENABLE_2FA = "/enable-2fa";
    public static final String REQUEST_OTP = "/request-otp";
    public static final String VERIFY_OTP = "/verify-otp";

    // PasswordController
    public static final String PASSWORD_CONTROLLER = BASE_API_URL + "passwords";
    public static final String CHANGE_PASSWORD = "/change-password";
    public static final String FORGOT = "/forgot-password";
    public static final String RESET_PASSWORD = "/reset-password";

    // RoleController
    public static final String ROLE_CONTROLLER = BASE_API_URL + "roles";
    public static final String ASSIGN_ROLES = "/assign-roles";
    public static final String GET_ROLES = "/roles";
    public static final String REMOVE_ROLE = "/remove-role";

    // TokenController
    public static final String TOKEN_CONTROLLER = BASE_API_URL + "tokens";
    public static final String ACTIVE_TOKEN = "/active-token";
    public static final String CHECK_TOKEN = "/check-token";
    public static final String RECREATE_TOKEN = "/recreate-token";

    // UserController
    public static final String USER_CONTROLLER = BASE_API_URL + "users";
    public static final String UPDATE_USER = "/update";
    public static final String USER_CURRENT = "/current";
    public static final String USER_REGISTER = "/register";

    // LOGIN_ATTEMPTS_CONTROLLER
    public static final String LOGIN_ATTEMPTS_CONTROLLER = BASE_API_URL + "login-attempts";
    public static final String BLOCK_TIME = "/block-time";
    public static final String CLEAR = "/clear";
    public static final String STATUS = "/status";

    // Swagger / API Docs
    public static final String URL_TO_API_DOC = "/v3/api-docs/**";
    public static final String URL_TO_API_SWAGGER = "/swagger-ui/**";
}