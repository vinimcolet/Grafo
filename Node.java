import java.awt.geom.Ellipse2D;

public class Node {
    // Identificador
    private String name;
    private Ellipse2D ellipse2D;

    public Node(String nome, Ellipse2D ellipse2D){
        this.name = nome;
        this.ellipse2D=ellipse2D;
    }

    public String getName(){
        return name;
    }

    public Ellipse2D getEllipse2D(){
        return ellipse2D;
    }

}
