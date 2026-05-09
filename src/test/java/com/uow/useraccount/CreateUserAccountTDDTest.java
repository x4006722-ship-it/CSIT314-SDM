package com.uow.useraccount;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class CreateUserAccountTDDTest {

    @Test
    public void test_create_account_success() {
        System.out.println("TESTING: create account success with valid data");
        CreateUserAccountController controller = new CreateUserAccountController(new FakeUserAccount(false, true));

        Map<String, Object> validData = new HashMap<>();
        validData.put("username", "alice");
        validData.put("password", "password123");
        validData.put("fullName", "Alice Tan");
        validData.put("email", "alice@example.com");
        validData.put("phoneNumber", "91234567");
        validData.put("accountStatus", "Active");
        validData.put("profileId", 1);

        boolean result = controller.createAccount(validData);
        Assert.assertEquals(true, result);

        System.out.println("PASSED");
    }

    @Test
    public void test_create_account_empty_fields_fails() {
        System.out.println("TESTING: create account fails with empty fields");
        CreateUserAccountController controller = new CreateUserAccountController(new FakeUserAccount(false, false));

        Map<String, Object> emptyData = new HashMap<>();
        emptyData.put("username", "");
        emptyData.put("password", "");
        emptyData.put("fullName", "");
        emptyData.put("email", "");
        emptyData.put("phoneNumber", "");
        emptyData.put("accountStatus", "");
        emptyData.put("profileId", 0);

        boolean result = controller.createAccount(emptyData);
        Assert.assertFalse(result);

        System.out.println("PASSED");
    }

    @Test
    public void test_create_account_duplicate_username_fails() {
        System.out.println("TESTING: create account fails with duplicate username");
        CreateUserAccountController controller = new CreateUserAccountController(new FakeUserAccount(true, true));

        Map<String, Object> duplicateData = new HashMap<>();
        duplicateData.put("username", "existing_user");
        duplicateData.put("password", "password123");
        duplicateData.put("fullName", "Existing User");
        duplicateData.put("email", "existing@example.com");
        duplicateData.put("phoneNumber", "90000000");
        duplicateData.put("accountStatus", "Active");
        duplicateData.put("profileId", 1);

        boolean result = controller.createAccount(duplicateData);
        Assert.assertFalse(result);

        System.out.println("PASSED");
    }

    private static class FakeUserAccount extends UserAccount {
        private final boolean hasDuplicate;
        private final boolean saveResult;

        FakeUserAccount(boolean hasDuplicate, boolean saveResult) {
            this.hasDuplicate = hasDuplicate;
            this.saveResult = saveResult;
        }

        @Override
        public Object getSearchAccount(Object searchData) {
            return hasDuplicate ? List.of(Map.of("username", "existing_user")) : List.of();
        }

        @Override
        public boolean saveCreateAccount(Object newAccountData) {
            return saveResult;
        }
    }
}
