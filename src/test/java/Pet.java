
public class Pet {
    // Estamos defindo uma classe chamada Pet para guardar a estrutura de dados
    // sobre os animais (pets)
    public int id;

    public class Category {
        public int id;
        public String name;
    }

    public Category category;
    public String name;
    public String[] photoUrls;

    public class Tag {
        public int id;
        public String name;
    }

    public Tag tags[];
    public String status;
}