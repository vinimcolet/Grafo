public class Edge {
    // Identificador
    private String name;
    // Extremidades (v,w) v -> w se direcionado
    private Node node1;
    private Node node2;
    // Custo/Peso
    private int weight;

    public Edge(String name, Node node1, Node node2, int weight) {
        this.name = name;
        this.node1 = node1;
        this.node2 = node2;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public Node getNode1() {
        return node1;
    }

    public Node getNode2() {
        return node2;
    }

    public int getWeight() {
        return weight;
    }
}