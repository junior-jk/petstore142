
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

public class TestUser {
    static String ct = "application/json";
    static String uriUser = "https://petstore.swagger.io/v2/user";
    static String token;

    @Test
    public static String testLogin() {
        // Configura
        String username = "Rui ";
        String password = "teste123";

        String resultadoEsperado = "logged in user session:";

        Response resposta = (Response) given() // se chama (Response) casting
                .contentType(ct)
                .log().all()// tudo que vai
                // Executa
                .when()
                .get(uriUser + "/login?username=" + username + "&password=" + password)
                // Valida
                .then()
                .log().all()// volta do que mandou
                .statusCode(200)
                .body("code", is(200))
                .body("type", is("unknown"))
                .body("message", containsString(resultadoEsperado)) // Contém
                .body("message", hasLength(36)) // tamanho do campo message
                .extract();

        // extração
        token = resposta.jsonPath().getString("message").substring(23);
        System.out.println("Conteudo do Token: " + token);
        return token;
    }
}