package com.woodcore.backend.bankingservice.utils;

public class MethodSecurityConstants {

    public static final String ADMIN = "hasRole('ROLE_ADMIN')";

    public static final String USER = "hasRole('ROLE_USER')";
}
