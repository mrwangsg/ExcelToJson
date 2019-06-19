package com.sgwang;

import java.util.*;

/**
 * @创建人 sgwang
 * @name MyTreeNode
 * @user 91119
 * @创建时间 2019/6/18
 * @描述
 */
public class Node {
    private String nameNode;                                // 节点名
    private String typeNode;                                // 节点类型 [attr obj arr]
    private List<Node> childrenNode = new ArrayList<Node>();   // 子节点
    private Set<String> arrSize = new TreeSet<String>(); // 仅针对数组类型节点 用于生成多个数组对象

    public Node() {
    }

    public Node(String nameNode, String typeNode) {
        this.nameNode = nameNode;
        this.typeNode = typeNode;
    }

    /**
     * @描述 根据nodeName 存在子节点返回 否则返回null
     * @参数 String nodeName
     * @返回值 Node | null
     */
    public Node getChildNodeName(String nodeName) {
        Iterator<Node> iterator = this.childrenNode.iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();

            if (node.getNameNode().equals(nodeName)) {
                return node;
            }
        }
        return null;
    }

    /**
     * @描述 根据nodeName 判断是否存在该子节点
     * @参数 String nodeName
     * @返回值 boolean
     */
    public boolean isExistChildNode(String nodeName) {
        Iterator<Node> iterator = this.childrenNode.iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();

            if (node.getNameNode().equals(nodeName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @描述 添加子节点
     * @参数 Node node
     * @返回值 void
     */
    public void addChildNode(Node node) {
        this.childrenNode.add(node);
    }


    public String getNameNode() {
        return nameNode;
    }

    public void setNameNode(String nameNode) {
        this.nameNode = nameNode;
    }

    public String getTypeNode() {
        return typeNode;
    }

    public void setTypeNode(String typeNode) {
        this.typeNode = typeNode;
    }

    public List<Node> getChildrenNode() {
        return childrenNode;
    }

    public void setChildrenNode(List<Node> childrenNode) {
        this.childrenNode = childrenNode;
    }

    public Set<String> getArrSize() {
        return arrSize;
    }

    public void setArrSize(Set<String> arrSize) {
        this.arrSize = arrSize;
    }
}
