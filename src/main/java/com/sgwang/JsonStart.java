package com.sgwang;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @创建人 sgwang
 * @name JsonStart
 * @user 91119
 * @创建时间 2019/6/21
 * @描述
 */
public class JsonStart {
    private static Node rootNode = new Node("root", "obj");
    private static String[] typeNode = new String[]{"attr", "obj", "arr"};

    public static void main(String[] args) throws Exception {
        String[] initStr = new String[]{
                "userName", "passWord",
                "innerDemo.innerName", "innerDemo.innerPass",
                "outDemo.outName", "outDemo.outPass",
                "listDemo[0].list.name", "listDemo[0].list.pass",
                "listDemo[1].list.name", "listDemo[1].list.pass",
        };

        Node rootNode = new Node("root", "obj");
        JSONObject rootObjJson = new JSONObject();
        handlerTask(initStr, rootNode, rootObjJson);

        System.out.println(rootObjJson.toJSONString());

    }


    /**
     * @描述 将字符串 转化为 树型结构
     * @参数 String[] initStr
     * @返回值 void
     */
    public static void handlerTask(String[] initStr, Node rootNode, JSONObject rootObjJson) {

        // 模拟循环 excel的列
        for (String indexStr : initStr) {
            // 切割字符串时 需要递归一下
            buildNodeTree(indexStr, rootNode);
        }

        levelIteratorTree(rootNode, rootObjJson, null);
    }

    /**
     * @描述 输入当前Node 和 当前切割的字符串
     * @参数 Node indexNode && String indexStr
     * @返回值 void
     */
    public static void buildNodeTree(String indexStr, Node indexNode) {

        // 存在"." 说明类型为 obj || arr
        if (indexStr.contains(".")) {
            String indexNodeName = "";                  // 当前节点 名称
            String indexNodeType = diffType(indexStr);  // 区别节点 类型
            Node nextNode = null;                       // 下一child节点
            String nextStr = "";                        // 下一child字符串

            if (indexNodeType == "arr") {
                indexNodeName = indexStr.substring(0, indexStr.indexOf("["));
                nextStr = indexStr.substring(indexStr.indexOf(".") + 1);
            } else if (indexNodeType == "obj") {
                indexNodeName = indexStr.substring(0, indexStr.indexOf("."));
                nextStr = indexStr.substring(indexStr.indexOf(".") + 1);
            }

            // 如果已经存在孩子节点 获取它 递归子节点
            // 如果不存在 先新建Node节点插入 再递归子节点
            if (indexNode.isExistChildNode(indexNodeName)) {
                nextNode = indexNode.getChildNodeName(indexNodeName);
            } else {
                nextNode = new Node(indexNodeName, indexNodeType);
                indexNode.addChildNode(nextNode);
            }

            if (indexNodeType == "arr") {
                // 用于记录 特别arr的 数组对象个数
                signIndexArrSize(nextNode, indexStr);
            }

            buildNodeTree(nextStr, nextNode);
        } else {
            // 不存在"." 说明已经是尽头
            if (indexNode.isExistChildNode(indexStr)) {
                // 如果已经存在不做处理
            } else {
                // 如果不存在 新建Node节点 插入
                indexNode.addChildNode(new Node(indexStr, "attr"));
            }
        }

    }

    /**
     * @描述 层级遍历树结构
     * @参数 Node indexNode
     * @返回值 void
     */
    public static void levelIteratorTree(Node indexNode, JSONObject indexJsonObj, JSONArray indexJsonArr) {
        //  这里可以做构建 json的处理
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("nameNode: " + indexNode.getNameNode() + "   typeNode: " + indexNode.getTypeNode());
        System.out.println("indexJsonObj: " + indexJsonObj + "   indexJsonArr: " + indexJsonArr);
        if (indexNode.getTypeNode() == "arr")
            System.out.println("arrSize: " + indexNode.getArrSize().size() + "个siez！");

        List<Node> childrenNode = indexNode.getChildrenNode();
        if (!childrenNode.isEmpty()) {
            Iterator<Node> iterator = childrenNode.iterator();
            while (iterator.hasNext()) {
                Node childNode = iterator.next();
                String nameNode = childNode.getNameNode();
                String typeNode = childNode.getTypeNode();

                if (indexJsonObj != null) {
                    if (typeNode == "attr") {
                        indexJsonObj.put(nameNode, typeNode);
                    } else if (typeNode == "obj") {
                        JSONObject nextObjJson = new JSONObject();

                        indexJsonObj.put(nameNode, nextObjJson);
                        levelIteratorTree(childNode, nextObjJson, null);
                    } else if (typeNode == "arr") {
                        JSONArray nextArrJson = new JSONArray();

                        indexJsonObj.put(nameNode, nextArrJson);
                        levelIteratorTree(childNode, null, nextArrJson);
                    }
                } else if (indexJsonArr != null) {
                    if (typeNode == "attr") {
                        JSONObject nextJsonObject = (JSONObject) indexJsonArr.get(0);

                        nextJsonObject.put(nameNode, typeNode);
                    } else if (typeNode == "obj") {
                        for (int index = 0; index < indexNode.getArrSize().size(); index++) {
                            JSONObject nextObjJson = new JSONObject();

                            indexJsonArr.add(nextObjJson);
                            levelIteratorTree(childNode, nextObjJson, null);
                        }
                    } else if (typeNode == "arr") {
                        JSONArray nextArrJson = new JSONArray();

                        indexJsonArr.add(nextArrJson);
                        levelIteratorTree(childNode, null, nextArrJson);
                    }
                }
            }
        }
    }

    /**
     * @描述 根据字符串 返回arr | obj | attr
     * @参数 String
     * @返回值 String 备注：arr | obj | attr
     */
    public static String diffType(String indexStr) {

        if (indexStr.contains(".")) {
            // 继续判断 obj | arr
            if (!indexStr.contains("]")) {
                return "obj";
            } else if (indexStr.indexOf(".") < indexStr.indexOf("]")) {
                return "obj";
            } else {
                return "arr";
            }
        } else {
            return "attr";
        }
    }

    /**
     * @描述 用于记录 特别arr的节点 数组对象个数
     * @参数 Node indexNode, String indexStr
     * @返回值 void
     */
    public static void signIndexArrSize(Node indexNode, String indexStr) {
        String signSize = indexStr.substring(indexStr.indexOf("["), indexStr.indexOf("]") + 1);
        Set<String> ret = indexNode.getArrSize();
        ret.add(signSize);
    }
}
