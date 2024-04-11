package user;

import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static config.Config.YOU_NOT_AUTH;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class UserDataUpdateTest {

    private String userAccessToken;

    User user = new User();

    @Before
    public void tearUp() throws Exception {
        //создание пользователя
        user = User.getRandomUser();
        //создание учетной записи пользователя
        userAccessToken = UserSpec.getResponseCreateUser(user,200).accessToken;
    }

    //удаление учетной записи пользователя
    @After
    public void tearDown() throws Exception {
        UserSpec.getResponseUserDeleted(userAccessToken, 202);
    }

    @Test
    @DisplayName("Тест успешного изменения данных (пароля) авторизованного пользователя")
    public void changePasswordOfTheAuthorizationUser() throws JsonProcessingException {
        User createdUser = new User(user.getEmail(), user.getPassword());
        //авторизация пользователя
        UserSpec.getResponseUserAuthorization(createdUser, 200);
        //изменение пароля пользователя
        String updatedPassword = "New" + user.getPassword();
        User updatedUser = new User(user.getEmail(), updatedPassword, user.getName());
        //изменение данных пользователя
        UserSpec.getResponseUpdateUserData(updatedUser, userAccessToken, 200);
        //авторизация с измененным паролем
        userAccessToken = UserSpec.getResponseUserAuthorization(updatedUser, 200).accessToken;
        assertThat(userAccessToken, notNullValue());
    }

    @Test
    @DisplayName("Тест успешного изменения данных (имени) авторизованного пользователя")
    public void successChangeNameOfTheAuthorizationUser() throws JsonProcessingException {
        User createdUser = new User(user.getEmail(), user.getPassword());
        //авторизация пользователя
        userAccessToken = UserSpec.getResponseUserAuthorization(createdUser, 200).accessToken;
        //изменение имени пользователя
        String updatedName = "New" + user.getName();
        User updatedUser = new User(user.getEmail(), user.getPassword(), updatedName);
        //изменение данных пользователя
        UserSpec.getResponseUpdateUserData(updatedUser, userAccessToken, 200)
                .body("user.name",equalTo(updatedName));
    }

    @Test
    @DisplayName("Тест успешного изменения данных (email) авторизованного пользователя")
    public void successChangeEmailOfTheAuthorizationUser() throws JsonProcessingException {
        User createdUser = new User(user.getEmail(), user.getPassword());
        //авторизация пользователя
        userAccessToken = UserSpec.getResponseUserAuthorization(createdUser, 200).accessToken;
        //изменение email пользователя
        String updatedEmail = "New" + user.getEmail();
        User updatedUser = new User(updatedEmail, user.getPassword(), user.getName());
        //изменение данных пользователя
        UserSpec.getResponseUpdateUserData(updatedUser, userAccessToken, 200)
                .body("user.email",equalTo(updatedEmail.toLowerCase()));
    }

    @Test
    @DisplayName("Тест неуспешного изменения данных (пароля) неавторизованного пользователя")
    public void failChangePasswordOfTheUnauthorizationUser() throws JsonProcessingException {
        //изменение пароля пользователя
        String updatedPassword = "New" + user.getPassword();
        User updatedUser = new User(user.getEmail(), updatedPassword, user.getName());
        //изменение данных пользователя
        UserSpec.getResponseUpdateUserData(updatedUser, "", 401)
                .body("message",equalTo(YOU_NOT_AUTH));
    }

    @Test
    @DisplayName("Тест неуспешного изменения данных (имени) неавторизованного пользователя")
    public void failChangeNameOfTheUnauthorizationUser() throws JsonProcessingException {
        //изменение имени пользователя
        String updatedName = "New" + user.getName();
        User updatedUser = new User(user.getEmail(), user.getPassword(), updatedName);
        //изменение данных пользователя
        UserSpec.getResponseUpdateUserData(updatedUser, "", 401)
                .body("message",equalTo(YOU_NOT_AUTH));
    }

    @Test
    @DisplayName("Тест неуспешного изменения данных (email) неавторизованного пользователя")
    public void failChangeEmailOfTheUnauthorizationUser() throws JsonProcessingException {
        //изменение email пользователя
        String updatedEmail = "New" + user.getEmail();
        User updatedUser = new User(updatedEmail, user.getPassword(), user.getName());
        //изменение данных пользователя
        UserSpec.getResponseUpdateUserData(updatedUser, "", 401)
                .body("message",equalTo(YOU_NOT_AUTH));
    }
}