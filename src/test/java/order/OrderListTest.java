package order;

import user.User;
import user.UserSpec;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

import static config.Config.YOU_NOT_AUTH;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;

public class OrderListTest {
    private String userAccessToken;
    private int numberOfOrders;

    User user = new User();

    @Before
    public void tearUp() throws Exception {
        //создание пользователя
        user = User.getRandomUser();
        userAccessToken = UserSpec.getResponseCreateUser(user,200).accessToken;
        //количество заказов пользователя
        numberOfOrders = 4;
        //создание списка заказов пользователя
        OrderSpec.createListOfOrders(user, numberOfOrders);
    }

    @After //удаление учетной записи пользователя
    public void tearDown() throws Exception {
        UserSpec.getResponseUserDeleted(userAccessToken, 202);
    }

    @Test
    @DisplayName("Проверка получения списка заказов авторизованного пользователя")
    public void getOfOrdersListFromAuthorizedUser() throws JsonProcessingException {
        //авторизацию пользователя
        userAccessToken = UserSpec.getResponseUserAuthorization(user, 200).accessToken;
        //получения списка заказов пользователя
        ArrayList<Integer> orderNumber =
                new ArrayList<>(OrderSpec.getOrderList(userAccessToken, 200)
                .extract()
                .path("orders.number"));
        assertEquals(numberOfOrders, orderNumber.size());
    }

    @Test
    @DisplayName("Проверка НЕполучения списка заказов НЕавторизованного пользователя")
    public void getOfOrdersListFromUnauthorizedUser() throws JsonProcessingException {
        //получения списка заказов пользователя
        OrderSpec.getOrderList("", 401)
                .body("message",equalTo(YOU_NOT_AUTH));


    }
}
