package utils;

import javafx.scene.Node;

public class NamedNode {
	private Node node;
	private String name;
	
	public NamedNode(Node node, String name) {
		this.node = node;
		this.name = name;
	}

	public Node getNode() {
		return node;
	}

	public String getName() {
		return name;
	}

}
