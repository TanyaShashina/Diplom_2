package user;

import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Test;

import static config.Config.FIELD_BAD;
import static config.Config.USER_EXISIST;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class UserCreateTest {

    private String userAccessToken;
    private boolean userCreateSuccess;

    User user = new User();

    //удаление учетной записи пользователя
    @After
    public void deleteAfterUser() throws Exception {
        if (userCreateSuccess) {
            UserSpec.getResponseUserDeleted(userAccessToken, 202);
        }
    }

    @Test
    @DisplayName("Тест успешного создания учетной записи пользователя")
    public void successfulCreateUser() throws JsonProcessingException {
        //создание пользователя
        user = User.getRandomUser();
        //создание учетки пользователя
        UserSpec response = UserSpec.getResponseCreateUser(user,200);
        userAccessToken = response.accessToken;
        userCreateSuccess = response.success;
        assertThat(userAccessToken, notNullValue());
        assertTrue(userCreateSuccess);
    }

    @Test
    @DisplayName("Тест неуспешного создания учетной записи пользователя без пароля")
    public void failCreateUserWithOutPassword() throws JsonProcessingException {
        //создание пользователя без пароля
        user = User.getRandomUserWithoutPassword();
        //создание учетки пользователя
        UserSpec response = UserSpec.getResponseCreateUser(user, 403);
        assertFalse(response.success);
        assertEquals(FIELD_BAD,response.message);
    }

    @Test
    @DisplayName("Тест неуспешного создания учетной записи пользователя без имени")
    public void failCreateUserWithOutName() throws JsonProcessingException {
        //создание пользователя без имени
        user = User.getRandomUserWithoutName();
        //создание "учетки" пользователя
        UserSpec response = UserSpec.getResponseCreateUser(user, 403);
        assertFalse(response.success);
        assertEquals(FIELD_BAD,response.message);
    }

    @Test
    @DisplayName("Тест неуспешного создания учетной записи пользователя без email")
    public void failCreateUserWithOutEmail() throws JsonProcessingException {
        //создание пользователя без email
        user = User.getRandomUserWithoutEmail();
        //создание учетки пользователя
        UserSpec response = UserSpec.getResponseCreateUser(user, 403);
        assertFalse(response.success);
        assertEquals(FIELD_BAD,response.message);
    }

    @Test
    @DisplayName("Тест неуспешного создания учетной записи " +
            "пользователя который уже зарегистрирован (с повторяющимся email)")
    public void failCreateCourierRecurringEmail() throws JsonProcessingException {
        //создание пользователя
        user = User.getRandomUser();
        //создание учетки пользователя
        UserSpec initResponse = UserSpec.getResponseCreateUser(user,200);
        userAccessToken = initResponse.accessToken;
        userCreateSuccess = initResponse.success;
        //создание учетки пользователя который уже зарегистрирован
        UserSpec response = UserSpec.getResponseCreateUser(user, 403);
        assertFalse(response.success);
        assertEquals(USER_EXISIST,response.message);
    }
}