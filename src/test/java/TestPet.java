import static io.restassured.RestAssured.given; // funçao given
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import com.google.gson.Gson;

// 2 -classe
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Ativa a ordenação

public class TestPet {

    // 2.1 atributos
    static String ct = "application/json"; // content-type
    static String uriPet = "https://petstore.swagger.io/v2/pet";
    static int petId = 32527601;
    String petName = "valente";
    String categoryName = "cachorro";
    String tagName = "vacinado";
    String[] status = { "available", "sold" };

    // 2.2 funções e métodos
    // 2.2.1 funções e métodos comuns / uteis

    // Função de leitura de Json
    public static String lerAqrquiviJson(String arquivoJson) throws IOException {
        return new String(Files.readAllBytes(Paths.get(arquivoJson)));
    }

    // 2.2.2 métodos de teste
    @Test
    @Order(1)
    public void testePostPet() throws IOException {
        // configura

        // CARREGAR OS DADOS DO ARQUIVO JSON DO PET

        String jsonBody = lerAqrquiviJson("src/test/resources/json/pet1.json");

        // comeca o teste via Rest --Assured

        given() // Dado que
                .contentType(ct) // o tipo de conteúdo é
                .log().all() // mostre tudo na ida
                .body(jsonBody) // envie o corpo da requisição

                // Executa
                .when() // Quando
                .post(uriPet) // Chamamos o endpoint fazendo post

                // Valida
                .then() // Então
                .log().all() // Mostre tudo na volta
                .statusCode(200) // verifica se o status code é 200
                .body("name", is(petName)) // verifica p nome do animal
                .body("id", is(petId)) // verifica o codigo do animal
                .body("category.name", is(categoryName)) // se e cachorro
                .body("tags[0].name", is(tagName)) // se e vacinado
        ;// fim do given

    }

    @Test
    @Order(2)
    public void testGetPet() {
        // Configura
        // Entrada e Saidas definidas no nível da classe

        given()
                .contentType(ct)
                .log().all()
                .header("", "api_key: " + TestUser.testLogin())
                // quando é get ou delete não tem body
                // Executa
                .when()
                .get(uriPet + "/" + petId) // montar o endpoint da URI/<petId>
                // Valida
                .then()
                .log().all()
                .statusCode(200)
                .body("name", is(petName)) // verifica se o nome é valente
                .body("id", is(petId)) // verifique o código do pet
                .body("category.name", is(categoryName)) // se é cachorro
                .body("tags[0].name", is(tagName)) // se está vacinado

        ; // fim do given
    }

    @Test
    @Order(3)
    public void testPutPet() throws IOException {
        // Configura
        String jsonBody = lerAqrquiviJson("src/test/resources/json/pet2.json");

        given()
                .contentType(ct)
                .log().all()
                .body(jsonBody)
                // Executa
                .when()
                .put(uriPet)
                // Valida
                .then()
                .log().all()
                .statusCode(200)
                .body("name", is(petName)) // verifica se o nome é Snoopy
                .body("id", is(petId)) // verifique o código do pet
                .body("category.name", is(categoryName)) // se é cachorro
                .body("tags[0].name", is(tagName)) // se está vacinado
                .body("status", is(status[1])) // status do pet na loja
        ;
    }

    @Test
    @Order(4)
    public void testDeletePet() {
        // Configura --> Dados de entrada e saída no começo da Classe

        given()// Dado
                .contentType(ct)
                .log().all()
                // Executa
                .when()
                .delete(uriPet + "/" + petId)
                // Valida
                .then()// Então
                .log().all()
                .statusCode(200) // se comunicou e processou
                .body("code", is(200)) // se apagou
                .body("type", is("unknown")) // desconhecido
                .body("message", is(String.valueOf(petId))); // e o petid do animal
    }

    // Data Driven Testing (DDT) / Teste Direcionado por Dados / Teste com Massa
    // Teste com Json parametrizado

    @ParameterizedTest
    @Order(5)
    @CsvFileSource(resources = "/csv/petMassa142.csv", numLinesToSkip = 1, delimiter = ',')
    public void testPostPetDDT(
            int petId,
            String petName,
            int catId,
            String catName,
            String status1,
            String status2

    ) // fim dos parametros
    { // inicio do código do método testPostPetDDT

        // Criar a classe pet para receber os dados do csv

        Pet pet = new Pet(); // instancia a classe User
        Pet.Category category = pet.new Category(); // instancia a subclasse Category
        Pet.Tag[] tags = new Pet.Tag[2]; // instancia a subclasse Tag
        tags[0] = pet.new Tag();
        tags[1] = pet.new Tag();

        pet.id = petId;
        pet.category = category; // associar a pet.category com a subclasse category
        pet.category.id = catId;
        pet.category.name = catName;
        pet.name = petName;
        // pet.photoUrls não precisa ser incluido porque será vazio
        pet.tags = tags; // associa a pet.tags com a subclasse tags
        pet.tags[0].id = 9;
        pet.tags[0].name = "vacinado";
        pet.tags[1].id = 8;
        pet.tags[1].name = "vermifugado";
        pet.status = status1; // status inicial usado no Post = "available"

        // Criar um Json para o Body a ser enviado a partir da classe Pet e do CSV
        Gson gson = new Gson(); // instancia a classe Gson como o objeto gson
        String jsonBody = gson.toJson(pet);

        given()
                .contentType(ct)
                .log().all()
                .body(jsonBody)
                .when()
                .post(uriPet)
                .then()
                .log().all()
                .statusCode(200)
                .body("id", is(petId))
                .body("name", is(petName))
                .body("category.id", is(catId))
                .body("category.name", is(catName))
                .body("status", is(status1)) // inicial do Post

        ;

    }

}