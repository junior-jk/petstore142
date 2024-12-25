import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Ativa a ordenação
public class EntidadeStore {

    // 2.1 atributos
    static String ct = "application/json"; // content-type
    static String uriStore = "https://petstore.swagger.io/v2/store/order";
    static int petId = 325276;
    int orderId = 10;
    String quantity = "3";
    String shipDate = "";
    String status = "placed";
    String complete = "true";

    // 2.2 funções e métodos
    // 2.2.1 funções e métodos comuns / uteis

    // Função de leitura de Json
    public static String lerAqrquiviJson(String arquivoJson) throws IOException {
        return new String(Files.readAllBytes(Paths.get(arquivoJson)));
    }

    @Test
    @Order(1)
    public void testPostStore() throws IOException {
        String jsonBody = lerAqrquiviJson("src/test/resources/json/store1.json");

        given()
                .contentType(ct)
                .log().all()
                .body(jsonBody)
                .when()
                .post(uriStore)
                .then()
                .log().all()
                .statusCode(200) // Verifique se o status de criação é 200
                .body("id", is(orderId)) // Valida o campo "id"
                .body("petId", is(petId)) // Valida o campo "petId"
                .body("quantity", is(3)) // Valida o campo "quantity"
                .body("shipDate",
                        anyOf(nullValue(),
                                matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}(Z|[+-]\\d{4})"))) // shipDate
                .body("status", is("placed")) // Valida o campo "status"
                .body("complete", is(true)); // Valida o campo "complete"
    }

    @Test
    @Order(2)
    public void testGetStoreOrder() {

        // Realiza o GET para obter a ordem da loja
        given()
                .contentType(ct)
                .log().all()
                .header("Authorization", "Bearer " + TestUser.testLogin()) // Adiciona o token no cabeçalho
                .when()
                .get(uriStore + "/" + orderId)
                .then()
                .statusCode(200)
                .body("id", is(orderId)) // Valida o ID
                .body("petId", is(petId)) // Verifica se o petId é o esperado
                .body("quantity", is(3)) // Verifica a quantidade
                .body("shipDate", anyOf(nullValue(),
                        matchesRegex("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}[+-]\\d{4}")))
                .body("status", is("placed")) // Verifica o status
                .body("complete", is(true)); // Verifica se a ordem foi completada
    }

    @Test
    @Order(3)
    public void testDeleteStoreOrder() {

        given()// Dado

                .contentType(ct)
                .log().all()
                // Executa
                .when()
                .delete(uriStore + "/" + orderId)
                // Valida
                .then()// Então
                .log().all()
                .statusCode(200) // se comunicou e processou
                .body("code", is(200)) // se apagou
                .body("type", is("unknown")) // desconhecido
                .body("message", is(String.valueOf(orderId))); // e o petid do animal
    }

    @ParameterizedTest
    @Order(4)
    @CsvFileSource(resources = "/csv/storeMassa.csv", numLinesToSkip = 1, delimiter = ',')
    public void testPostStoreDDT(

            int id,
            int petId,
            String quantity,
            String status1,
            String status2,
            String status3,
            String complete)

    { // fim dos parametros
      // inicio do código do método testPostPetDDT

        String requestBody = String.format("{\n" +
                "    \"id\": %d,\n" +
                "    \"petId\": %d,\n" +
                "    \"quantity\": %s,\n" +
                "    \"status\": \"%s\",\n" +
                "    \"complete\": %s\n" +
                "}", id, petId, quantity, status1, complete);

        // Realiza o POST para criar a ordem da loja
        given()
                .contentType("application/json") // Tipo de conteúdo JSON
                .body(requestBody) // Corpo da requisição com os dados dinâmicos
                .log().all()
                .when()
                .post(uriStore) // Endpoint para criar a ordem
                .then()
                .statusCode(200) // Verifica se o status é 200 (OK)
                .body("id", is(id)) // Verifica o ID retornado
                .body("petId", is(petId)) // Verifica o petId retornado
                .body("quantity", is(Integer.parseInt(quantity))) // Verifica a quantidade retornada
                .body("status", anyOf(equalTo(status1), is(status2), is(status3)))
                .body("complete", is(Boolean.parseBoolean(complete))); // Verifica se o pedido foi completado
    }
}
