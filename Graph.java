import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Stack;

public class Graph extends JFrame implements ActionListener {

    public static void main(String[] args) {
        Graph frame = new Graph();
        frame.setVisible(true);
    }

    // grafo G = (V, A)
    ArrayList<Node> Nodes = new ArrayList<>();
    ArrayList<Edge> Edges = new ArrayList<>();
    boolean isDirected = false;
    // controle dos elementos visuais
    private int radius = 50;
    private int maxRadius = 70;
    private int minRadius = 30;
    private int step = 10;
    // controle do nome dos novos vertices/arestas/arcos
    private int edgeCount = 0;
    private int nodeCount = 0;
    // arestas selecionadas para os algoritmos
    ArrayList<Edge> highlightedEdges;

    JPanel mainPanel;
    JPanel centerPanel;

    public Graph() {
        highlightedEdges = new ArrayList<>();
        mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        mainPanel.setLayout(new BorderLayout());
        createToolBar();
        createCenterPane();
        add(mainPanel);
        setTitle("Grafo");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void createToolBar() {
        var toolBar = new JToolBar("Ferramentas");
        toolBar.setFloatable(false);

        var button = new JButton("Add Node");
        button.addActionListener(this);
        //button.setActionCommand("Novo");
        toolBar.add(button);
        button = new JButton("Remove Node");
        button.addActionListener(this);
        toolBar.add(button);

        toolBar.add(Box.createHorizontalGlue());
        button = new JButton("Add Edge");
        button.addActionListener(this);
        
        toolBar.add(button);
        button = new JButton("Remove Edge");
        button.addActionListener(this);
        toolBar.add(button);

        toolBar.add(Box.createHorizontalGlue());

        button = new JButton("Directed");
        button.addActionListener(this);
        toolBar.add(button);

        toolBar.add(Box.createHorizontalGlue());

        button = new JButton("PRIM");
        button.addActionListener(this);
        toolBar.add(button);
        button = new JButton("BFS");
        button.addActionListener(this);
        toolBar.add(button);
        button = new JButton("DFS");
        button.addActionListener(this);
        toolBar.add(button);
        button = new JButton("Roy");
        button.addActionListener(this);
        toolBar.add(button);

        toolBar.add(Box.createHorizontalGlue());

        button = new JButton("Node Size+");
        button.addActionListener(this);
        toolBar.add(button);
        button = new JButton("Node Size-");
        button.addActionListener(this);
        toolBar.add(button);

        button = new JButton("G1");
        button.addActionListener(this);
        toolBar.add(button);

        button = new JButton("G2");
        button.addActionListener(this);
        toolBar.add(button);

        button = new JButton("Clear");
        button.addActionListener(this);
        toolBar.add(button);

        

        mainPanel.add(toolBar, BorderLayout.PAGE_START);
    }

    private void createCenterPane() {
        centerPanel = new MyPanel();
        JScrollPane scrollPane = new JScrollPane(centerPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        switch (s) {
            case "Add Node" -> addNode();
            case "Remove Node" -> removeNode();
            case "Add Edge" -> addEdge();
            case "Remove Edge" -> removeEdge();
            case "Directed" -> directed();
            case "PRIM" -> PRIM();
            case "BFS" -> BFS();
            case "DFS" -> DFS();
            case "Roy" -> Roy();
            case "Node Size+" -> increaseNodeSize();
            case "Node Size-" -> decreaseNodeSize();
            case "G1" -> g1();
            case "G2" -> g2();
            case "Clear" -> clear();
        }
    }

    public void addNode() {
        String name = (String) JOptionPane.showInputDialog(this, "Name: ", "Add Node", JOptionPane.PLAIN_MESSAGE, null, null, "Node" + nodeCount);
        if (name != null) {
            Node node = new Node(name, new Ellipse2D.Float(50 - (radius / 2), 50 - (radius / 2), radius, radius));
            Nodes.add(node);
            nodeCount++;
            centerPanel.validate();
            centerPanel.repaint();
        }
    }

    public void removeNode() {
        if (Nodes.size() == 0) return;
        JPanel panel = new JPanel(new FlowLayout());

        String[] choices = new String[Nodes.size()];
        for (int i = 0; i < Nodes.size(); i++) {
            choices[i] = String.format("%02d", i) + ": " + Nodes.get(i).getName();
        }

        panel.add(new JLabel("Select node: "));
        JComboBox<String> comboBoxNode = new JComboBox<>(choices);
        panel.add(comboBoxNode);

        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, panel, "Remove Node", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE)) {
            Node node = Nodes.get(comboBoxNode.getSelectedIndex());
            for (int i = Edges.size() - 1; i > -1; i--) {
                if (Edges.get(i).getNode1() == node || Edges.get(i).getNode2() == node) Edges.remove(i);
            }
            Nodes.remove(node);
            mainPanel.validate();
            mainPanel.repaint();
        }

    }

    public void addEdge() {
        if (Nodes.size() == 0) return;
        GridLayout layout = new GridLayout(4, 2);
        layout.setVgap(5);
        JPanel panel = new JPanel(layout);

        String[] choices = new String[Nodes.size()];
        for (int i = 0; i < Nodes.size(); i++) {
            choices[i] = String.format("%02d", i) + ": " + Nodes.get(i).getName();
        }

        panel.add(new JLabel("Name: "));
        JTextField name = new JTextField("Edge" + edgeCount);
        panel.add(name);

        panel.add(new JLabel("Select start node: "));
        JComboBox<String> comboBoxNode1 = new JComboBox<>(choices);
        panel.add(comboBoxNode1);

        panel.add(new JLabel("Select end node: "));
        JComboBox<String> comboBoxNode2 = new JComboBox<>(choices);
        panel.add(comboBoxNode2);

        panel.add(new JLabel("Value: "));
        JTextField value = new JTextField("0");
        panel.add(value);

        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, panel, "Add Edge", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE)) {
            Edge edge = new Edge(name.getText(), Nodes.get(comboBoxNode1.getSelectedIndex()), Nodes.get(comboBoxNode2.getSelectedIndex()), Integer.parseInt(value.getText()));
            Edges.add(edge);
            edgeCount++;
            mainPanel.validate();
            mainPanel.repaint();
        }
    }

    public void removeEdge() {
        if (Edges.size() == 0) return;
        JPanel panel = new JPanel(new FlowLayout());

        String[] choices = new String[Edges.size()];
        for (int i = 0; i < Edges.size(); i++) {
            choices[i] = String.format("%02d", i) + ": " + Edges.get(i).getName() + " | " + Edges.get(i).getNode1().getName() + " -> " + Edges.get(i).getNode2().getName();
        }

        panel.add(new JLabel("Select Edge: "));
        JComboBox<String> comboBoxEdges = new JComboBox<>(choices);
        panel.add(comboBoxEdges);

        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, panel, "Remove Edge", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE)) {
            Edges.remove(comboBoxEdges.getSelectedIndex());
            mainPanel.validate();
            mainPanel.repaint();
        }
    }

    public void directed() {
        isDirected = !isDirected;
        highlightedEdges.clear();
        mainPanel.validate();
        mainPanel.repaint();
    }

    public void PRIM() {
        if (Nodes.size() == 0 || isDirected) return;
        ArrayList<Node> visitedNodes = new ArrayList<>();
        highlightedEdges.clear();
        // Se existem nós no grafo e ele nao eh direcionado
        // Inicia pelo primeiro nó da lista
        visitedNodes.add(Nodes.get(0));
        // Enquanto existirem nós não visitados
     
        while (visitedNodes.size() != Nodes.size()) {
            Edge minEdge = null;
            Node nextNode = null;
            for (Edge edge : Edges) {
                if (visitedNodes.contains(edge.getNode1()) || visitedNodes.contains(edge.getNode2())) {
                    if (!visitedNodes.contains(edge.getNode1()) || !visitedNodes.contains(edge.getNode2())) {
                        if (minEdge == null) {
                            minEdge = edge;
                            if (visitedNodes.contains(edge.getNode2())) nextNode = edge.getNode1();
                            else nextNode = edge.getNode2();
                        } else if (edge.getWeight() < minEdge.getWeight()) {
                            minEdge = edge;
                            if (visitedNodes.contains(edge.getNode2())) nextNode = edge.getNode1();
                            else nextNode = edge.getNode2();
                        }
                    }

                }
            }
            visitedNodes.add(nextNode);
            if (minEdge != null) highlightedEdges.add(minEdge);
        }

        mainPanel.validate();
        mainPanel.repaint();

        int totalWeight = 0;
        for (Edge edge : highlightedEdges)
            totalWeight += edge.getWeight();
        JLabel label = new JLabel("<html><center>Total weight: " + totalWeight);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        JOptionPane.showMessageDialog(null, label, "Minimum Spanning Tree", JOptionPane.PLAIN_MESSAGE);
    }

    public void Roy() {
        if (!isDirected) return;
        // Numero de conjuntos
        int i = 0;
        highlightedEdges.clear();
        ArrayList<Node> V = new ArrayList<>(Nodes);
        ArrayList<Node> plusNodes;
        ArrayList<Node> minusNodes;
        // enquanto (V!=0) faça
        while (V.size() > 0) {
            plusNodes = new ArrayList<>();
            minusNodes = new ArrayList<>();
            // escolher e marcar um vertice v qq, v e v, com (+) e (-)
            plusNodes.add(V.get(0));
            minusNodes.add(V.get(0));
            int size;
            // marca com (+)
            do {
                size = plusNodes.size();
                for (Edge edge : Edges) {
                    if (!plusNodes.contains(edge.getNode1()) && plusNodes.contains(edge.getNode2()))
                        plusNodes.add(edge.getNode1());
                }
            } while (plusNodes.size() != size);
            // marca com (-)
            do {
                size = minusNodes.size();
                for (Edge edge : Edges) {
                    if (minusNodes.contains(edge.getNode1()) && !minusNodes.contains(edge.getNode2()))
                        minusNodes.add(edge.getNode2());
                }
            } while (minusNodes.size() != size);
            i += 1;
            // Si = vertices marcados com (+) e (-) simultaneamente
            ArrayList<Node> Si = new ArrayList<>();
            for (Node node : plusNodes) {
                if (minusNodes.contains(node)) Si.add(node);
            }
            // V = V/Si
            for (Node node : Si) {
                V.remove(node);
            }
            for (Edge edge : Edges) {
                if (edge.getNode1() != edge.getNode2() && Si.contains(edge.getNode1()) && Si.contains(edge.getNode2()))
                    highlightedEdges.add(edge);
            }
            // fim enquanto
        }
        mainPanel.validate();
        mainPanel.repaint();
    }

    public void BFS() {
        if (Nodes.size() == 0 || isDirected) return;

        // Seleciona o no de partida
        JPanel panel = new JPanel(new FlowLayout());
        String[] choices = new String[Nodes.size()];
        for (int i = 0; i < Nodes.size(); i++) {
            choices[i] = String.format("%02d", i) + ": " + Nodes.get(i).getName();
        }
        panel.add(new JLabel("Select starting node: "));
        JComboBox<String> comboBoxNode = new JComboBox<>(choices);
        panel.add(comboBoxNode);

        if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(this, panel, "BFS", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE)) {
            return;
        }
        // variaveis de controle
        ArrayList<Node> visitedNodes = new ArrayList<>();
        ArrayList<Edge> exploredEdges = new ArrayList<>();
        highlightedEdges.clear();
        int x = 0;
        int y = 0;

        // definir uma fila Q vazia
        Stack<Node> stack = new Stack<>();
        // escolher o vértice inicial v
        Node node = Nodes.get(comboBoxNode.getSelectedIndex());
        // marcar v
        visitedNodes.add(node);
        node.getEllipse2D().setFrame(50 - (radius / 2), 50 - (radius / 2), radius, radius);
        // inserir v em Q
        stack.add(node);

        // Enquanto Q ≠ Ø Faça
        while (!stack.isEmpty()) {
            x = 0;
            y++;
            node = stack.pop();
            //Para todo w Є Ƭ(v) Faça
            for (Edge edge : Edges) {
                // Se w é não marcado Então
                if (edge.getNode1() == node || edge.getNode2() == node)
                    if (!visitedNodes.contains(edge.getNode1()) || !visitedNodes.contains(edge.getNode2())) {
                        Node w;
                        if (!visitedNodes.contains(edge.getNode1())) w = edge.getNode1();
                        else w = edge.getNode2();
                        // explorar (v,w)
                        exploredEdges.add(edge);
                        highlightedEdges.add(edge);
                        // inserir w em Q
                        stack.add(w);
                        // marcar w
                        visitedNodes.add(w);
                        w.getEllipse2D().setFrame(50 - (radius / 2) + x * 100, 50 - (radius / 2) + y * 100, radius, radius);
                        x++;
                    } else if (!exploredEdges.contains(edge)) { //Senão Se (v,w) não explorada Então
                        // explorar (v,w)
                        exploredEdges.add(edge);
                    }
            }
        }
        mainPanel.validate();
        mainPanel.repaint();
    }
    
    public void DFS() {
        if (Nodes.size() == 0 || isDirected) return;

        // Seleciona o no de partida
        JPanel panel = new JPanel(new FlowLayout());
        String[] choices = new String[Nodes.size()];
        for (int i = 0; i < Nodes.size(); i++) {
            choices[i] = String.format("%02d", i) + ": " + Nodes.get(i).getName();
        }
        panel.add(new JLabel("Select starting node: "));
        JComboBox<String> comboBoxNode = new JComboBox<>(choices);
        panel.add(comboBoxNode);

        if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(this, panel, "DFS", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE)) {
            return;
        }

        highlightedEdges.clear();
        // escolher o vértice inicial v
        Node v = Nodes.get(comboBoxNode.getSelectedIndex());
        v.getEllipse2D().setFrame(50 - (radius / 2), 50 - (radius / 2), radius, radius);
        DFS(v, new ArrayList<>(), new ArrayList<>(), 0);
        mainPanel.validate();
        mainPanel.repaint();
    }

    private void DFS(Node v, ArrayList<Node> visitedNodes, ArrayList<Edge> exploredEdges, int y) {
        y++;
        int x = 0;
        // marcar v
        visitedNodes.add(v);
        // Enquanto existir w Є Ƭ(v) Faça
        for (Edge edge : Edges) {
            if (edge.getNode1() == v || edge.getNode2() == v) {
                // Se w é não marcado Então
                if (!visitedNodes.contains(edge.getNode1()) || !visitedNodes.contains(edge.getNode2())) {
                    Node w;
                    if (!visitedNodes.contains(edge.getNode1())) w = edge.getNode1();
                    else w = edge.getNode2();
                    // explorar (v,w)
                    exploredEdges.add(edge);
                    highlightedEdges.add(edge);
                    // marcar w
                    visitedNodes.add(w);
                    w.getEllipse2D().setFrame(50 - (radius / 2) + x * 100, 50 - (radius / 2) + y * 100, radius, radius);
                    x++;
                    //BP(w)
                    DFS(w, visitedNodes, exploredEdges, y);
                }
                // Senão
                if (!exploredEdges.contains(edge)) exploredEdges.add(edge);
            }
        }
    }

    public void increaseNodeSize() {
        if (radius < maxRadius) {
            radius += step;
            for (Node node : Nodes) {
                node.getEllipse2D().setFrame(node.getEllipse2D().getX() - (float) step / 2, node.getEllipse2D().getY() - (float) step / 2, radius, radius);
            }
            centerPanel.validate();
            centerPanel.repaint();
        }
    }

    public void decreaseNodeSize() {
        if (radius > minRadius) {
            radius -= step;
            for (Node node : Nodes) {
                node.getEllipse2D().setFrame(node.getEllipse2D().getX() + (float) step / 2, node.getEllipse2D().getY() + (float) step / 2, radius, radius);
            }
            centerPanel.validate();
            centerPanel.repaint();
        }
    }

    public void g1() {
        Nodes = new ArrayList<>();
        Edges = new ArrayList<>();
        highlightedEdges.clear();
        isDirected = false;

        Node node1, node2, node3, node4, node5, node6, node7;
        node1 = new Node("1", new Ellipse2D.Float(50 - (radius / 2), 50 - (radius / 2), radius, radius));
        node2 = new Node("2", new Ellipse2D.Float(50 - (radius / 2), 250 - (radius / 2), radius, radius));
        node3 = new Node("3", new Ellipse2D.Float(250 - (radius / 2), 150 - (radius / 2), radius, radius));
        node4 = new Node("4", new Ellipse2D.Float(450 - (radius / 2), 50 - (radius / 2), radius, radius));
        node5 = new Node("5", new Ellipse2D.Float(450 - (radius / 2), 250 - (radius / 2), radius, radius));
        Nodes.add(node1);
        Nodes.add(node2);
        Nodes.add(node3);
        Nodes.add(node4);
        Nodes.add(node5);

        Edge edge1, edge2, edge3, edge4, edge5, edge6;
        edge1 = new Edge("Edge1", node1, node2, 8);
        edge2 = new Edge("Edge2", node3, node1, 7);
        edge3 = new Edge("Edge3", node2, node3, 3);
        edge4 = new Edge("Edge4", node3, node4, 4);
        edge5 = new Edge("Edge5", node3, node5, 2);
        edge6 = new Edge("Edge6", node4, node5, 2);

        Edges.add(edge1);
        Edges.add(edge2);
        Edges.add(edge3);
        Edges.add(edge4);
        Edges.add(edge5);
        Edges.add(edge6);

        mainPanel.validate();
        mainPanel.repaint();
    }
    
    public void g2(){
        Nodes = new ArrayList<>();
        Edges = new ArrayList<>();
        highlightedEdges.clear();
        isDirected = true;

        Node node1, node2, node3, node4, node5, node6, node7, node8;
        node1 = new Node("1", new Ellipse2D.Float(50 - (radius / 2), 150 - (radius / 2), radius, radius));
        node2 = new Node("2", new Ellipse2D.Float(125 - (radius / 2), 50 - (radius / 2), radius, radius));
        node3 = new Node("3", new Ellipse2D.Float(125 - (radius / 2), 250 - (radius / 2), radius, radius));
        node4 = new Node("4", new Ellipse2D.Float(350 - (radius / 2), 250 - (radius / 2), radius, radius));
        node5 = new Node("5", new Ellipse2D.Float(350 - (radius / 2), 50 - (radius / 2), radius, radius));
        node6 = new Node("6", new Ellipse2D.Float(450 - (radius / 2), 150 - (radius / 2), radius, radius));
        node7 = new Node("7", new Ellipse2D.Float(200 - (radius / 2), 150 - (radius / 2), radius, radius));
        node8 = new Node("8", new Ellipse2D.Float(350 - (radius / 2), 150 - (radius / 2), radius, radius));
        Nodes.add(node1);
        Nodes.add(node2);
        Nodes.add(node3);
        Nodes.add(node4);
        Nodes.add(node5);
        Nodes.add(node6);
        Nodes.add(node7);
        Nodes.add(node8);

        Edge edge1, edge2, edge3, edge4, edge5, edge6, edge7, edge8, edge9, edge10, edge11, edge12;
        edge1 = new Edge("Edge1", node1, node2, 8);
        edge2 = new Edge("Edge2", node2, node7, 7);
        edge3 = new Edge("Edge3", node7, node3, 3);
        edge4 = new Edge("Edge4", node3, node1, 4);
        edge5 = new Edge("Edge5", node7, node8, 2);
        edge6 = new Edge("Edge6", node2, node5, 2);
        edge7 = new Edge("Edge7", node5, node8, 2);
        edge8 = new Edge("Edge8", node8, node4, 2);
        edge9 = new Edge("Edge9", node3, node4, 2);
        edge10 = new Edge("Edge10", node4, node6, 2);
        edge11 = new Edge("Edge11", node6, node5, 2);
        edge12 = new Edge("Edge12", node7, node1, 2);

        Edges.add(edge1);
        Edges.add(edge2);
        Edges.add(edge3);
        Edges.add(edge4);
        Edges.add(edge5);
        Edges.add(edge6);
        Edges.add(edge7);
        Edges.add(edge8);
        Edges.add(edge9);
        Edges.add(edge10);
        Edges.add(edge11);
        Edges.add(edge12);

        mainPanel.validate();
        mainPanel.repaint();


    }

    public void clear() {
        Nodes.clear();
        Edges.clear();
        highlightedEdges.clear();
        edgeCount = 0;
        nodeCount = 0;
        mainPanel.validate();
        mainPanel.repaint();
    }

    public class MyPanel extends JPanel {
        private Ellipse2D dragged;
        private Point offset;

        public MyPanel() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    for (Node node : Nodes) {
                        if (node.getEllipse2D().contains(e.getPoint())) {
                            dragged = node.getEllipse2D();
                            // Adjust for the different between the top/left corner of the
                            // node and the point it was clicked...
                            offset = new Point(node.getEllipse2D().getBounds().x - e.getX(), node.getEllipse2D().getBounds().y - e.getY());
                            // Highlight the clicked node
                            repaint();
                            break;
                        }
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    // Erase the "click" highlight
                    if (dragged != null) {
                        repaint();
                    }
                    dragged = null;
                    offset = null;
                }
            });

            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (dragged != null && offset != null) {
                        // Adjust the position of the drag point to allow for the
                        // click point offset
                        Point to = e.getPoint();
                        to.x += offset.x;
                        to.y += offset.y;
                        // Modify the position of the node...
                        Rectangle bounds = dragged.getBounds();
                        bounds.setLocation(to);
                        dragged.setFrame(bounds);
                        // repaint...
                        repaint();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            Stroke defaultStroke = g2d.getStroke();

            // desenha as arestas
            for (Edge edge : Edges) {
                if (highlightedEdges != null && highlightedEdges.contains(edge)) {
                    g2d.setColor(Color.BLUE);
                    g2d.setStroke(new BasicStroke(3));
                } else {
                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(defaultStroke);
                }
                // Se nao for um laço
                if (edge.getNode1() != edge.getNode2()) {
                    Point from = edge.getNode1().getEllipse2D().getBounds().getLocation();
                    from.x += radius / 2;
                    from.y += radius / 2;
                    Point to = edge.getNode2().getEllipse2D().getBounds().getLocation();
                    to.x += radius / 2;
                    to.y += radius / 2;
                    // Caso o grafo seja direcionado
                    if (isDirected) {
                        // desloca a linha
                        float angle = (float) Math.toDegrees(Math.atan2(to.y - from.y, to.x - from.x));
                        if (angle < 0) {
                            angle += 360;
                        }
                        angle = 360 - angle;
                        from.x += Math.sin(Math.toRadians(angle)) * radius / 2;
                        from.y += Math.cos(Math.toRadians(angle)) * radius / 2;
                        to.x += Math.sin(Math.toRadians(angle)) * radius / 2;
                        to.y += Math.cos(Math.toRadians(angle)) * radius / 2;
                        // desenha seta
                        double phi = Math.toRadians(40);
                        int barb = 10;
                        double dy = to.y - from.y;
                        double dx = to.x - from.x;
                        double ax = to.x - dx / 2;
                        double ay = to.y - dy / 2;
                        double theta = Math.atan2(dy, dx);
                        double x, y, rho = theta + phi;
                        for (int j = 0; j < 2; j++) {
                            x = ax - barb * Math.cos(rho);
                            y = ay - barb * Math.sin(rho);
                            g2d.draw(new Line2D.Double(ax, ay, x, y));
                            rho = theta - phi;
                        }
                    }
                    //
                    g2d.draw(new Line2D.Float(from, to));
                    // desenha o valor da aresta
                    FontMetrics fm = g.getFontMetrics();
                    String text = Integer.toString(edge.getWeight());
                    if (!text.equals("0"))
                        g2d.drawString(text, (from.x + to.x - fm.stringWidth(text)) / 2, (from.y + to.y - fm.getHeight()) / 2);
                } else {
                    Point circle = edge.getNode1().getEllipse2D().getBounds().getLocation();
                    circle.y -= radius / 4;
                    g2d.drawOval(circle.x, circle.y, radius, (int) (radius / 1.5));
                    // desenha o valor da aresta
                    FontMetrics fm = g.getFontMetrics();
                    String text = Integer.toString(edge.getWeight());
                    if (!text.equals("0"))
                        g2d.drawString(text, circle.x + (radius / 2) - fm.stringWidth(text) / 2, circle.y - fm.getHeight() / 2);
                    // desenha a seta
                    if (isDirected) {
                        int tx = circle.x + 5 + radius / 2;
                        int ty = circle.y;
                        double phi = Math.toRadians(40);
                        int barb = 10;
                        double dy = 0;
                        double dx = tx - circle.x;
                        double theta = Math.atan2(dy, dx);
                        double x, y, rho = theta + phi;
                        for (int j = 0; j < 2; j++) {
                            x = tx - barb * Math.cos(rho);
                            y = ty - barb * Math.sin(rho);
                            g2d.draw(new Line2D.Double(tx, ty, x, y));
                            rho = theta - phi;
                        }

                    }
                }
            }
            g2d.setStroke(defaultStroke);

            // desenha os vertices
            for (Node node : Nodes) {
                g2d.setColor(Color.pink);
                g2d.fill(node.getEllipse2D());
                if (node.getEllipse2D() == dragged) {
                    g2d.setColor(Color.RED);
                    g2d.draw(node.getEllipse2D());
                }
                g2d.setColor(Color.BLUE);
                FontMetrics fm = g.getFontMetrics();
                String text = node.getName();
                int textWidth = fm.stringWidth(text);
                int x = node.getEllipse2D().getBounds().x;
                int y = node.getEllipse2D().getBounds().y;
                int width = node.getEllipse2D().getBounds().width;
                int height = node.getEllipse2D().getBounds().height;
                g.drawString(text, x + ((width - textWidth)) / 2, y + ((height - fm.getHeight()) / 2) + fm.getAscent());
            }
            g2d.dispose();
        }
    }

}
